package com.lakala.elive.common.utils;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.lakala.elive.Constants;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.net.resp.DictDetailRespInfo;
import com.lakala.elive.common.net.resp.MerDictionaryResp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 请求其他数据字典信息
 * Created by gaofeng on 2017/4/20.
 */
public class RequestAllDictDataService extends IntentService implements ApiRequestListener {
    private final int REQUEST_ALL_DICT_DATA = 0x1002;

    private final int REQUEST_DICT_YYMJ_DATA = 0x1003;
    private final int REQUEST_DICT_JYNR_DATA = 0x1004;
    private final int REQUEST_DICT_JSZQ_DATA = 0x1005;
    private final int REQUEST_DICT_ZDLQFS_DATA = 0x1006;
    private final int REQUEST_DICT_JX_DATA = 0x1007;

    private final int REQUEST_DICT_JSZQ_DS_DATA = 0x1008;
    private final int REQUEST_DICT_JSZQ_DG_DATA = 0x1009;

    public static final String DICTIONARY_ALL_SERVICE = "request_all_dictionary_service";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *  name Used to name the worker thread, important only for debugging.
     */
    public RequestAllDictDataService() {
        super(DICTIONARY_ALL_SERVICE);
    }
    private void loadCommonDict() {
        UserReqInfo reqInfo = new UserReqInfo();
        Session session = Session.get(getApplicationContext());
        if(session!=null && StringUtil.isNotNullAndBlank(session.getUserLoginInfo().getAuthToken())){
            reqInfo.setAuthToken(session.getUserLoginInfo().getAuthToken());
            reqInfo.setDictTypeCode(Constants.dictDataList);
            NetAPI.getAllDictDataList(this, this,reqInfo,REQUEST_ALL_DICT_DATA);
        }
    }
    private void requestInfoDic(){
        Session session = Session.get(getApplicationContext());
        if(session!=null && StringUtil.isNotNullAndBlank(session.getUserLoginInfo().getAuthToken())){
            //营业面积查询
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "SHOP_AREA"), REQUEST_DICT_YYMJ_DATA);
            //经营内容
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "MER_BIZ_CONTENT"), REQUEST_DICT_JYNR_DATA);

            //结算周期
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "SETTLE_PERIOD"), REQUEST_DICT_JSZQ_DATA);
            //结算周期 对公
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "SETTLE_PERIOD_PUBLIC"), REQUEST_DICT_JSZQ_DG_DATA);
            //结算周期 对私
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "SETTLE_PERIOD_PRIVATE"), REQUEST_DICT_JSZQ_DS_DATA);

            //终端领取方式
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "DEVICE_DRAW_METHOD"), REQUEST_DICT_ZDLQFS_DATA);
            // 机型
            NetAPI.merDictionaryReq(this, this,
                    new MerDictionaryReq(session.getUserLoginInfo().getAuthToken(),
                            "dic", "PARAM", "TERMINAL_TYPE"), REQUEST_DICT_JX_DATA);
        }

    }
    @Override
    protected void onHandleIntent(Intent intent) {
//        requestInfoDic();
        loadCommonDict();
    }
    @Override
    public void onSuccess(int method, Object obj) {
        switch (method){
            case REQUEST_ALL_DICT_DATA:
                DictDetailRespInfo dictDetailRespInfo = (DictDetailRespInfo) obj;
                Map<String,Map<String,String>> allDict = dictDetailRespInfo.getContent();
                for(String key:allDict.keySet()){
                    if(TextUtils.isEmpty(key))continue;
                    Map<String,String> map = allDict.get(key);
                    if(map != null && map.size() != 0){
                        DictionaryUtil.getInstance().saveOtherDictsMap(key,map);
                    }
                }
                break;
            case REQUEST_DICT_YYMJ_DATA:// 营业面积
                MerDictionaryResp yymj = (MerDictionaryResp) obj;
                saveDicts("SHOP_AREA",yymj.getContent().getItems());
                break;
            case REQUEST_DICT_JYNR_DATA:// 经营内容
                MerDictionaryResp jynr = (MerDictionaryResp) obj;
                saveDicts("MER_BIZ_CONTENT",jynr.getContent().getItems());
                break;
            case REQUEST_DICT_JSZQ_DATA:// 结算周期
                MerDictionaryResp jszq = (MerDictionaryResp) obj;
                saveDicts("SETTLE_PERIOD",jszq.getContent().getItems());
                break;
            case REQUEST_DICT_ZDLQFS_DATA:// 终端领取方式
                MerDictionaryResp zdlqfs = (MerDictionaryResp) obj;
                saveDicts("DEVICE_DRAW_METHOD",zdlqfs.getContent().getItems());
                break;
            case REQUEST_DICT_JX_DATA:// 机型
                MerDictionaryResp jx = (MerDictionaryResp) obj;
                saveDicts("TERMINAL_TYPE",jx.getContent().getItems());
                break;
            case REQUEST_DICT_JSZQ_DG_DATA:
                MerDictionaryResp dg = (MerDictionaryResp) obj;
                saveDicts("SETTLE_PERIOD_PUBLIC",dg.getContent().getItems());
                break;
            case REQUEST_DICT_JSZQ_DS_DATA:
                MerDictionaryResp ds = (MerDictionaryResp) obj;
                saveDicts("SETTLE_PERIOD_PRIVATE",ds.getContent().getItems());
                break;
        }
    }
    private void saveDicts(String type,List<MerDictionaryResp.ContentBean.ItemsBean> list){
        if(TextUtils.isEmpty(type))return;
        Map<String,String> maps = new HashMap<String,String>();
        for(MerDictionaryResp.ContentBean.ItemsBean itemsBean:list){
            String key = itemsBean.getId();
            String value = itemsBean.getValue();
            maps.put(itemsBean.getId(),itemsBean.getValue());
        }
        if(maps.size() ==0 )return;
        DictionaryUtil.getInstance().saveOtherDictsMap(type,maps);
    }
    @Override
    public void onError(int method, String statusCode) {
    }
}
