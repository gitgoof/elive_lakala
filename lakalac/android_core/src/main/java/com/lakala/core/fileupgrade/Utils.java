package com.lakala.core.fileupgrade;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>Description  : TODO.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午1:14.</p>
 */
/*package*/ class Utils {

    private static final String NAME = Utils.class.getName();

    /**
     * 获取错误信息
     *
     * @param message    error message
     * @param className  Class name
     * @return           String
     */
    public static String errorMessage(String message, String className){
        return String.format("%s  Class : %s", message, className);
    }

    /**
     * 字符串是否为空
     *
     * @param str  String
     * @return     boolean
     */
    public static boolean isEmpty(String str){
        return str == null || "".equals(str.trim()) || "null".equals(str.trim());
    }

    /**
     * 格式话文件内容，去空格换行符
     */
    public static String formatFileContent(String string){
        if (string == null) return "";
        String newString = string.replaceAll("\n", "")
                .replaceAll("\t", "")
                .replaceAll("\r", "");
        return newString;
    }

    /**
     * 创建文件
     *
     * @param file 文件
     */
    public static boolean createFile(File file) throws IOException {
        if (file == null)
            return false;
        if (file.exists())
            return true;

        //文件夹不存在
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }

        return file.createNewFile();
    }


    /**
     * 复制assets中的文件到指定目录下
     *
     * @param context
     * @param assetsFileName  assets相对路径
     * @param targetFilePath  目标文件绝对路径
     * @return
     */
    public static boolean copyAssetFile(Context context, String assetsFileName, String targetFilePath) {
        try {
            InputStream inputStream = context.getAssets().open(assetsFileName);
            File file = new File(targetFilePath);

            if (file.exists()){
                deleteFile(file);
            }
            createFile(file);

            FileOutputStream output = new FileOutputStream(file);
            byte[] buf              = new byte[10240];
            int count               = 0;
            while ((count = inputStream.read(buf)) > 0) {
                output.write(buf, 0, count);
            }
            output.close();
            inputStream.close();
        } catch (IOException e) {
            LOG.e(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 读取文件内容（File -> String）
     *
     * @param path      文件路径
     * @return          字符内容
     */
    public static String readFile(String path) throws IOException {
        if (path == null || path.equals(""))
            return "";
        return readFile(new File(path));
    }

    /**
     * 读取文件内容（File -> String）
     *
     * @param file      文件
     * @return          字符内容
     */
    public static String readFile(File file) throws IOException {
        if (file == null)
            return "";
        if (!file.exists()){
            throw new FileNotFoundException("读取文件不存在");
        }
        if (!file.isFile()){
            throw new FileNotFoundException("读取文件非法");
        }

        BufferedReader inputStream = null;
        StringBuffer sb = new StringBuffer();
        try {
            inputStream = new BufferedReader(new FileReader(file));
            char[] bytes = new char[1024 * 5];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(bytes,0,len);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return sb.toString().trim();
    }

    /**
     * 读取文件为字节数组
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static byte[] readFile2Byte(File file) throws IOException {
        if (file == null)
            return new byte[]{};
        if (!file.exists())
            throw new FileNotFoundException("读取文件不存在");
        if (!file.isFile())
            throw new FileNotFoundException("读取文件非法");

        FileInputStream inputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024 * 5];
            int len = 0;
            while ((len = (inputStream.read(bytes))) != -1) {
                out.write(bytes,0,len);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return out.toByteArray();
    }

    /**
     * 文件是否存在
     *
     * @param file 文件
     *             return true:存在 false:不存在
     */
    public static boolean isExist(File file) {
        if (file == null)
            return false;
        return file.exists();
    }

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     *                 return true:存在 false:不存在
     */
    public static boolean isExist(String filePath) {
        if (filePath == null || filePath.equals(""))
            return false;
        return new File(filePath).exists();
    }

    /**
     * 创建文件夹
     *
     * @param file 文件夹
     */
    public static boolean createDirectory(File file) {
        if (file == null)
            return false;
        if (file.exists())
            return true;
        return file.mkdirs();
    }

    /**
     * 创建文件夹(重载方法)
     *
     * @param path 文件夹路径
     */
    public static boolean createDirectory(String path) {
        if (path == null || path.equals(""))
            return false;
        return createDirectory(new File(path));
    }

    /**
     * 获取文件后缀名
     *
     * @param file 文件
     * @return 返回文件后缀名
     */
    public static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory())
            return "";

        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");
        if (name.startsWith(".") || lastIndex == -1)
            return "";

        return name.substring(lastIndex + 1, name.length());
    }

    /**
     * 解压缩文件
     *
     * @param zipFile    压缩包
     * @param targetPath 解压缩文件目标目录,""代表当前文件夹
     */
    public static boolean decompressZip(File zipFile, String targetPath) throws IOException {
        //解压文件不存在
        if (zipFile == null || targetPath == null)
            return false;
        if (!zipFile.exists())
            throw new FileNotFoundException("解压zip文件不存在");
        if (!getSuffix(zipFile).equals("zip"))
            throw new FileNotFoundException("压缩文件非法");

        //创建目标目录
        createDirectory(targetPath);
        //获取当前目录
        if (targetPath.equals("")) {
            String zipParentPath = zipFile.getParent();
            if (zipParentPath != null && !zipParentPath.equals("")) {
                targetPath = zipParentPath + File.separator;
            }
        } else {
            targetPath = targetPath + File.separator;
        }
        //标志是否成功
        boolean isSuccess = false;
        //获取zip包输入流
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            //解压缩
            unZip(targetPath, zipInputStream);
            isSuccess = true;
        } finally {
            if (zipInputStream != null) {
                zipInputStream.closeEntry();
                zipInputStream.close();
            }
        }
        return isSuccess;
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 压缩包
     * @param targetPath  解压缩文件目标目录,""代表当前文件夹
     */
    public static boolean decompressZip(String zipFilePath, String targetPath) throws IOException {
        if (zipFilePath == null || targetPath == null)
            return false;
        return decompressZip(new File(zipFilePath), targetPath);
    }

    /**
     * 解压缩文件
     *
     * @param fileTargetPath 解压缩文件目标目录
     * @param zipInputStream 压缩包输入流
     */
    private static void unZip(String fileTargetPath, ZipInputStream zipInputStream) throws IOException {
        if (!fileTargetPath.equals("")) {
            fileTargetPath = fileTargetPath + File.separator;
        }

        ZipEntry entry      = null;
        String entryName    = "";
        while ((entry = zipInputStream.getNextEntry()) != null) {
            entryName = entry.getName();
            if (entry.isDirectory()) {
                entryName = entryName.substring(0, entryName.length() - 1);
                createDirectory(fileTargetPath + entryName);
            } else {
                File file = new File(fileTargetPath + entryName);
                createFile(file);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    byte[] bytes = new byte[1024 * 5];
                    int len = 0;
                    while ((len = zipInputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, len);
                    }
                    fileOutputStream.flush();
                } finally {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
            }
        }
    }


    /**
     * 删除文件（1.单个文件直接删除2.文件夹递归删除）
     *
     * @param file 需要删除的文件
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file == null || !file.exists())
            return false;

        //文件夹递归删除
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                deleteFile(childFile);
            }
        }

        return file.delete();
    }

    /**
     * 删除文件(重载方法)（1.单个文件直接删除2.文件夹递归删除）
     *
     * @param filePath 文件路径
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.equals(""))
            return false;

        File file = new File(filePath);
        return deleteFile(file);
    }

}
