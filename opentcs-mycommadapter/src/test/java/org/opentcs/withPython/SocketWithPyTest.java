package org.opentcs.withPython;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

public class SocketWithPyTest {

  private final static ExecutorService executor = Executors.newFixedThreadPool(10);

  String host = "127.0.0.1";
  int port = 40000;

  @Test
  void test01()
      throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);

    Scanner scanner = new Scanner(System.in);

    while (true) {
      Socket client = serverSocket.accept();

      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
      executor.submit(() -> {
        try {
          String message;
          while ((message = reader.readLine()) != null) {
            System.out.println("recv: " + message);
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      });
      executor.submit(() -> {
        int count = 0;
        while (true) {
          writer.println("你好 client: " + count++);
          Thread.sleep(1000);
        }
      });
    }

  }

}
