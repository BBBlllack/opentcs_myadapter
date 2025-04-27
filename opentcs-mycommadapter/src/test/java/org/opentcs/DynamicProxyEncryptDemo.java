package org.opentcs;

import java.io.*;
import java.lang.reflect.*;
import java.util.Base64;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DynamicProxyEncryptDemo {

  private static final String DES_KEY = "12345678";

  // 自定义接口
  public interface MyWriter {
    void println(String s);
  }

  public interface MyReader {
    String readLine()
        throws IOException;
  }

  // 加密方法
  public static String encrypt(String plainText) {
    // 生成密钥
    try {
      DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey key = keyFactory.generateSecret(keySpec);

      // 加密
      Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
      // 转成 Base64 便于存储或传输
      return Base64.getEncoder().encodeToString(encrypted);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // 解密方法
  public static String decrypt(String cipherText) {
    try {
      // 生成密钥
      DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey key = keyFactory.generateSecret(keySpec);

      // 解密
      Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));

      return new String(decrypted, "UTF-8");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // 代理Writer
  public static MyWriter createEncryptedWriter(PrintWriter writer) {
    return (MyWriter) Proxy.newProxyInstance(
        MyWriter.class.getClassLoader(),
        new Class<?>[]{MyWriter.class},
        (proxy, method, args) -> {
          if ("println".equals(method.getName()) && args.length == 1 && args[0] instanceof String) {
            String encrypted = encrypt((String) args[0]);
            writer.println(encrypted);
            writer.flush(); // 确保写入
            return null;
          }
          return method.invoke(writer, args);
        }
    );
  }

  // 代理Reader
  public static MyReader createDecryptedReader(BufferedReader reader) {
    return (MyReader) Proxy.newProxyInstance(
        MyReader.class.getClassLoader(),
        new Class<?>[]{MyReader.class},
        (proxy, method, args) -> {
          if ("readLine".equals(method.getName())) {
            String line = reader.readLine();
            return line != null ? decrypt(line) : null;
          }
          return method.invoke(reader, args);
        }
    );
  }

  // 测试
  public static void main(String[] args)
      throws IOException {
    String filename
        = "C:\\Users\\13273\\IdeaProjects\\opentcs\\opentcs-mycommadapter\\src\\encryTxt";
    // 写入加密内容
    try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
      MyWriter writer = createEncryptedWriter(pw);
      writer.println("Hello World!");
      writer.println("Java 动态代理测试");
    }

    // 读取解密内容
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      MyReader reader = createDecryptedReader(br);
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println("Decrypted: " + line);
      }
    }
  }
}
