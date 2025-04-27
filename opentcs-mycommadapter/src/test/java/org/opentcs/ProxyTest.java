package org.opentcs;

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

public class ProxyTest {

  ExecutorService executor = Executors.newFixedThreadPool(5);

  private final static int PORT = 20014;

  @Test
  void test01()
      throws IOException {
    ServerSocket serverSocket = new ServerSocket(PORT);
    while (true){
      Socket client = serverSocket.accept();
      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      DynamicProxyEncryptDemo.MyReader decryptedReader
          = DynamicProxyEncryptDemo.createDecryptedReader(reader);
      executor.submit(()->{
        try {
          String message;
          while ((message = decryptedReader.readLine()) != null){
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
    DynamicProxyEncryptDemo.MyWriter encryptedWriter
        = DynamicProxyEncryptDemo.createEncryptedWriter(writer);
    Scanner scanner = new Scanner(System.in);
    while (true){
      String s = scanner.nextLine();
      encryptedWriter.println(s);
      if ("done".equalsIgnoreCase(s)){
        break;
      }
    }
    writer.close();
    scanner.close();
    socket.close();
  }
}
