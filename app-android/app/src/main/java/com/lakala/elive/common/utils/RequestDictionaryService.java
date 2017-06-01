package com.lakala.elive.common.utils;

import android.app.IntentService;
import android.content.Intent;

import com.lakala.elive.beans.AreaDataBean;
import com.lakala.elive.beans.MccDataBean;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;

import java.util.List;

/**
 * 请求网络字典数据
 * Created by gaofeng on 2017/4/19.
 */
public class RequestDictionaryService extends IntentService implements ApiRequestListener{
    private final int REQUEST_ADDRESS_DATA = 0x1000;
    private final int REQUEST_MCC_DATA = 0x1001;
    public static final String DICTIONARY_SERVICE = "request_dictionary_service";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public RequestDictionaryService() {
        super(DICTIONARY_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO 进行网络请求
        toRequestMccData();
        toRequestAddressData();
    }
    private void toRequestAddressData(){
        List<AreaDataBean.AreaInfoItem> list = DictionaryUtil.getInstance().getProvinceValue();
        if(list == null || list.size() == 0){
        }
        NetAPI.getAddressDataRequest(this,this,REQUEST_ADDRESS_DATA);
    }
    private void toRequestMccData(){
        // TODO 判断是否需要请求MCC。 目前数据问题。没有版本判断
        List<MccDataBean.MccInfoItem> list = DictionaryUtil.getInstance().getMccBig();
        if(list == null || list.size() == 0){
        }
        NetAPI.getMccDataRequest(this,this,REQUEST_MCC_DATA);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method){
            case REQUEST_ADDRESS_DATA:
                AreaDataBean areaDataBean = (AreaDataBean) obj;
                if(areaDataBean == null){
                    break;
                }
                AreaDataBean.ContentBean contentBean = areaDataBean.getContent();
                if(contentBean == null){
                    break;
                }
                if(contentBean.getProInfoList() == null || contentBean.getProInfoList().size() == 0){
                    break;
                }
                if(contentBean.getCityInfoList() == null || contentBean.getCityInfoList().size() == 0){
                    break;
                }
                if(contentBean.getAreaInfoList() == null || contentBean.getAreaInfoList().size() == 0){
                    break;
                }
                DictionaryUtil.getInstance().saveProvinceData(contentBean.getProInfoList());
                DictionaryUtil.getInstance().saveCityList(contentBean.getCityInfoList());
                DictionaryUtil.getInstance().saveAreaList(contentBean.getAreaInfoList());
                break;
            case REQUEST_MCC_DATA:
                MccDataBean mccDataBean = (MccDataBean) obj;
                if(mccDataBean == null){
                    break;
                }
                MccDataBean.ContentBean mccContent = mccDataBean.getContent();
                if(mccContent.getBigMccList() == null || mccContent.getBigMccList().size() == 0){
                    break;
                }
                if(mccContent.getMccInfoList() == null || mccContent.getMccInfoList().size() == 0){
                    break;
                }
                if(mccContent.getSmallMccList() == null || mccContent.getSmallMccList().size() == 0){
                    break;
                }
                DictionaryUtil.getInstance().saveMccBig(mccContent.getBigMccList());
                DictionaryUtil.getInstance().saveMccCenter(mccContent.getSmallMccList());
                DictionaryUtil.getInstance().saveMccSmall(mccContent.getMccInfoList());
                break;
        }
    }
    @Override
    public void onError(int method, String statusCode) {
    }
}
