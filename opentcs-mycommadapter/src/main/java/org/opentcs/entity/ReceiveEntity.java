package org.opentcs.entity;

/**
 * 接收小车发来的响应消息对象
 */
public class ReceiveEntity {
  // 车辆当前执行的操作种类
  private VehicleOperation operation;
  // 车辆名称
  private String vehicleName;
  // 时间戳
  private Long timestamp;
  // 车辆状态, IDLE, CHARGING...
  private String state;
  // 电量
  private Double battery;
  // 当前位置
  private String position;
  // 下达的指令
  private Object instruction;
  // 指令完成反馈, failed, done
  // 如果是握手信息反馈, 此处放握手消息而不是指令
  private String instructionFeedBack;

  public ReceiveEntity() {
  }

  public ReceiveEntity(
      VehicleOperation operation, String vehicleName, Long timestamp, String state, Double battery,
      String position, Object instruction, String instructionFeedBack
  ) {
    this.operation = operation;
    this.vehicleName = vehicleName;
    this.timestamp = timestamp;
    this.state = state;
    this.battery = battery;
    this.position = position;
    this.instruction = instruction;
    this.instructionFeedBack = instructionFeedBack;
  }

  public VehicleOperation getOperation() {
    return operation;
  }

  public void setOperation(VehicleOperation operation) {
    this.operation = operation;
  }

  public String getVehicleName() {
    return vehicleName;
  }

  public void setVehicleName(String vehicleName) {
    this.vehicleName = vehicleName;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Double getBattery() {
    return battery;
  }

  public void setBattery(Double battery) {
    this.battery = battery;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public Object getInstruction() {
    return instruction;
  }

  public void setInstruction(Object instruction) {
    this.instruction = instruction;
  }

  public String getInstructionFeedBack() {
    return instructionFeedBack;
  }

  public void setInstructionFeedBack(String instructionFeedBack) {
    this.instructionFeedBack = instructionFeedBack;
  }

  @Override
  public String toString() {
    return "ReceiveEntity{" +
        "operation=" + operation +
        ", vehicleName='" + vehicleName + '\'' +
        ", timestamp=" + timestamp +
        ", state='" + state + '\'' +
        ", battery=" + battery +
        ", position='" + position + '\'' +
        ", instruction=" + instruction +
        ", instructionFeedBack='" + instructionFeedBack + '\'' +
        '}';
  }
}
