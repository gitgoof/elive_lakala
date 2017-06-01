package com.lakala.library.encryption;

import com.lakala.library.util.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class AESUtils {

    /**
     * Turns array of bytes into string
     *
     * @param buf
     *           Array of bytes to convert to hex string
     * @return Generated hex string
     */
    public static String asHexString(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    /**
     * Converts a hexadecimal String to a byte array.
     *
     * @param hexStr
     * @return
     */
    public static byte[] asByteArray(String hexStr) {
        byte bArray[] = new byte[hexStr.length() / 2];
        for (int i = 0; i < (hexStr.length() / 2); i++) {
            byte firstNibble = Byte.parseByte(hexStr.substring(2 * i, 2 * i + 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(hexStr.substring(2 * i + 1, 2 * i + 2), 16);
            int finalByte = (secondNibble) | (firstNibble << 4); // bit-operations
            // only with
            // numbers, not
            // bytes.
            bArray[i] = (byte) finalByte;
        }
        return bArray;
    }

    /**
     * Given an input string and a hexadecimal AES secret key, this method
     * outputs the encrypted hexadecimal value.
     *
     * @param whatToEncrypt
     * @param aesHexKey
     * @return
     * @throws Exception
     */
    public static String encrypt2(String whatToEncrypt, String aesHexKey) throws Exception {

        LogUtil.print("key",aesHexKey);
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec skeySpec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encryptedBytes = cipher.doFinal(whatToEncrypt.getBytes());
        LogUtil.print("key",asHexString(encryptedBytes));
        return asHexString(encryptedBytes);

    }

    /**
     * Given an input encrypted string (in hexadecimal format) and a hexadecimal
     * AES secret key, this method outputs a decrypted string value.
     *
     * @param whatToDecrypt
     * @param aesHexKey
     * @return
     * @throws Exception
     */
    public static String decrypt2(String whatToDecrypt, String aesHexKey) throws Exception {
        LogUtil.print("key",aesHexKey);
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec skeySpec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decryptedBytes = cipher.doFinal(asByteArray(whatToDecrypt));
        LogUtil.print("key",new String(decryptedBytes));
        return new String(decryptedBytes);

    }

    public static String encrypt(String whatToDecrypt, String aesHexKey) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = whatToDecrypt.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(aesHexKey.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            LogUtil.print("key",new BASE64Encoder().encode(encrypted));
            return new BASE64Encoder().encode(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String whatToDecrypt, String aesHexKey) throws Exception {
        try
        {
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(whatToDecrypt);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(aesHexKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(aesHexKey.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            LogUtil.print("key",originalString);
            return originalString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an AES 128-bit secret key as a hexadecimal string.
     *
     * @return
     * @throws Exception
     */
    public static String getAesHexKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // Higher than 128-bit encryption requires a download of additional provider implementations for the JDK.

        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();

        return asHexString(raw);
    }

}

