package org.opentcs.smTest;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.symmetric.SM4;
import org.junit.jupiter.api.Test;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.KeyUtil;

import java.security.KeyPair;
import org.opentcs.entity.SendEntity;

public class SM4Test {

  @Test
  void test01(){
    byte[] key = "0123456789abcdef".getBytes(); // 16字节密钥
    SM4 sm4 = SmUtil.sm4(key);

    String content = "Hello, SM4!";
//    String encryptHex = sm4.encryptHex(content);
    String encryptHex = sm4.encryptBase64(content);
    System.out.println("加密：" + encryptHex);

//    String decryptStr = sm4.decryptStr(encryptHex);
    String decryptStr = sm4.decryptStr(encryptHex);
    System.out.println("解密：" + decryptStr);
  }

  @Test
  void test02(){
    // 1. 生成密钥对
    KeyPair keyPair = KeyUtil.generateKeyPair("SM2");

    SM2 sm2 = new SM2(keyPair.getPrivate(), keyPair.getPublic());

    String text = "Hello SM2";

    // 2. 加密（默认使用公钥）
    String encrypted = sm2.encryptBcd(text, KeyType.PublicKey);
    System.out.println("Encrypted: " + encrypted);

    // 3. 解密（使用私钥）
    String decrypted = sm2.decryptStr(encrypted, KeyType.PrivateKey);
    System.out.println("Decrypted: " + decrypted);

//    // 4. 签名
//    String sign = sm2.signHex(text);
//    System.out.println("Signature: " + sign);
//
//    // 5. 验签
//    boolean verify = sm2.verifyHex(text, sign);
//    System.out.println("Verify: " + verify);

  }
}
