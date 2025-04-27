package org.opentcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.opentcs.encr.MyReader;
import org.opentcs.encr.MyWriter;
import org.opentcs.encr.ProxyReaderWriterDESEncrypt;

public class VehicleSocketTest {

  private final static int VEHICLE_SOCKET_PORT = 30000;
  private final static String VEHICLE_SOCKET_HOST = "127.0.0.1";

  @Test
  void test01()
      throws IOException {
    Socket socket = new Socket(VEHICLE_SOCKET_HOST, VEHICLE_SOCKET_PORT);
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
    writer.println("Vehicle-01");
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("input: ");
      String line = scanner.nextLine();
      if ("exit".equalsIgnoreCase(line)) {
        break;
      }
      writer.println(line);
    }
    writer.close();
    scanner.close();
    socket.close();
  }

  public static void main(String[] args)
      throws IOException {
    Socket socket = new Socket(VEHICLE_SOCKET_HOST, VEHICLE_SOCKET_PORT);
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
    MyWriter encryptedWriter
        = ProxyReaderWriterDESEncrypt.createEncryptedWriter(writer);
    encryptedWriter.println("DESVehicle-01");
    Scanner scanner = new Scanner(System.in);
//    try {
//      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//      String message;
//      while ((message = reader.readLine()) != null) {
////        System.out.println("wait...");
//        System.out.println(message);
//      }
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//    }
    new Thread(()->{
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        MyReader decryptedReader
            = ProxyReaderWriterDESEncrypt.createDecryptedReader(reader);
        String message;
        while ((message = decryptedReader.readLine()) != null) {
          System.out.println(String.format("recv from server: %s", message));
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    while (true) {
      System.out.print("input: ");
      String line = scanner.nextLine();
      if ("exit".equalsIgnoreCase(line)){
        break;
      }
      encryptedWriter.println(line);
    }
//    writer.close();
//    scanner.close();
//    socket.close();
  }

  @Test
  void test05() {
    String s = "hello";
    System.out.println(s.substring(0,3));
    System.out.println(s.substring(3,s.length()));
  }
}
