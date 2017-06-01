package com.lakala.core.fileupgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * <p>Description  : TODO.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午5:21.</p>
 */
/*package*/ class Digest {

    /**
     * 对文件进行 md5
     *
     * @param file  file
     * @return      md5
     */
    public static String md5(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md      = MessageDigest.getInstance("MD5");
                          fis     = new FileInputStream(file);
            byte[]        buffer  = new byte[2048];
            int           length  = -1;

            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            byte[] b = md.digest();

            return byte2HexString(b);

        } catch (Exception ex) {
            LOG.e(ex.getMessage(), ex);
            return null;
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.e(e.getMessage(), e);
                }

        }

    }

    /**
     * byte2HexString
     * byte 转 16进制
     */
    private static String byte2HexString(byte[] tmp) {
        String s;
        char str[] = new char[16 * 2];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++]   = hexDigits[byte0 >>> 4 & 0xf];
            str[k++]   = hexDigits[byte0 & 0xf];
        }
        s = new String(str);
        return s;
    }

    /**
     * 对文件全文生成MD5摘要
     *
     * @param file
     * 要加密的文件
     * @return MD5摘要码
     */

    private final static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
