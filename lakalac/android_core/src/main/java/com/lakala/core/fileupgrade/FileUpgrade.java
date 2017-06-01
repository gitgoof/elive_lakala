package com.lakala.core.fileupgrade;

import android.content.Context;
import android.util.Log;

import com.lakala.library.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * <p>Description  : 文件更新.</p>
 * <p>说明：一级文件为主配置文件中对应的文件，二级文件为一级文件中对应的文件。</p>
 * <p>2014/12/24  增加 {@link #doRequest} {@link #check(boolean, com.lakala.core.fileupgrade.FileUpgradeDelegate, String...)} 可灵活控制需要更新的文件是否执行网络请求。</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 上午11:28.</p>
 */
/*package*/  class FileUpgrade {

    private static final String  NAME        = FileUpgrade.class.getName();

    private FileMainEntity mainEntity;
    private boolean        doRequest;

    private static FileUpgrade instance;

    private FileUpgrade(){
        mainEntity = Config.getInstance().getMainEntity();
    }

    public static synchronized FileUpgrade getInstance(){
        if (instance == null){
            instance = new FileUpgrade();
        }

        return instance;
    }

    /**
     * 文件更新检测
     *
     * @param delegate  模块代理
     * @param tag       检测目标，tag.length 为0则检测main, 为1检测一级文件，为2检测一级和二级文件
     */
    public void check(FileUpgradeDelegate delegate, String... tag){
        this.doRequest = true;

        checkStart(delegate, tag);
    }

    /**
     * 文件更新检测，同 {@link #check(com.lakala.core.fileupgrade.FileUpgradeDelegate, String...)},
     * 如果需要网络请求，增加是否发送判断
     *
     * @param doRequest 是否执行请求
     * @param delegate  delegate
     * @param tag       tag
     */
    public void check(boolean doRequest, FileUpgradeDelegate delegate, String... tag){
        this.doRequest = doRequest;

        checkStart(delegate, tag);
    }

    /**
     * 开始检测文件更新
     *
     * @param delegate delegate
     * @param tag      tag
     */
    private void checkStart(FileUpgradeDelegate delegate, String... tag){
        if (delegate == null){
            throw new IllegalArgumentException("FileUpgradeDelegate must not be null");
        }

        int len = tag.length;
        String tag0 , tag1; tag0 = tag1 = "";


        if (len == 0){
            checkMain(delegate);
            return;
        }

        if (len == 1){
            tag0 = tag[0];
            if (Utils.isEmpty(tag0)){
                delegate.onError(Utils.errorMessage("tag0 不能为空!", NAME), "61");
                return;
            }
        }

        if (len == 2){
            tag0 = tag[0];
            tag1 = tag[1];
            if (Utils.isEmpty(tag0)){
                delegate.onError(Utils.errorMessage("tag0 不能为空!", NAME), "70");
                return;
            }
        }

        check(tag0, tag1, delegate);
    }

    /**
     * 检测主配置文件
     * #1. 校验包目录下面是否存在主配置文件，若不存在则复制预装版本(app 预装 assets 中必须存在主配置文件)
     * #2. 校验主配置文件是否有效，若无效，则复制预装版本到包目录
     * #3. 联网请求
     */
    private void checkMain(FileUpgradeDelegate delegate){

        try {
            mainEntity.checkValidateSignIn();

            delegate.onLocalExist(mainEntity);

            new DownloadFile(mainEntity, delegate).download();

        } catch (Exception e) {
            delegate.onError(Utils.errorMessage(e.getMessage(), NAME), "99");
            LOG.e(e.getMessage(), e);
        }

    }

    /**
     * 检测
     *
     * @param tag0      一级 tag
     * @param tag1      二级 tag
     * @param delegate  模块代理
     */
    private void check(String tag0, String tag1, FileUpgradeDelegate delegate){
        FileEntity tag0Entity = getFileEntity(tag0, delegate);
        if (tag0Entity != null){
            tag0Entity.setChildTag(tag1);
            check(tag0Entity, delegate);
        }
        else{
            LogUtil.e("FileUpgrade",String.format("'%s' not found",tag0));
        }
    }

    /**
     * 根据实体检测对应的文件的有效性
     *
     * @param entity    实体
     * @param delegate  模块代理
     */
     void check(FileEntity entity, FileUpgradeDelegate delegate){

        if (!checkValidateAndSignIn(entity, delegate)){
            return;
        }

        boolean doNotCheckChild   = Utils.isEmpty(entity.getChildTag());
        boolean isConfigOrUpgrade = entity.isConfigFile() || entity.isUpgradeFile();

        if (doNotCheckChild && isConfigOrUpgrade){
            delegate.onLocalExist(entity);
        }

        checkNeedUpgrade(entity, delegate);
    }

    /**
     * 检测是否需要更新，如果需要则更新，反之回调成功
     *
     * @param entity    FileEntity
     * @param delegate  模块代理
     */
    private void checkNeedUpgrade(FileEntity entity, FileUpgradeDelegate delegate){
        boolean tag1IsEmpty = Utils.isEmpty(entity.getChildTag());
        boolean needUpgrade = entity.needUpgrade();
        //需要更新
        if (needUpgrade && doRequest){
            new DownloadFile(entity, delegate).download();
        }
        //不需要更新
        else {
            //如果 tag1 为空，则检测结束，执行成功回调
            if (tag1IsEmpty){
                delegate.onSuccess(entity);
                delegate.onFinish(entity);
            }
            //检测 tag1 对应实体的文件
            else {
                check(getFileEntity(entity.getTag(), entity.getChildTag(), delegate), delegate);
            }
        }

    }

    /**
     * 校验文件是否有效，以及对文件进行验签
     *
     * @param entity        FileEntity
     * @param delegate      模块代理
     * @return              是否验证通过
     */
    private boolean checkValidateAndSignIn(FileEntity entity, FileUpgradeDelegate delegate){

        //如果在本地和预装assets里面都没有找到该文件，那么该文件为新增文件
        if (!validateFile(entity, delegate)){
            new DownloadFile(entity, delegate).download();
            return false;
        }

        //若验签失败则复制原有文件，并网络请求下载
        if (!checkSignIn(entity, delegate)){
            //如果文件状态为验证无效，但是app预装assets中存在，并且这个文件是 config 或 upgrade 文件，而且
            //不检测子配置，那么同步执行 onLocalExist 方法
            boolean isExistFileInAssets = entity.getFileStatus() == FileEntity.FileStatus.InvalidateButExistFile;
            boolean doNotCheckChild     = Utils.isEmpty(entity.getChildTag());
            boolean isConfigOrUpgrade   = entity.isConfigFile() || entity.isUpgradeFile();
            if (isExistFileInAssets && doNotCheckChild && isConfigOrUpgrade){
                delegate.onLocalExist(entity);
            }
            new DownloadFile(entity, delegate).download();
            return false;
        }

        return true;
    }

    /**
     * 校验文件是否存在，若不存在，则在app预装asset中找，如果还没有，则该文件为新增文件。
     *
     * @param entity        文件对应的实体
     * @param delegate      模块代理
     * @return              是否存在
     */
    private boolean validateFile(FileEntity entity, FileUpgradeDelegate delegate){
        if (entity == null) {
            delegate.onError(Utils.errorMessage("FileEntity 不能为 null", NAME), "");
            return false;
        }

        File   target        = entity.getFile();

        if (target.exists()){
            return true;
        }
        try {
            //如果不存在，则 copy Assets 中预装的到包目录下
            boolean isExistInAsset = entity.copyAssetFileAndDecompress();

            //如果复制失败，则说明预装没有这个文件
            if (!isExistInAsset) {
                Utils.createFile(target);
                //设置文件状态为新增文件
                entity.setFileStatus(FileEntity.FileStatus.NewFile);
                return false;
            }

        } catch (Exception e) {
            delegate.onError(Utils.errorMessage(e.getMessage(), NAME), "83");
        }
        return true;
    }

    /**
     * 对entity对应的实体进行验签，若验签失败则需要发送请求重新下载
     *
     * @param entity
     * @param delegate
     * @return
     */
    private boolean checkSignIn(FileEntity entity, FileUpgradeDelegate delegate){
        boolean isComplete = true;
        if (entity.isVerifySign()){
            //验签
            isComplete = CheckSign.checkSign(entity.getFile());
            //验签失败,删除当前文件,并拷贝app预装assets中的该文件,若assets中没有该文件则暂不处理
            if (!isComplete){
                Utils.deleteFile(entity.getFilePath());
                boolean isExist = validateFile(entity, delegate);
                //设置文件状态，若预装中存在该文件，那么状态为验证无效但是文件存在，反之为验证无效并且文件不存在
                entity.setFileStatus(isExist ? FileEntity.FileStatus.InvalidateButExistFile
                                             : FileEntity.FileStatus.InvalidateAndNotExistFile);
            }
        }
        return isComplete;
    }

    /**
     * 获取主配置文件中的内容，读取文件前会对文件进行验签，如果失败
     * 那么复制 assets 中预装的主配置文件
     *
     * @param delegate  代理
     * @return          JSONObject
     */
    private JSONObject mainConfig2Json(FileUpgradeDelegate delegate){
        JSONObject mainJson       = null;
        try {
            mainJson = new JSONObject(mainEntity.getConfigFileContent());
        } catch (Exception e) {
            delegate.onError(Utils.errorMessage(e.getMessage(), NAME), "91");
        }

        return mainJson;
    }

    /**
     * 获取实体对应的文件内容 --> Json
     *
     * @param entity        Entity
     * @param delegate      模块代理
     * @return              JSONObject
     */
    private JSONObject getFileContent2Json(FileEntity entity, FileUpgradeDelegate delegate){
        if (entity == null){
            delegate.onError(Utils.errorMessage("FileEntity 不能为空!", NAME), "105");
            return null;
        }

        String filePath       = entity.getFilePath();
        String entityContent  = null;
        JSONObject entityJson = null;
        try {
            entityContent = Utils.readFile(filePath);

            if (Utils.isEmpty(entityContent)){
                delegate.onError(Utils.errorMessage("FileEntity 对应的文件内容为 null.", NAME), "");
                return null;
            }

            entityJson = new JSONObject(entityContent);

        } catch (IOException e) {
            delegate.onError(Utils.errorMessage(e.getMessage(), NAME), "89");
        } catch (JSONException e) {
            delegate.onError(Utils.errorMessage(e.getMessage(), NAME), "");
        }

        return entityJson;
    }

    /**
     * 获取主配置文件中的一级配置实体
     *
     * @param tag0        一级 tag
     * @param delegate    模块代理
     * @return            FileUpgrade
     */
     FileEntity getFileEntity(String tag0, FileUpgradeDelegate delegate){
        JSONObject mainJson = mainConfig2Json(delegate);
        if (mainJson == null){
            delegate.onError(Utils.errorMessage("主配置文件为 null.", NAME), "100");
            return null;
        }

        //获取主配置文件中的子
        JSONObject child = mainJson.optJSONObject(Keys.CHILD_KEY);
        if (child == null){
            delegate.onError(Utils.errorMessage("主配置文件中没有 config 属性.", NAME), "107");
            return null;
        }

        JSONObject tag0JSONObj = child.optJSONObject(tag0);
        if (tag0JSONObj == null){
            delegate.onError(Utils.errorMessage(String.format("主配置文件中的子配置项中，没有找到 %s 对应的实体.", tag0), NAME), "113");
            return null;
        }

        FileEntity entity = new FileEntity(tag0, tag0JSONObj);
        entity.setParentTag(FileMainEntity.TAG);

        return entity;
    }

    /**
     * 获取二级配置实体
     *
     * @param tag0       一级 tag
     * @param tag1       二级 tag
     * @param delegate   模块代理
     * @return           FileEntity
     */
     FileEntity getFileEntity(String tag0, String tag1, FileUpgradeDelegate delegate){
        //如果 tag1 为空，则返回 tag0 对应的实体
        if (Utils.isEmpty(tag1)){
            delegate.onError(Utils.errorMessage("在获取二级实体的时候，tag1 不能为空.", NAME), "128");
            return null;
        }

        FileEntity tag0Entity = getFileEntity(tag0, delegate);
        if (tag0Entity == null){
            delegate.onError(Utils.errorMessage(String.format("获取主配置文件中 %s 对应的实体为 null.", tag0), NAME), "123");
            return null;
        }

        JSONObject tag0Json = getFileContent2Json(tag0Entity, delegate);
        if (tag0Json == null){
            delegate.onError(Utils.errorMessage(String.format("获取主配置文件中 %s 对应实体的内容为 null.", tag0), NAME), "");
            return null;
        }

        JSONObject chile = tag0Json.optJSONObject(Keys.CHILD_KEY);
        if (chile == null){
            delegate.onError(Utils.errorMessage(String.format("%s 对应的配置文件中没有 config 属性.", tag0), NAME), "");
            return null;
        }

        JSONObject tag1Json = chile.optJSONObject(tag1);
        if (tag1Json == null){
            delegate.onError(Utils.errorMessage(String.format("%s 对应的配置文件中，没有找到 %s 对应的实体.", tag0, tag1), NAME), "");
            return null;
        }

        FileEntity entity = new FileEntity(tag1, tag1Json);
        entity.setParentTag(tag0);

        return entity;
    }

}
