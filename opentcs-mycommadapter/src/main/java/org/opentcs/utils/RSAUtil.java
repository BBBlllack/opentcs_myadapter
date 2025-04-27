package org.opentcs.utils;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtil {
  private final static String ALGORITHM = "RSA";
  private final static String ENCODE = "UTF-8";
  private final static int KEY_SIZE = 2048;

  public static KeyPair generateKeyPair() throws Exception {
    KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
    gen.initialize(KEY_SIZE);
    return gen.generateKeyPair();
  }

  public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] bytes = cipher.doFinal(plainText.getBytes(ENCODE));
    return Base64.getEncoder().encodeToString(bytes);
  }

  public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] bytes = Base64.getDecoder().decode(encryptedText);
    return new String(cipher.doFinal(bytes), ENCODE);
  }
}
