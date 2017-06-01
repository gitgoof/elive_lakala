package com.lakala.core.fileupgrade;

/**
 * <p>Description  : 文件更新代理.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午12:55.</p>
 */
/*package*/ class FileUpgradeDelegate {

    /**
     * start
     *
     * @param entity  EntityInterface
     */
    public void onStart(EntityInterface entity){}

    /**
     * 下载文件进度
     *
     * @param bytesWritten  当前下载
     * @param totalSize     总大小
     * @param entity        下载实体
     */
    public void onProgress(int bytesWritten, int totalSize, EntityInterface entity){}

    /**
     * 更新成功或该文件不需要更新时候回调该方法
     *
     * @param entity EntityInterface
     */
    public void onSuccess(EntityInterface entity){}

    /**
     * 该方法为同步回调方法，当且仅当检测 app 预装存在的文件时回调
     *
     * @param entity EntityInterface
     */
    public void onLocalExist(EntityInterface entity){}

    /**
     * 更新失败回调该方法
     *
     * @param entity EntityInterface
     */
    public void onFailure(EntityInterface entity){}

    /**
     * finish
     *
     * @param entity  EntityInterface
     */
    public void onFinish(EntityInterface entity){}

    /**
     * 异常信息回调
     *
     * @param id    id
     * @param data  data
     */
    public void onError(String id, Object data){}
}
