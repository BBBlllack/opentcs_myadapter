package org.opentcs.encr;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.opentcs.utils.RSAUtil;

public class ProxyReaderWriterNonEncrypt {

  // 代理Writer
  public static MyWriter createEncryptedWriter(PrintWriter writer) {
    return (MyWriter) Proxy.newProxyInstance(
        MyWriter.class.getClassLoader(),
        new Class<?>[]{MyWriter.class},
        (proxy, method, args) -> {
          if ("println".equals(method.getName()) && args.length == 1 && args[0] != null) {
            String encrypted = String.valueOf(args[0]);
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
            return reader.readLine();
          }
          return method.invoke(reader, args);
        }
    );
  }
}
