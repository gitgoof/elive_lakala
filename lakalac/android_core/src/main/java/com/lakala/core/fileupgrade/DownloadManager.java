package com.lakala.core.fileupgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 * <p>Description  : 文件下载管理，保存所有下载成功的文件实体和下载失败的文件实体.
 *                   保存上次检测完成的三个列表（下载总列表，下载成功列表，下载失败列表）</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/9.</p>
 * <p>Time         : 下午8:19.</p>
 */
/*package*/ class DownloadManager {
    /*                  保存上次下载完成未处理的 list 的文件名字                 */
    final private static String SUCCESS_NAME             = "success.dat";
    final private static String FAILURE_NAME             = "failure.dat";
    final private static String DOWNLOAD_NAME            = "download.dat";
    /*                  当前下载相关列表                    */
    private final Vector<FileEntity> downloadList        = new Vector<>();
    private final Vector<FileEntity> successEntity       = new Vector<>();
    private final Vector<FileEntity> failureEntity       = new Vector<>();
    //检测文件升级计数列表
    private final Vector<String>     checkList           = new Vector<>();

    private boolean isAllChecking     = false;
    private boolean isSingleChecking  = false;
    //是否开始检测文件升级
    private boolean isStartCheck      = false;

    private static DownloadManager instance;

    private DownloadManager(){
    }

    public static synchronized DownloadManager getInstance(){
        if (instance == null){
            instance = new DownloadManager();
        }

        return instance;
    }

    /**
     * 添加下载的实体到下载列表中
     *
     * @param entity    Entity
     */
    public void addDownloadEntity(FileEntity entity){
        if (downloadList.contains(entity)){
            return;
        }
        downloadList.add(entity);
    }

    /**
     * 添加下载成功的实体到列表中
     *
     * @param entity    Entity
     */
    public void addSuccessEntity(FileEntity entity){
        if (successEntity.contains(entity)){
            return;
        }
        successEntity.add(entity);
    }

    /**
     * 添加下载失败的实体到列表中
     *
     * @param entity    Entity
     */
    public void addFailureEntity(FileEntity entity){
        if (failureEntity.contains(entity)){
            return;
        }
        failureEntity.add(entity);
    }

    /**
     * 添加 List 到下载列表中，并删除当前 list 中的实体
     *
     * @param list   List
     */
    public void addDownloadEntityList(Vector<FileEntity> list){
        downloadList.clear();
        downloadList.addAll(list);
    }

    /**
     * 添加 List 到下载成功列表中，并删除当前 list 中的实体
     *
     * @param list   List
     */
    public void addSuccessEntityList(Vector<FileEntity> list){
        successEntity.clear();
        successEntity.addAll(list);
    }

    /**
     * 添加 List 到下载失败列表中，并删除当前 list 中的实体
     *
     * @param list   List
     */
    public void addFailureEntityList(Vector<FileEntity> list){
        failureEntity.clear();
        failureEntity.addAll(list);
    }

    /**
     * 添加一个需要计数到列表
     */
    public void addCheckCount(){
        isStartCheck = true;
        checkList.add("1");
    }

    /**
     * 从列表中移除一个计数
     */
    public void removeCheckCount(){
        if (checkList.size() > 0){
            checkList.remove(0);
        }
    }

    /**
     * 所有文件是否检测完成
     *
     * @return boolean
     */
    public boolean allCheckFinish(){
        if (isStartCheck && checkList.size() == 0){
            isStartCheck = false;
            return true;
        }

        return false;
    }

    /**
     * 获取下载成功的实体列表
     *
     * @return  list
     */
    public Vector<FileEntity> getSuccessEntityList(){
        return successEntity;
    }

    /**
     * 获取下载失败的实体列表
     *
     * @return  list
     */
    public Vector<FileEntity> getFailureEntityList(){
        return failureEntity;
    }

    /**
     * 获取下载列表
     *
     * @return  list
     */
    public Vector<FileEntity> getDownloadList(){
        return downloadList;
    }

    /**
     * 获取检测的所有文件的列表
     *
     * @return list
     */
    public Vector<String> getCheckList(){
        return checkList;
    }

    /**
     * 设置是否正在检测所有文件
     *
     * @param doing boolean
     */
    public void setAllChecking(boolean doing){
        this.isAllChecking = doing;
    }

    /**
     * 设置是否正在检测单个文件
     *
     * @param doing boolean
     */
    public void setSingleChecking(boolean doing){
        this.isSingleChecking = doing;
    }

    /**
     * 获取是否正在检测所有文件
     *
     * @return boolean
     */
    public boolean isAllChecking(){
        return isAllChecking;
    }

    /**
     * 获取是否正在检测单个文件
     *
     * @return boolean
     */
    public boolean isSingleChecking(){
        return isSingleChecking;
    }

    /**
     * 移除Entity
     *
     * @param entity FileEntity
     */
    public void remove(FileEntity entity){
        if (downloadList.contains(entity)){
            downloadList.remove(entity);
        }
        if (successEntity.contains(entity)){
            successEntity.remove(entity);
        }
        if (failureEntity.contains(entity)){
            failureEntity.remove(entity);
        }
    }

    /**
     * 清空列表
     */
    public void clear(){
        downloadList.clear();
        successEntity.clear();
        failureEntity.clear();
        checkList.clear();
    }

    /**
     * 保存本次更新的所有结果列表, 每次保存都会先删除当前文件
     */
    public void saveLastList(){
        clearSaveFile();

        File success  = getSuccessListFile(),
             failure  = getFailureListFile(),
             download = getDownloadListFile();

        saveFile(getDownloadList(), download);
        saveFile(getSuccessEntityList(), success);
        saveFile(getFailureEntityList(), failure);

    }

    /**
     * 获取保存下载成功的列表
     *
     * @return  List
     */
    public Vector<FileEntity> getSaveSuccessList(){
        return getSaveList(getSuccessListFile());
    }

    /**
     * 获取保存下载失败的列表
     *
     * @return List
     */
    public Vector<FileEntity> getSaveFailureList(){
        return getSaveList(getFailureListFile());
    }

    /**
     * 获取保存的下载列表
     *
     * @return List
     */
    public Vector<FileEntity> getSaveDownloadList(){
        return getSaveList(getDownloadListFile());
    }

    /**
     * 保存 List 到 File
     *
     * @param list  List
     * @param file  File
     */
    private void saveFile(Vector<FileEntity> list, File file){
        if (file == null){
            return;
        }

        FileOutputStream    fileOutputStream   = null;
        ObjectOutputStream  objectOutputStream = null;
        try {
            fileOutputStream   = new FileOutputStream(file.toString());
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list);
        } catch (Exception e) {
            LOG.e(e.getMessage(), e);
        } finally {
            if (objectOutputStream != null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 清空保存的文件
     */
    private void clearSaveFile(){

        File success  = getSuccessListFile(),
             failure  = getFailureListFile(),
             download = getDownloadListFile();
        if (success != null){
            success.delete();
        }
        if (failure != null){
            failure.delete();
        }
        if (download != null){
            download.delete();
        }
    }

    /**
     * 获取保存的 List 文件
     *
     * @param file  文件
     * @return      List
     */
    private Vector<FileEntity> getSaveList(File file){
        Vector<FileEntity> list                 = new Vector<>();
        FileInputStream    fileInputStream      = null;
        ObjectInputStream  objectInputStream    = null;

        if (file == null || file.length() == 0){
            String fileName = file != null ? file.toString() : "";
            LOG.e("file is null :" + (file==null) +" , or file length == 0  " + fileName);
            return list;
        }

        try {
            fileInputStream   = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            list              = (Vector<FileEntity>) objectInputStream.readObject();
        } catch (Exception e) {
            list              = new Vector<>();
            LOG.e(e.getMessage(), e);
        } finally {
            if (objectInputStream != null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return list;
    }

    /**
     * 获取下载成功列表的文件
     *
     * @return File
     */
    private File getSuccessListFile(){
        return getSaveFile(SUCCESS_NAME);
    }

    /**
     * 获取保存下载失败列表的文件
     *
     * @return File
     */
    private File getFailureListFile(){
        return getSaveFile(FAILURE_NAME);
    }

    /**
     * 获取保存下载列表的文件
     *
     * @return File
     */
    private File getDownloadListFile(){
        return getSaveFile(DOWNLOAD_NAME);
    }

    /**
     * 获取该名字保存的文件
     *
     * @param name Name
     * @return     File
     */
    private File getSaveFile(String name){

        File file = new File(Config.getInstance().getPathConfig().getRootPath()
                + File.separator
                + name);
        if (file.exists()){
            return file;
        }

        boolean isDirCreated  = file.getParentFile().mkdirs();
        boolean isFileCreated = false;
        try {
            isFileCreated = file.createNewFile();
        } catch (IOException e) {
            LOG.e(e.getMessage(), e);
        }

        return isDirCreated && isFileCreated ? file : null;
    }

}
