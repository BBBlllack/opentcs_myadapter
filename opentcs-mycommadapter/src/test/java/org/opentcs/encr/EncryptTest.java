package org.opentcs.encr;

import org.junit.jupiter.api.Test;

public class EncryptTest {

  @Test
  void test01(){
    String msg = "hello";
    String encrypt = ProxyReaderWriterDESEncrypt.encrypt(msg);
    System.out.println(encrypt);

    String msg1 = "On5DZtd3QSM=";
    String decrypt = ProxyReaderWriterDESEncrypt.decrypt(msg1);
    System.out.println(decrypt);
  }

}
