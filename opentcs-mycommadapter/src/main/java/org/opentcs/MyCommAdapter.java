package org.opentcs;

import static java.util.Objects.requireNonNull;
import static org.opentcs.constants.AdapterSocketConstants.*;
import static org.opentcs.MyCommAdapterMessages.*;
import static org.opentcs.constants.VehicleSocketConstants.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opentcs.common.LoopbackAdapterConstants;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.LoadHandlingDevice;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapterMessage;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.encr.ProxyReaderWriterDESEncrypt;
import org.opentcs.encr.ProxyReaderWriterNonEncrypt;
import org.opentcs.encr.ProxyReaderWriterRSAEncrypt;
import org.opentcs.encr.ProxyReaderWriterSM4Encrypt;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.MapValueExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opentcs.encr.MyWriter;
import org.opentcs.encr.MyReader;

public class MyCommAdapter
    extends BasicVehicleCommAdapter {

  // 处理车辆通信的线程池
  private final static ScheduledExecutorService myExecutor = Executors.newScheduledThreadPool(5);
  private final static Logger LOGGER = LoggerFactory.getLogger(MyCommAdapter.class);
  public static final String LHD_NAME = "mydefault";
  private final static Map<String, VehicleProcessModel> processModelMap = new HashMap<>();
  private final static Map<String, MyCommAdapter> myCommAdapterMap = new HashMap<>();
  private final static Map<String, Socket> vehicleSocketMap = new HashMap<>();
  private Socket vehicleSocketClient;
  private MyReader socketReader;
  private MyWriter socketWriter;
  private final VehicleProcessModel processModel;
  private final Vehicle vehicle;
  private final MapValueExtractor mapValueExtractor;
  private boolean initialized;
  private boolean isVehicleConnected = false;
  private final static int ADAPTER_SOCKET_PROT = 25565;
  private final static int VEHICLE_SOCKET_PORT = 30000;
  private static volatile boolean isAdapterSocketStarted = false;
  private static volatile boolean isVehicleSocketStarted = false;
  private final static ObjectMapper objectMapper = new ObjectMapper();
  private static final int SIMULATION_PERIOD = 100;

  /**
   * Creates a new instance.
   *
   * @param vehicleModel An observable model of the vehicle's and its comm adapter's attributes.
   * @param commandsCapacity The number of commands this comm adapter accepts. Must be at least 1.
   * @param rechargeOperation The string to recognize as a recharge operation.
   * @param executor The executor to run tasks on.
   */
  @Inject
  public MyCommAdapter(
      @Assisted
      VehicleProcessModel vehicleModel,
      @Assisted
      int commandsCapacity,
      String rechargeOperation,
      @Assisted
      Vehicle vehicle,
      MapValueExtractor mapValueExtractor,
      @KernelExecutor
      ScheduledExecutorService executor
  ) {
    // AdapterFactory -> ComponentFactory -> adapter
    super(vehicleModel, commandsCapacity, rechargeOperation, executor);
    this.mapValueExtractor = requireNonNull(mapValueExtractor, "mapValueExtractor");
    this.processModel = vehicleModel;
    this.vehicle = vehicle;
  }

  @SuppressWarnings("all")
  public synchronized void startAdapterSocket() {
    if (isAdapterSocketStarted) return;
    new Thread(() -> {
      try {
        ServerSocket socket = new ServerSocket(ADAPTER_SOCKET_PROT);
        isAdapterSocketStarted = true;
        LOGGER.info("MyCommAdapter Socket initialized on port {}", ADAPTER_SOCKET_PROT);
        while (true) {
          Socket client = socket.accept();
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(client.getInputStream()));
          // 获取加密类
          MyReader decryptedReader
              = ProxyReaderWriterDESEncrypt.createDecryptedReader(reader);
          String message;
          while ((message = decryptedReader.readLine()) != null) {
            LOGGER.info("recv client message: " + message);
            if ("exit".equals(message)) {
              client.close();
              socket.close();
              LOGGER.info("MyCommAdapter Socket closed");
              break;
            }
            Map<?, ?> map = objectMapper.readValue(message, Map.class);
            String type = (String) map.get("type");
            String data = (String) map.get("data");
            String name = (String) map.get("name");
            VehicleProcessModel currentProcessModel = requireNonNull(
                processModelMap.get(name),
                "Vehicle not exist"
            );
            if (type == null || data == null || name == null) {
              return;
            }
            switch (type) {
              case SOCKET_SET_POSITION -> currentProcessModel.setPosition(data);
              case SOCKET_SET_STATE -> currentProcessModel.setState(Vehicle.State.valueOf(data));
              default -> {
                LOGGER.warn("AdapterSocket type {} not exist!", type);
              }
            }
          }
        }
      }
      catch (IOException e) {
        LOGGER.error("Could not open MyCommAdapter Socket", e);
      }
    }).start();
    isAdapterSocketStarted = true;
  }

  public synchronized void startVehicleServerSocket() {
    if (isVehicleSocketStarted) return;
    new Thread(() -> {
      try {
        ServerSocket serverSocket = new ServerSocket(VEHICLE_SOCKET_PORT);
        LOGGER.info("VehicleServerSocket Socket started on port: {}", VEHICLE_SOCKET_PORT);
        isVehicleSocketStarted = true;
        while (true) {
          Socket client = serverSocket.accept();
          LOGGER.info("VehicleServerSocket accepted one client");
          myExecutor.submit(() -> {
            try {
              BufferedReader reader = new BufferedReader(
                  new InputStreamReader(client.getInputStream()));
              PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
              MyReader decryptedReader
                  = ProxyReaderWriterDESEncrypt.createDecryptedReader(reader);
              MyWriter decryptedWriter = ProxyReaderWriterDESEncrypt.createEncryptedWriter(writer);
              // 协商加密方式, 车辆通告服务器自己名称
              String interactData = reader.readLine();
              // 获取协商后的加密方式
              String encryptMethod = interactData.substring(0, 3);
              // 获取车辆名称
              String vehicleName = interactData.substring(3);
              LOGGER.info("encryptMethod: {}, Vehicle Name: {}", encryptMethod, vehicleName);
              MyCommAdapter vehicleAdapter = myCommAdapterMap.get(vehicleName);
              if (vehicleAdapter != null) {
                vehicleSocketMap.put(vehicleName, client);
//                根据协商的加密方式做对应处理
                switch (encryptMethod) {
                  case ENCRYPT_METHOD_NONE -> handleNone(vehicleAdapter, reader, writer);
                  case ENCRYPT_METHOD_DES -> handleDES(vehicleAdapter, reader, writer);
                  case ENCRYPT_METHOD_RSA -> handleRSA(vehicleAdapter, reader, writer);
                  case ENCRYPT_METHOD_SM4 -> handleSM4(vehicleAdapter, reader, writer);
                  default -> {
                  }
                }
//                设置通信的socket,Reader,Writer
                vehicleAdapter.setVehicleSocketClient(client);
//                设置当前适配器为连接到物理车辆状态
                vehicleAdapter.connectVehicle();
              }
              else {
                LOGGER.error("{} not exist", vehicleName);
              }
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          });
        }
      }
      catch (Exception e) {
        LOGGER.error("Could not start VehicleServerSocket: ", e);
      }
    }).start();
    isVehicleSocketStarted = true;
  }

  public void handleNone(MyCommAdapter adapter, BufferedReader reader, PrintWriter writer) {
    adapter.setSocketReader(ProxyReaderWriterNonEncrypt.createDecryptedReader(reader));
    adapter.setSocketWriter(ProxyReaderWriterNonEncrypt.createEncryptedWriter(writer));
    adapter.getSocketWriter().println("NON");
  }

  public void handleDES(MyCommAdapter adapter, BufferedReader reader, PrintWriter writer) {
    adapter.setSocketReader(ProxyReaderWriterDESEncrypt.createDecryptedReader(reader));
    adapter.setSocketWriter(ProxyReaderWriterDESEncrypt.createEncryptedWriter(writer));
    adapter.getSocketWriter().println("DES");
  }

  public void handleRSA(MyCommAdapter adapter, BufferedReader reader, PrintWriter writer) {
    adapter.setSocketReader(ProxyReaderWriterRSAEncrypt.createDecryptedReader(reader, null));
    adapter.setSocketWriter(ProxyReaderWriterRSAEncrypt.createEncryptedWriter(writer, null));
  }

  public void handleSM4(MyCommAdapter adapter, BufferedReader reader, PrintWriter writer){
    adapter.setSocketReader(ProxyReaderWriterSM4Encrypt.createDecryptedReader(reader));
    adapter.setSocketWriter(ProxyReaderWriterSM4Encrypt.createEncryptedWriter(writer));
    adapter.getSocketWriter().println("SM4");
  }

  @Override
  public void initialize() {
    LOGGER.info("Initializing MyCommAdapter Vehicle: {}", vehicle.getName());
    startAdapterSocket();
    startVehicleServerSocket();
    if (isInitialized()) {
      return;
    }
    processModelMap.put(vehicle.getName(), processModel);
    myCommAdapterMap.put(vehicle.getName(), this);
    super.initialize();
    String initialPos
        = vehicle.getProperties().get(LoopbackAdapterConstants.PROPKEY_INITIAL_POSITION);
//    if (initialPos != null) {
//      initVehiclePosition(initialPos);
//    }
    String position = getProcessModel().getPosition();
    LOGGER.info("init position: {}", position);
    getProcessModel().setState(Vehicle.State.IDLE);
    getProcessModel().setLoadHandlingDevices(
        Arrays.asList(new LoadHandlingDevice(LHD_NAME, false))
    );
    initialized = true;
    LOGGER.info("MyCommAdapter {} has been initialized", vehicle.getName());
  }

  @Override
  public boolean isInitialized() {
    return this.initialized;
  }

  @Override
  public synchronized void enable() {
    LOGGER.info("Enabling MyCommAdapter");
    if (!isEnabled()) {
      super.enable();
    }
  }

  @Override
  public synchronized void disable() {
    LOGGER.info("disabling MyCommAdapter");
    if (isEnabled()) {
      super.disable();
    }
  }

  @Override
  public synchronized boolean isEnabled() {
    return super.isEnabled();
  }

  @Override
  protected void connectVehicle() {
    LOGGER.info("connectVehicle {}", vehicle.getName());
    if (getVehicleSocketClient() != null) {
      this.isVehicleConnected = true;
    }
  }

  @Override
  protected void disconnectVehicle() {
    LOGGER.info("disconnectVehicle {}", vehicle.getName());
    this.isVehicleConnected = false;
  }

  @Override
  protected boolean isVehicleConnected() {
    return this.isVehicleConnected;
  }

  @Nonnull
  @Override
  public ExplainedBoolean canProcess(
      @Nonnull
      TransportOrder order
  ) {
    LOGGER.info("canProcess: {}", order);
    return new ExplainedBoolean(true, "canProcess");
  }

  @Override
  public void onVehiclePaused(boolean paused) {
    LOGGER.info("onVehiclePaused: {}", paused);
  }

  // TODO sendCommand
  @Override
  public synchronized void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {
    requireNonNull(cmd, "cmd");
    VehicleProcessModel model = getProcessModel();
    LOGGER.info("{} MovementCommand: {}", model.getName(), cmd.toString());
    if (getSentCommands().isEmpty()) {
      ((ExecutorService) getExecutor()).submit(() -> startVehicleSimulation(cmd));
    }
    else {
      ((ExecutorService) getExecutor()).submit(
          () -> startVehicleSimulation(getSentCommands().peek())
      );
    }
  }

  public void sendCommandToVehicle(MovementCommand command) {
    if (isVehicleConnected()) {
      PrintWriter writer;
      try {
//      writer = new PrintWriter(vehicleSocketMap.get(vehicle.getName()).getOutputStream(), true);
        writer = new PrintWriter(getVehicleSocketClient().getOutputStream(), true);
        MyWriter encryptedWriter
            = ProxyReaderWriterDESEncrypt.createEncryptedWriter(writer);
        getSocketWriter().println(command);
        LOGGER.info("MovementCommand {} was sent: ", command);
      }
      catch (Exception e) {
        LOGGER.error("{} {}", vehicle.getName(), e.getMessage());
        if (e instanceof IOException) {
//         socket 中断, 强制结束订单
          getProcessModel().setState(Vehicle.State.IDLE);
        }
      }
    }
    else {
      LOGGER.warn("{} not connected", vehicle.getName());
    }
  }

  private void startVehicleSimulation(MovementCommand command) {
    if (command == null) {
      return;
    }
    VehicleProcessModel model = getProcessModel();
    LOGGER.debug("Starting {} simulation for command: {}", model.getName(), command);
    model.setState(Vehicle.State.EXECUTING);
    Route.Step step = command.getStep();

    LOGGER.info("Step: {}", step);
    if (step == null) {
      LOGGER.info("Starting operation simulation load cargo...");
    }
    else {
      getExecutor().schedule(
          () -> movementSimulation(command),
          SIMULATION_PERIOD,
          TimeUnit.MILLISECONDS
      );
    }
  }

  /**
   * 通知内核指令完成并且开始执行下一条指令
   *
   * @param command
   */
  public void finishedCurrentAndStartNextCommand(MovementCommand command) {
    VehicleProcessModel model = getProcessModel();
    // 收到回复后 -> UI界面更新车辆状态
    model.setPosition(command.getStep().toString());
    if (getSentCommands().size() <= 1 && getUnsentCommands().isEmpty()) {
      getProcessModel().setState(Vehicle.State.IDLE);
    }
    if (Objects.equals(getSentCommands().peek(), command)) {
      // Let the comm adapter know we have finished this command.
      getProcessModel().commandExecuted(getSentCommands().poll());
    }
    ((ExecutorService) getExecutor()).submit(
        () -> startVehicleSimulation(getSentCommands().peek())
    );
  }

  public void handleOperation(MovementCommand command) {
    LOGGER.info("{} operation...", vehicle.getName());
    if (isVehicleConnected()) {
      myExecutor.submit(() -> {
        try {
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(getVehicleSocketClient().getInputStream()));
          MyReader decryptedReader
              = ProxyReaderWriterDESEncrypt.createDecryptedReader(reader);
          String message;
          while ((message = getSocketReader().readLine()) != null) {
            LOGGER.info("recv from {} client: {}", vehicle.getName(), message);
            if ("done".equals(message)) {
              finishedCurrentAndStartNextCommand(command);
              break;
            }
          }
        }
        catch (IOException e) {
          LOGGER.error("{} {}", vehicle.getName(), e.getMessage());
        }
      });
    }
    else {
      finishedCurrentAndStartNextCommand(command);
    }
  }

  private void movementSimulation(
      @Nonnull
      MovementCommand command
  ) {
    VehicleProcessModel model = getProcessModel();
    // 下发指令给下游机器
    sendCommandToVehicle(command);
    if (!command.hasEmptyOperation()) {
      // 模拟耗时操作
      getExecutor().schedule(
          () -> handleOperation(command),
          SIMULATION_PERIOD,
          TimeUnit.MILLISECONDS
      );
    }
    else {
      if (isVehicleConnected()) {
        myExecutor.submit(() -> {
          try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(getVehicleSocketClient().getInputStream()));
            MyReader decryptedReader
                = ProxyReaderWriterDESEncrypt.createDecryptedReader(reader);
            String message;
            while ((message = getSocketReader().readLine()) != null) {
              LOGGER.info("recv from {} client: {}", vehicle.getName(), message);
              if ("done".equalsIgnoreCase(message)) {
                finishedCurrentAndStartNextCommand(command);
                break;
              }
              if ("disconnect".equalsIgnoreCase(message)) {
                getProcessModel().setState(Vehicle.State.IDLE);
                getVehicleSocketClient().close();
                this.disconnectVehicle();
              }
            }
          }
          catch (IOException e) {
            LOGGER.error("{} {}", vehicle.getName(), e.getMessage());
          }
        });
      }
      else {
        finishedCurrentAndStartNextCommand(command);
      }
    }
  }

  @Override
  public void processMessage(
      @Nonnull
      VehicleCommAdapterMessage message
  ) {
    super.processMessage(message);
    LOGGER.info("{} processMessage: {}", vehicle.getName(), message.getType());
    switch (message.getType()) {

      case INIT_POSITION -> handleInitPosition(message);
      case CURRENT_MOVEMENT_COMMAND_FAILED -> handleCurrentMovementCommandFailed(message);
//      case PUBLISH_EVENT -> handlePublishEvent(message);
//      case SET_ENERGY_LEVEL -> handleSetEnergyLevel(message);
//      case SET_LOADED -> handleSetLoaded(message);
//      case SET_ORIENTATION_ANGLE -> handleSetOrientationAngle(message);
      case SET_POSITION -> handleSetPosition(message);
//      case RESET_POSITION -> handleResetPosition(message);
//      case SET_PRECISE_POSITION -> handleSetPrecisePosition(message);
//      case RESET_PRECISE_POSITION -> handleResetPrecisePosition(message);
      case SET_STATE -> handleSetState(message);
//      case SET_PAUSED -> handleSetPaused(message);
//      case SET_PROPERTY -> handleSetProperty(message);
//      case RESET_PROPERTY -> handleResetProperty(message);
//      case SET_SINGLE_STEP_MODE_ENABLED -> handleSetSingleStepModeEnabled(message);
//      case TRIGGER_SINGLE_STEP -> handleTriggerSingleStep(message);
      default -> {
        // Do nothing.
        LOGGER.warn("{} not exist!", message.getType());
      }
    }
  }

  private void handleCurrentMovementCommandFailed(VehicleCommAdapterMessage message) {
    MovementCommand failedCommand = getSentCommands().peek();
    if (failedCommand != null) {
      getProcessModel().commandFailed(failedCommand);
    }
  }

  private void handleInitPosition(VehicleCommAdapterMessage message) {
    mapValueExtractor.extractString(
            INIT_POSITION_PARAM_POSITION,
            message.getParameters()
        )
        .ifPresent(position -> {
          ((ExecutorService) getExecutor()).submit(() -> getProcessModel().setPosition(position));
        });
  }

  private void handleSetPosition(VehicleCommAdapterMessage message) {
    mapValueExtractor.extractString(SET_POSITION_PARAM_POSITION, message.getParameters())
        .ifPresent(position -> getProcessModel().setPosition(position));
  }

  private void handleSetState(VehicleCommAdapterMessage message) {
    mapValueExtractor.extractEnum(
            SET_STATE_PARAM_STATE,
            message.getParameters(),
            Vehicle.State.class
        )
        .ifPresent(state -> getProcessModel().setState(state));
  }

  @Override
  @Deprecated
  public void processMessage(
      @Nullable
      Object message
  ) {
    LOGGER.info("processMessage: {}", message);
  }

  public void setVehicleSocketClient(Socket vehicleSocketClient) {
    this.vehicleSocketClient = vehicleSocketClient;
  }

  public Socket getVehicleSocketClient() {
    return this.vehicleSocketClient;
  }

  public void setSocketReader(MyReader socketReader) {
    this.socketReader = socketReader;
  }

  public MyReader getSocketReader() {
    return this.socketReader;
  }

  public void setSocketWriter(MyWriter socketWriter) {
    this.socketWriter = socketWriter;
  }

  public MyWriter getSocketWriter() {
    return this.socketWriter;
  }
}
