package org.opentcs.encr;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.lang.reflect.*;
import org.opentcs.utils.RSAUtil;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.*;

public class ProxyReaderWriterRSAEncrypt {

  public static MyWriter createEncryptedWriter(PrintWriter original, PublicKey publicKey) {
    return (MyWriter) Proxy.newProxyInstance(
        MyWriter.class.getClassLoader(),
        new Class<?>[]{MyWriter.class},
        (proxy, method, args) -> {
          if ((method.getName().equals("write") || method.getName().equals("println"))
              && args.length == 1 && args[0] instanceof String) {
            String originalText = (String) args[0];
            String encrypted = RSAUtil.encrypt(originalText, publicKey);
            return method.invoke(original, encrypted);  // 写加密后的内容
          }
          return method.invoke(original, args);  // 其它方法照常执行
        }
    );
  }

  public static MyReader createDecryptedReader(
      BufferedReader original, PrivateKey privateKey) {
    return (MyReader) Proxy.newProxyInstance(
        MyReader.class.getClassLoader(),
        new Class<?>[]{MyReader.class},
        (proxy, method, args) -> {
          if (method.getName().equals("readLine") && args == null) {
            String encrypted = original.readLine();
            if (encrypted == null) return null;
            return RSAUtil.decrypt(encrypted, privateKey); // 解密后返回
          }
          return method.invoke(original, args);
        }
    );
  }
}
