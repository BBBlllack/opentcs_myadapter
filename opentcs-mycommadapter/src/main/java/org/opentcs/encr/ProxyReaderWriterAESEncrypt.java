package org.opentcs.encr;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用动态代理代理 println 和 readLine 方法实现通信对称加密（AES）
 */
public class ProxyReaderWriterAESEncrypt {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProxyReaderWriterAESEncrypt.class);

  private static final String AES_KEY = "1234567890abcdef"; // 16字节密钥（128位）
  private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
  private static final String ALGORITHM = "AES";
  private static final SecretKeySpec KEY_SPEC;

  static {
    KEY_SPEC = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
  }

  // 加密
  public static String encrypt(String plainText) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, KEY_SPEC);
      byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      LOGGER.error("AES encryption error", e);
      return null;
    }
  }

  // 解密
  public static String decrypt(String cipherText) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, KEY_SPEC);
      byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception e) {
      LOGGER.error("AES decryption error", e);
      return null;
    }
  }

  // 代理 Writer
  public static MyWriter createEncryptedWriter(PrintWriter writer) {
    return (MyWriter) Proxy.newProxyInstance(
        MyWriter.class.getClassLoader(),
        new Class<?>[]{MyWriter.class},
        (proxy, method, args) -> {
          if ("println".equals(method.getName()) && args.length == 1 && args[0] != null) {
            String encrypted = encrypt(String.valueOf(args[0]));
            writer.println(encrypted);
            writer.flush();
            return null;
          }
          return method.invoke(writer, args);
        }
    );
  }

  // 代理 Reader
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
