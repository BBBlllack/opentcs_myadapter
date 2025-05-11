package org.opentcs.encr;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class EncryDecry {
  abstract String encrypt(final String plainText);

  abstract String decrypt(final String cipherText);

  MyWriter createEncryptedWriter(PrintWriter writer) {
    return null;
  }

  MyWriter createEncryptedWriter(PrintWriter writer, PublicKey publicKey) {
    return null;
  }

  MyReader createDecryptedReader(BufferedReader reader){
    return null;
  }

  MyReader createDecryptedReader(BufferedReader reader, PrivateKey privateKey){
    return null;
  }
}
