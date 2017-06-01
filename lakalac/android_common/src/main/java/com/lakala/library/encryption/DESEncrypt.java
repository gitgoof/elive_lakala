package com.lakala.library.encryption;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public abstract class DESEncrypt {

    /**
     * 密钥算法
     */
    public static final String KEY_ALGORITHM = "DESede";

    /**
     * 加密/解密算法 / 工作模式 / 填充方式
     * Java 6支持PKCS5PADDING填充方式
     * Bouncy Castle支持PKCS7Padding填充方式
     */
    public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

    /**
     * 加密
     *
     * @param sourceData 待加密数据
     * @param key  密钥
     *
     * @return byte[] 加密数据
     *
     * @throws Exception
     */
    public static byte[] encrypt(byte[] sourceData, byte[] key) throws Exception {

        // 还原密钥
        Key k = toKey(key);

		/*
         * 实例化
		 * 使用PKCS5Padding填充方式
		 */
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        // 初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);

        // 执行操作
        return cipher.doFinal(sourceData);
    }

    /**
     * 解密
     *
     * @param cipherData 待解密数据
     * @param key  密钥
     *
     * @return byte[] 解密数据
     *
     * @throws Exception
     */
    public static byte[] decrypt(byte[] cipherData, byte[] key) throws Exception {

        // 还原密钥
        Key k = toKey(key);

		/* 
         * 实例化
		 * 使用PKCS5Padding填充方式
		 */
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        // 初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);

        // 执行操作
        return cipher.doFinal(cipherData);
    }



    /**
     * 生成数据统计密钥 <br>
     * 从2.2.0 密钥改变每个手机一个，利用 IMEI 计算出一个密匙值。
     *
     * @return byte[] 168位 3DES 密钥
     *
     * @throws Exception
     */
    public static byte[] genDESKeyByIMEI(String imei) throws Exception {

        byte[] keyBytes = "2#%UImlk./<]{0-7gj*93.D*".getBytes();

        //将 imei 与 key 按位异或
        if (imei != null && !"".equals(imei)) {

            byte[] imeiBytes    = imei.getBytes();
            int keySize         = keyBytes.length;
            int imeiSize        = imeiBytes.length;
            for (int index = 0; index < keySize; index++) {
                keyBytes[index] = (byte) (keyBytes[index] ^ imeiBytes[(index % imeiSize)]);
            }

        }

        return keyBytes;
    }

    /**
     * 随机生成一个二进制密钥
     * @return  二进制密钥
     * @throws java.security.NoSuchAlgorithmException
     */
    public static byte[] genDESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);

        keyGenerator.init(168);

        SecretKey secretKey = keyGenerator.generateKey();

        return secretKey.getEncoded();
    }

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     *
     * @return Key 密钥
     *
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        // 实例化DES密钥材料
        DESedeKeySpec dks = new DESedeKeySpec(key);

        // 实例化秘密密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);

        // 生成秘密密钥
        SecretKey secretKey = keyFactory.generateSecret(dks);

        return secretKey;
    }
}
