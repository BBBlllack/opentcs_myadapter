package org.opentcs;

import com.google.errorprone.annotations.Var;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

public class SocketTest {

  ExecutorService executor = Executors.newFixedThreadPool(5);

  private final static int PORT = 20014;

  @Test
  void test01()
      throws IOException {
    ServerSocket serverSocket = new ServerSocket(PORT);
    while (true){
      Socket client = serverSocket.accept();
      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      executor.submit(()->{
        try {
          String message;
          while ((message = reader.readLine()) != null){
            System.out.println(Thread.currentThread().getName() + ":" + message);
          }
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
      executor.submit(() -> {
        try {
          String message;
          while ((message = reader.readLine()) != null){
            System.out.println(Thread.currentThread().getName() + ":" + message);
          }
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  public static void main(String[] args)
      throws IOException {
    Socket socket = new Socket("127.0.0.1", PORT);
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
    Scanner scanner = new Scanner(System.in);
    while (true){
      String s = scanner.nextLine();
      writer.println(s);
      if ("done".equalsIgnoreCase(s)){
        break;
      }
    }
    writer.close();
    scanner.close();
    socket.close();
  }
}
