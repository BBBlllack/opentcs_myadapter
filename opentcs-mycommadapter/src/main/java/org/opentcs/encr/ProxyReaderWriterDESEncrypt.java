package org.opentcs.encr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用动态代理代理println和readLine方法实现通信对称加密
 */
public class ProxyReaderWriterDESEncrypt {

  private final static Logger LOGGER = LoggerFactory.getLogger(ProxyReaderWriterDESEncrypt.class);
  private final static String DES_KEY = "12345678";
  private final static String TRANSFORMATION = "DES/ECB/PKCS5Padding";
  private final static String ALGORITHM = "DES";
  private final static String CHARSET_NAME = "UTF-8";
  private final static DESKeySpec keySpec;
  private final static SecretKeyFactory keyFactory;
  private final static SecretKey key;
  private static final int IV_SIZE = 8;


  static {
    try {
      keySpec = new DESKeySpec(DES_KEY.getBytes());
      keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
      key = keyFactory.generateSecret(keySpec);
    }
    catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  // 加密方法
  public static String encrypt(String plainText) {
    // 生成密钥
    try {
      // 加密
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encrypted = cipher.doFinal(plainText.getBytes(CHARSET_NAME));
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
//    LOGGER.info("cipherText: {}", cipherText);
    try {
      // 解密
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
      return new String(decrypted, CHARSET_NAME);
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
