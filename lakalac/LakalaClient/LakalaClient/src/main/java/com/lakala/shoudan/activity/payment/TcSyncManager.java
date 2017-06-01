package com.lakala.shoudan.activity.payment;


import android.os.Handler;
import android.os.Message;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.loopj.lakala.http.RequestParams;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * 同步数据库中的历史上送失败的TC
 *
 * Created by More on 14-7-15.
 * Constructor should call in UI Thread
 */
public class TcSyncManager {

    TcDatabase dbManager;

    private Map<Integer, String> tcContents;

    private TcSyncListener tcSyncListener;

    public interface TcSyncListener{
        void onFinish();
    }

    public TcSyncListener getTcSyncListener() {
        return tcSyncListener;
    }

    public void setTcSyncListener(TcSyncListener tcSyncListener) {
        this.tcSyncListener = tcSyncListener;
    }

    public TcSyncManager() {
        initManger();
    }

    /**
     * 获取上送失败的tc相关信息
     */
    public void initManger(){
        dbManager  = TcDatabase.getInstance();
        tcContents = dbManager.getTcList(ApplicationEx.getInstance().getSession().getUser().getLoginName());
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ASYNC_TC_SUCCESS:
                    int id = (Integer)msg.obj;
                    if(dbManager == null){
                        dbManager = TcDatabase.getInstance();
                    }
                    dbManager.deleteTcById(id);
                    break;
            }
        }
    };


    private int preDd;

    private void upload(final int key, final Iterator<Integer> iterator){

        String[] sons = tcContents.get(key).split("_");
        String busId = sons[0].replace("*", "");
        String icc55 = sons[1].replace("*", "");
        String scpic55 = sons[2].replace("*", "");
        String tcValue = sons[3].replace("*", "");
        String srcSid = sons[4].replace("*", "");
        String termId = sons[5].replace("*", "");
        //String sytm, String sysref, String acinstcode
        String sytm = sons[6].replace("*", "");
        String sysref = sons[7].replace("*", "");
        String acinstcode = sons[8].replace("*", "");

        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/trade", HttpRequest.RequestMethod.POST);
        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("busid", "TCCHK");
        requestParams.put("termid", termId);
        requestParams.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("cardtype", "2");
        requestParams.put("series", Utils.createSeries());
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("tcicc55", icc55);
        requestParams.put("scpic55", scpic55);
        requestParams.put("tcvalue", tcValue);
        requestParams.put("sid", srcSid);
        requestParams.put("tc_asyflag", 0);
        requestParams.put("posemc", "1");
        requestParams.put("hsmtrade", "02");
        requestParams.put("acinstcode", acinstcode);
        requestParams.put("sytm", sytm);
        requestParams.put("sysref", sysref);

        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {


                Message message = new Message();
                message.what = ASYNC_TC_SUCCESS;
                message.obj = key;
                handler.sendMessage(message);


                if(iterator.hasNext()){
                    int key  = iterator.next();

                    upload(key, iterator);
                }else{
                    finish();
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                if(tcSyncListener != null){
                    tcSyncListener.onFinish();
                }

            }
        });

        businessRequest.execute();


    }

    private void finish(){
        if(tcSyncListener != null){
            tcSyncListener.onFinish();
        }

    }


    public void tcSyncService(){

        if(tcContents.size() == 0){
            finish();
            return;
        }

        Iterator<Integer> iterator = tcContents.keySet().iterator();


        if(!iterator.hasNext()){
            finish();
            return;
        }

        upload(iterator.next(), iterator);

    }

    private static final int ASYNC_TC_SUCCESS = 0;

    /**
     *
     * tc需要用的数据以下划线 分割保存 至数据库中
     *
     * @param tcBusId
     * @param icc55
     * @param scpic55
     * @param tcValue
     * @param srcSid
     * @param termId
     * @param sytm
     * @param sysref
     * @param acinstcode
     */
    public void saveTcInfo(String tcBusId, String icc55, String scpic55, String tcValue, String srcSid, String termId,
                           String sytm, String sysref, String acinstcode){
        String tcContent = tcBusId + "_" + checkValue(icc55) + "_" + checkValue(scpic55)
                + "_" + checkValue(tcValue) + "_" + checkValue(srcSid )+ "_" + checkValue(termId)
                + "_" + checkValue(sytm) + "_" + checkValue(sysref) + "_" + checkValue(acinstcode);

        if(dbManager == null){
            dbManager = TcDatabase.getInstance();
        }
        dbManager.saveTc(tcContent);


    }

    private String checkValue(String value){
        return "".equals(value)? "*" : value;
    }



}
