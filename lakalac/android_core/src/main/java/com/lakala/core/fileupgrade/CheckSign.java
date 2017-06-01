package com.lakala.core.fileupgrade;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * <p>Description  : bundle验签 和 本地文件验证.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午5:16.</p>
 */
/*package*/ class CheckSign{

    /**
     * 验证签名文件
     *
     * @param originalFile 原文件
     */
    public static boolean checkSign(File originalFile) {
        String rootPath = originalFile.getParent();

        if (rootPath == null) {
            rootPath = "";
        }else{
            rootPath += File.separator;
        }

        String originalFileName = originalFile.getName();
        String signFileName     = originalFileName + ".sign";
        File   signFile         = new File(rootPath + signFileName);

        return checkSignatureFile(originalFile,signFile);
    }

    /**
     * 效验签名
     */
    private static boolean checkSignatureFile(File originalFile, File signFile) {
        boolean isComplete     = false;
        try {
            byte[]  signStr        = Utils.readFile2Byte(signFile);

            String  digest         = Utils.readFile(signFile);
            //公钥解密文件
            String  decryptDigest  = "";

            RSAEncrypt rsaEncrypt  = new RSAEncrypt(Config.getInstance().getPublicKey());
            decryptDigest          = rsaEncrypt.decryptPublicKey(Utils.formatFileContent(digest));
            //格式化内容，去掉空格换行
            decryptDigest          = Utils.formatFileContent(decryptDigest);

            //文件摘要
            if (Utils.isExist(originalFile)) {
                String fileDigestMD5 = Digest.md5(originalFile);
                //效验每个文件
                if (fileDigestMD5.equals(decryptDigest)) {
                    isComplete = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isComplete     = false;
        }
        return isComplete;
    }


    /**
     * 验证 bundle 完整性
     *
     * @param path path  bundle 路径（展开后的全路径）
     * @return     boolean
     */
    public static boolean checkBundle(String path) {
        String rootPath         = path + File.separator;
        String originalFileName = "manifest.json";
        String signFileName     = "manifest-digest.json";

        return bundleFileCheck(rootPath,originalFileName,signFileName);
    }

    /**
     * 验证Bundle文件
     */
    private static boolean bundleFileCheck(String rootPath,
                                           String originalFileName,
                                           String signFileName)  {
        boolean isComplete = false;
        try {

            //验证文件manifestDigest
            String     manifestDigestStr = Utils.readFile(rootPath + signFileName);

            JSONObject jsonObject        = new JSONObject(manifestDigestStr);
            String     digest            = jsonObject.getString("digest").replace("\n", "").replace("\t", "").replace("\r", "");
            //公钥解密文件
            String     decryptDigest     = "";
            RSAEncrypt rsaEncrypt        = new RSAEncrypt(Config.getInstance().getPublicKey());
            decryptDigest                = rsaEncrypt.decryptPublicKey(digest);
            //文件摘要
            File       manifestFile      = new File(rootPath + originalFileName);

            if (Utils.isExist(manifestFile)) {
                String fileDigestMD5 = Digest.md5(manifestFile);
                //效验每个文件
                if (fileDigestMD5.equals(decryptDigest)) {
                    isComplete = checkAllFile(rootPath,manifestFile);
                }
            }
        }catch (Exception e){
            LOG.e(e.getMessage(), e);
            isComplete = false;
        }

        return isComplete;
    }

    /**
     * 效验Bundle 目录下所有文件完整性。
     * 这个方法根据 Bundle 目录下的 manifest.json 文件中文件列表验签文件。
     * @param rootPath      bundle 路径
     * @param manifestFile  manifest.json 文件中文件
     * @return 是否验签成功
     * @throws IOException
     * @throws JSONException
     */
    private static boolean checkAllFile(String rootPath,File manifestFile) throws IOException, JSONException {
        String     manifestContent = Utils.readFile(manifestFile);
        JSONObject manifestObj     = new JSONObject(manifestContent);
        JSONArray  array           = manifestObj.getJSONArray("files");
        int        len             = array.length();

        for (int i = 0; i < len; i++) {
            JSONObject fileObj           = array.getJSONObject(i);
            String     path              = fileObj.getString("path");
            String     singleFileDigest  = fileObj.getString("digest").replace("\n", "").replace("\t", "").replace("\r", "");
            File       file              = new File(rootPath + File.separator + path);

            if (!singleFileDigest.equals(Digest.md5(file))) {
                return false;
            }
        }
        return true;
    }
}
