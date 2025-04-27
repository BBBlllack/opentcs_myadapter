package org.opentcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.opentcs.data.model.Vehicle;
import org.opentcs.encr.ProxyReaderWriterDESEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterSocketTest {

  private final static Logger LOGGER = LoggerFactory.getLogger(AdapterSocketTest.class);

  static int PORT = 25565;

  private static ObjectMapper mapper = new ObjectMapper();

  @Test
  void test01()
      throws IOException {

//    Socket socket = new Socket("127.0.0.1", PORT);
//    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//    ProxyReaderWriterDESEncrypt.MyWriter encryptedWriter
//        = ProxyReaderWriterDESEncrypt.createEncryptedWriter(out);
//    HashMap<String, String> dataMap = new HashMap<>();
////    dataMap.put("type", "setPosition");
//    dataMap.put("type", "setState");
////    dataMap.put("data", "Point-0003");
//    dataMap.put("data", "IDLE");
//    dataMap.put("name", "Vehicle-01");
//    encryptedWriter.println(mapper.writeValueAsString(dataMap));
//    out.close();
//    socket.close();
  }

  @Test
  void TEST04()
      throws IOException {
    Socket socket = new Socket("127.0.0.1", PORT);
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    HashMap<String, String> dataMap = new HashMap<>();
    dataMap.put("type", "setPosition");
    dataMap.put("data", "Point-0001");
    dataMap.put("name", "Vehicle-01");
    out.println(mapper.writeValueAsString(dataMap));
    out.close();
    socket.close();
  }

  @Test
  void test02()
      throws IOException {
    int port = PORT;
    ServerSocket socket = new ServerSocket(port);
    LOGGER.info("MyCommAdapter Socket initialized on port {}", port);
    while (true) {
      Socket client = socket.accept();
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(client.getInputStream()));
      String message;
      while ((message = reader.readLine()) != null) {
        if ("exit".equals(message)) {
          client.close();
          socket.close();
          break;
        }
        LOGGER.info("recv client message: " + message);
      }
    }
  }

  @Test
  void test03(){
    Vehicle.State unknown = Vehicle.State.valueOf("UNKNOWN");
    System.out.println(unknown);
  }
}
