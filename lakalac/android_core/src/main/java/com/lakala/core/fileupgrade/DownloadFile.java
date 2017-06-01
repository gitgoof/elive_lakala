package com.lakala.core.fileupgrade;


import com.lakala.library.net.HttpRequestParams;
import com.loopj.lakala.http.AsyncHttpClient;
import com.loopj.lakala.http.FileAsyncHttpResponseHandler;
import com.loopj.lakala.http.RequestParams;

import org.apache.http.Header;

import java.io.File;

/**
 * <p>Description  : 文件下载.
 *                   该类会对下载的文件进行验签，
 *                   注意：在强制更新判断中，只有当前实体没有需要检测的子实体时，若需要
 *                   强制更新，才会进行相应的更新</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/9.</p>
 * <p>Time         : 上午9:20.</p>
 */
/*package*/ class DownloadFile {

    private static final String PREFIX = "http://";

    private AsyncHttpClient      httpClient;
    private RequestParams        params;
    private EntityInterface      entityInterface;
    private FileUpgrade          fileUpgrade;
    private FileUpgradeDelegate  delegate;
    private Config               config;

    public DownloadFile(EntityInterface entityInterface, FileUpgradeDelegate delegate){
        this.httpClient        = new AsyncHttpClient(Config.getInstance().getContext());
        this.entityInterface   = entityInterface;
        this.delegate          = delegate;
        this.fileUpgrade       = FileUpgrade.getInstance();
        this.config            = Config.getInstance();
    }

    /**
     * 执行文件下载
     */
    public void download(){
        if (entityIsNull()){
            LOG.d(Utils.errorMessage("download is stop!", DownloadFile.class.getName()));
            return;
        }

        //如果当前下载队列存在该实体，那么直接返回，不重复请求
        if (entityInterface instanceof FileEntity && DownloadManager.getInstance().getDownloadList().contains(entityInterface)){
            return;
        }

        //是否非主配置文件
        final boolean isFileEntity      = entityInterface instanceof FileEntity;
        //是否为主配置文件实体
        final boolean isMainFileEntity  = entityInterface instanceof FileMainEntity;

        //设置请求参数
        setParams();
        //设置请求头
        setRequestHeader();
        //设置超时时间
        setTimeout();

        httpClient.post(config.getContext(), getUrl(), params, new FileAsyncHttpResponseHandler(entityInterface.getDownloadFile()) {

            @Override
            public void onStart() {
                LOG.e("start download < %s >", entityInterface.getFileName());
                if (isFileEntity){
                    invokeStart((FileEntity)entityInterface);
                }
                if (isMainFileEntity){
                    delegate.onStart(entityInterface);
                }
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                if (isFileEntity){
                    invokeProgress(bytesWritten, totalSize, (FileEntity)entityInterface);
                }
                if (isMainFileEntity){
                    delegate.onProgress(bytesWritten, totalSize, entityInterface);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                LOG.e("success download < %s > , length : %d", entityInterface.getFileName(), file.length());
                if (isFileEntity){
                    handleSuccess(file, (FileEntity)entityInterface);
                }
                if (isMainFileEntity){
                    FileMainEntity mainEntity = (FileMainEntity) entityInterface;

                    boolean newest      = true;
                    boolean newDownload = false;
                    //下载的文件不为空
                    if (file.length() != 0){
                        //验证有效性
                        newest = mainEntity.checkDownloadFile();
                        //文件有效则解压
                        if (newest){
                            mainEntity.decompressEntity();
                            //设置为新下载的最新实体
                            newDownload = true;
                        }
                    }
                    mainEntity.deleteDownloadFile();
                    mainEntity.setNewest(newest);
                    mainEntity.setNewDownload(newDownload);
                    delegate.onSuccess(mainEntity);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                LOG.e("failure download < %s >", entityInterface.getFileName());
                if (isFileEntity){
                    handleFailure(file, (FileEntity)entityInterface);
                }
                if (isMainFileEntity){
                    FileMainEntity mainEntity = (FileMainEntity) entityInterface;
                    mainEntity.setNewest(false);
                    delegate.onFailure(mainEntity);
                }
            }

            @Override
            public void onFinish() {
                if (isFileEntity){
                    invokeFinish((FileEntity)entityInterface);
                }
                if (isMainFileEntity){
                    delegate.onFinish(entityInterface);
                }
            }
        });
    }

    /**
     * 判断 Entity 是否为 null
     *
     * @return  boolean
     */
    private boolean entityIsNull(){
        if (entityInterface == null){
            LOG.e(Utils.errorMessage("Entity is null !", DownloadFile.class.getName()));
            return true;
        }
        return false;
    }

    /**
     * 设置请求 Header
     */
    private void setRequestHeader(){
        httpClient.addHeader("Accept", RequestParams.APPLICATION_OCTET_STREAM);
    }

    /**
     * 设置超时时间
     */
    private void setTimeout(){
        httpClient.setTimeout(config.getRequestConfig().getTimeout());
    }

    /**
     * 获取请求 URL
     *
     * @return URL
     */
    private String getUrl(){
        String url = entityInterface.getCheckURL();

        if (!url.startsWith(PREFIX)){
            url = config.getRequestConfig().getBaseURL() + url;
        }

        return url;
    }

    /**
     * 设置请求参数
     */
    private void setParams(){
        params = new HttpRequestParams(true, config.getRequestConfig().getCommonRequestParams());

        params.put("MKey", Utils.isEmpty(entityInterface.getMD5ForEntityPath()) ? "new" :  entityInterface.getMD5ForEntityPath());
        params.put("UpgradeFile", entityInterface.getTargetDirectory().concat(File.separator).concat(entityInterface.getFileName()));
    }


    /**
     * download success
     */
    private void handleSuccess(File file, FileEntity entity){
        //文件 length
        long    fileLength    = file.length();
        //是否有需要检测更新的子文件
        boolean hasChild      = !Utils.isEmpty(entity.getChildTag());

        //如果下载下来的文件为空，那么不处理
        if (fileLength == 0){
            //本地文件无效并且预装中也不存在该文件 或为新增文件
            //判断是否需要强制更新，若需要强制更新并且可以进行强制更新，而且不需要检测子文件，
            //那么重新进行检测，否则直接回调失败
            if (entity.getFileStatus() == FileEntity.FileStatus.InvalidateAndNotExistFile
                    || entity.getFileStatus() == FileEntity.FileStatus.NewFile){

                if (entity.isForceUpdate() && entity.canForceUpdate() && !hasChild){
                    fileUpgrade.check(entity, delegate);
                    return;
                }
                invokeFailure(entity);
                return;
            }

            //如果有孩子文件需要检测,那么继续检测孩子文件,并且标记孩子实体状态为 OldFile(因为下载的文件为空，本地文件不能保证是最新的)。
            //注意：因为当前目标检测为孩子文件，所以该文件为强制更新也不重新发送请求。
            if (hasChild){
                FileEntity childEntity = fileUpgrade.getFileEntity(entity.getTag(), entity.getChildTag(), delegate);
                childEntity.setFileStatus(FileEntity.FileStatus.OldFile);
                fileUpgrade.check(childEntity, delegate);
                return;
            }

            //如果不需要校验孩子文件，那么当前文件为最新的，将其状态设置为下载下来的最新文件(DownloadFile)
            entity.setFileStatus(FileEntity.FileStatus.DownloadFile);
            //如果不存在以上情况，那么表示本地文件为最新，执行成功回调
            invokeSuccess(entity);
            return;
        }

        //如果下载的文件无效，执行失败处理
        if (!entity.checkDownloadFile()){
            handleFailure(file, entity);
            return;
        }


        //如果需要检测子配置文件,那么解压下载成功的文件，并检查子文件
        if (hasChild){
            entity.decompressEntity();
            FileEntity childEntity = fileUpgrade.getFileEntity(entity.getTag(), entity.getChildTag(), delegate);
            fileUpgrade.check(childEntity, delegate);
            return;
        }

        //如果当前文件不是新添加的，那么标记为 DownloadFile ， 否则不更改标记
        if (entity.getFileStatus() != FileEntity.FileStatus.NewFile){
            //设置文件为最新
            entity.setFileStatus(FileEntity.FileStatus.DownloadFile);
        }

        //如果当前文件是新下载的，那么标记为 true
        entity.setNewDownload(true);
        invokeSuccess(entity);
    }

    /**
     * download failure
     */
    private void handleFailure(File file, FileEntity entity){
        //是否有需要检测更新的子文件
        boolean hasChild       = !Utils.isEmpty(entity.getChildTag());
        //本地文件无效并且预装中也不存在该文件
        boolean localNonExist  = entity.getFileStatus() == FileEntity.FileStatus.InvalidateAndNotExistFile;
        //是否需要强制更新
        boolean forceUpdate    = entity.isForceUpdate();

        //下载失败，删除创建的文件
        Utils.deleteFile(file);

        //#1.
        //如果当前本地无可用文件，而且 app 预装中也不存在，判断是否需要强制更新；
        //如果需要强制更新,并且没有需要检测的子文件，那么继续发送请求，若需要检测子文件则直接回调失败方法（回调的实体为当前无效的实体）；
        //如果不需要强制更新那么直接调用失败回调。
        if (localNonExist) {
            if (forceUpdate && entity.canForceUpdate() && !hasChild) {
                fileUpgrade.check(entity, delegate);
                return;
            }
            invokeFailure(entity);
            return;
        }

        //#2.
        //如果需要检测子配置文件，那么将孩子实体状态标记为 OldFile
        if (hasChild){
            //如果需要检测子配置文件，那么将子配置文件
            FileEntity childEntity = fileUpgrade.getFileEntity(entity.getTag(), entity.getChildTag(), delegate);
            childEntity.setFileStatus(FileEntity.FileStatus.OldFile);
            fileUpgrade.check(childEntity, delegate);
            return;
        }

        //#3.
        //如果需要强制更新，并且强制更新次数还没达到最大值，那么重新发送请求
        if (forceUpdate && entity.canForceUpdate()) {
            fileUpgrade.check(entity, delegate);
            return;
        }

        entity.setFileStatus(FileEntity.FileStatus.OldFile);
        invokeFailure(entity);

    }

    /**
     * invoke start
     */
    private void invokeStart(FileEntity entity){
        if (Utils.isEmpty(entity.getChildTag())){
            DownloadManager.getInstance().addDownloadEntity(entity);
        }
        delegate.onStart(entity);
    }

    /**
     * invoke progress
     */
    private void invokeProgress(int bytesWritten, int totalSize, FileEntity entity){
        delegate.onProgress(bytesWritten, totalSize, entity);
    }

    /**
     * invoke success
     */
    private void invokeSuccess(FileEntity entity){
        DownloadManager.getInstance().addSuccessEntity(entity);
        delegate.onSuccess(entity);
    }

    /**
     * invoke failure
     */
    private void invokeFailure(FileEntity entity){
        DownloadManager.getInstance().addFailureEntity(entity);
        delegate.onFailure(entity);
    }

    /**
     * invoke finish
     */
    private void invokeFinish(FileEntity entity){
        if (Utils.isEmpty(entity.getChildTag())){
            delegate.onFinish(entity);
        }
    }
}
