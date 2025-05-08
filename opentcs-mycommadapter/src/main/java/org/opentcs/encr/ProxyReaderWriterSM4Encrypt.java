package org.opentcs.encr;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;

public class ProxyReaderWriterSM4Encrypt {
  private final static byte[] SECRET_KEY = "0123456789abcdef".getBytes();
  private final static SM4 sm4 = SmUtil.sm4(SECRET_KEY);

  public static String encrypt(String plainText) {
    return sm4.encryptBase64(plainText);
  }

  public static String decrypt(String decryptText) {
    return sm4.decryptStr(decryptText);
  }

  // 代理Writer
  public static MyWriter createEncryptedWriter(PrintWriter writer) {
    return (MyWriter) Proxy.newProxyInstance(
        MyWriter.class.getClassLoader(),
        new Class<?>[]{MyWriter.class},
        (proxy, method, args) -> {
          if ("println".equals(method.getName()) && args.length == 1 && args[0] != null) {
            String encrypted = encrypt(String.valueOf(args[0]));
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
}
