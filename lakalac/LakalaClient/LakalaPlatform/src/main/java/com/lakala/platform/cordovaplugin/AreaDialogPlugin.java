package com.lakala.platform.cordovaplugin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.lakala.core.cordova.cordova.CallbackContext;
import com.lakala.core.cordova.cordova.CordovaPlugin;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.R;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertListDialog;
import com.lakala.ui.dialog.AreaSelecteDialog;
import com.lakala.ui.dialog.AreaSelecteDialog.AreaInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZhangMY on 2015/3/22.
 * 地区对话框
 */
public class AreaDialogPlugin extends CordovaPlugin{

    public static final String AREADIALOG = "showAreaPicker";

    private static final String LISTDIALOG = "showCommonPicker";

    private static final String HIDEPICKER = "hidePicker";

    private static final String DATEPICKER = "showDatePicker";

    private final int DELAY_TIME    = 500;

    private FragmentManager fragmentManager;
    private FragmentActivity context;

    private boolean isDialogShowing;

    private List<AreaInfo> allAreaInfos;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context = (FragmentActivity) this.cordova.getActivity();
        fragmentManager = context.getSupportFragmentManager();

        if(action.equals(AREADIALOG)){
            if(!isDialogShowing){
                isDialogShowing = true;
                return showAreaInfos(args, callbackContext);
            }
        }

        if(action.equals(LISTDIALOG)){
            if (!isDialogShowing) {
                isDialogShowing = true;
                return showAlertList(args, callbackContext);
            }
        }

        if(action.equals(HIDEPICKER)){
            return hidePicker();
        }

        if(action.equals(DATEPICKER)){
            if(!isDialogShowing){
                isDialogShowing = true;
                return showDateDialog(args,callbackContext);
            }
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(allAreaInfos != null){
            allAreaInfos.clear();
            allAreaInfos = null;
        }
    }

    public boolean hidePicker(){
        return true;
    }

    public boolean showAreaInfos(JSONArray args, final CallbackContext callbackContext){
        try{
            if(allAreaInfos == null || allAreaInfos.size()<=0){//第一次调用 获取信息
                String str = null;
                if(args == null){
                    str = getResourceFile(cordova.getActivity());
                }else{
                    String fileName = args.getString(0);
                    str = getResourceFile(cordova.getActivity(),fileName);
                }
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.optJSONArray("retData");
                allAreaInfos = unpackJson(jsonArray);
                str = null;
                jsonObject = null;
                jsonArray = null;
            }

            cordova.getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run () {
                        try {
                            showAreaDialog(allAreaInfos, callbackContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                            isDialogShowing = false;
                        }
                    }
            });

        }catch (Exception e){
            e.printStackTrace();
            isDialogShowing = false;
        }
        return true;
    }
    public String getResourceFile(Activity activity){
        return getResourceFile(activity,"citylist.json");
    }
    public String getResourceFile(Activity activity, String resourceName) {
        StringBuffer stringBuffer = new StringBuffer();
        FileInputStream isn = null;
        try{

            File fileWithinMyDir = new File("/data/data/com.lakala.shoudan/files/assets/www/app/resource/", resourceName);

            isn = new FileInputStream(fileWithinMyDir);
            int i = isn.available();
            if(i>0){
                byte[] data = new byte[9120];
                int length = 0;
                while((length=isn.read(data))!=-1){
                    stringBuffer.append(new String(data));
                }
                return stringBuffer.toString();
            }
            return stringBuffer.toString();
        }catch(Exception e){
            LogUtil.print(e);
        }finally{
            if(null != isn){
                try {
                    isn.close();
                    isn = null;
                } catch (IOException e) {
                    LogUtil.print(e);
                }
            }
        }
        return stringBuffer.toString();
    }

    public boolean showAreaDialog(List<AreaInfo> allAreaInfo,final CallbackContext callbackContext) throws JSONException{

        final String btnSure  = context.getString(R.string.com_confirm);
        final String btnCancel = context.getString(R.string.com_cancel);

        final AreaSelecteDialog areaSelecteDialog = new AreaSelecteDialog(this.cordova.getActivity(),allAreaInfo);
        areaSelecteDialog.setCancelable(true);
        areaSelecteDialog.setNegativeButton(btnCancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isDialogShowing = false;
            }
        });
        areaSelecteDialog.setPositiveButton(btnSure,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isDialogShowing = false;
                try{
                    String result = areaSelecteDialog.getCurrentCityInfo();
                    if(null != callbackContext){
                     callbackContext.success(result);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    isDialogShowing = false;
                }

        }});
        android.app.AlertDialog dialog = areaSelecteDialog.create();
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);
        return true;
    }

    private List<AreaInfo> unpackJson(JSONArray data)throws JSONException{
        List<AreaInfo> allAreaInfos = new ArrayList<AreaInfo>();

        for(int i=0;i<data.length();i++){

            AreaInfo provice = new AreaInfo();
            JSONObject jsonObject = data.getJSONObject(i);

            provice.setText(jsonObject.optString("areaName"));
            provice.setCode(jsonObject.optString("areaCode"));
            provice.setValue(jsonObject.optString("idx"));
            provice.setLeaf(jsonObject.optBoolean("leaf"));

            if(!provice.isLeaf()){
                provice.setChildInfos(unpackJson(jsonObject.optJSONArray("children")));
            }

            allAreaInfos.add(provice);
        }

        return allAreaInfos;
    }


    /**
     * List对话框
     */
    public boolean showAlertList(final JSONArray args, final CallbackContext callbackContext){
        try {

            final List<AlertListDialog.ItemValue> itemNames = new ArrayList<AlertListDialog.ItemValue>();
//                    JSONArray jsonArray = args.getJSONArray(0);
            JSONArray jsonArray = new JSONArray(args.get(0).toString());
            final List<String> names = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AlertListDialog.ItemValue itemValue = new AlertListDialog.ItemValue();
                itemValue.code = jsonObject.getString("code");
                itemValue.name = jsonObject.getString("name");
                itemNames.add(itemValue);

                names.add(itemValue.name);
            }

            this.cordova.getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {

                    AlertListDialog mAlertListDialog = new AlertListDialog();
                    mAlertListDialog.setTitle("请选择");
                    mAlertListDialog.setListNames(names);
                    mAlertListDialog.setCancelable(true);
                    mAlertListDialog.setButtons(new String[]{cordova.getActivity().getResources().getString(R.string.com_cancel)});
                    mAlertListDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
                        @Override
                        public void onButtonClick(AlertDialog dialog, View view, int index) {
                            dialog.dismiss();
                            isDialogShowing = false;
                        }
                    });
                    mAlertListDialog.setListItemCLickListener(new AlertListDialog.ItemClickListener() {

                        @Override
                        public void onItemClick(AlertDialog dialog, int index) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                AlertListDialog.ItemValue mItemValue = itemNames.get(index);
                                jsonObject.put("code", mItemValue.code);
                                jsonObject.put("name", mItemValue.name);
                                if (null != callbackContext) {
                                    callbackContext.success(jsonObject.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mAlertListDialog.show(fragmentManager);

                 }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDialogShowing = false;
                }
            }, DELAY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return true;
        }
    }


    public boolean showDateDialog(final JSONArray jsonArray,final CallbackContext mCallbackContext){

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //[{"date":"2014-03-30","maxDate":"2015-03-30","minDate":"2014-03-30"}]
                    JSONArray dateArray = new JSONArray(jsonArray.toString());
                    JSONObject dateJson = dateArray.getJSONObject(0);
                    final String maxDateStr = dateJson.optString("maxDate") ;
                    final String minDateStr = dateJson.optString("minDate");
                    final String dateStr = dateJson.optString("date");

                    Calendar curCalendar = Calendar.getInstance(Locale.CHINA);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date curDate = sdf.parse(dateStr);
                    curCalendar.setTime(curDate);

                    final DatePickerDialog mDatePickerDialog = new DatePickerDialog(cordova.getActivity(),new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            try{
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                Calendar selected = Calendar.getInstance(Locale.CHINA);
                                selected.set(Calendar.YEAR,year);
                                selected.set(Calendar.MONTH,month);
                                selected.set(Calendar.DAY_OF_MONTH,day);
                                Date selectedDate = selected.getTime();

                                Date maxDate = null;
                                Date minDate = null;
                                if(!TextUtils.isEmpty(minDateStr)){
                                    minDate = sdf.parse(minDateStr);
                                }
                                if(!TextUtils.isEmpty(maxDateStr)){
                                    maxDate = sdf.parse(maxDateStr);
                                }
                                String result = sdf.format(selectedDate);
                                if(maxDate != null && selectedDate.getTime()-maxDate.getTime()>0){
                                    result = sdf.format(maxDate);
                                }else if(minDate != null && minDate.getTime()-selectedDate.getTime()>0){
                                    result = sdf.format(minDate);
                                }

                                if(null != mCallbackContext){
                                    mCallbackContext.success(result);
                                }

                                isDialogShowing = false;

                            }catch(Exception e){
                                e.printStackTrace();
                                isDialogShowing = false;
                            }
                        }
                    },curCalendar.get(Calendar.YEAR),curCalendar.get(Calendar.MONTH),curCalendar.get(Calendar.DAY_OF_MONTH));
                    mDatePickerDialog.setCancelable(true);
                    mDatePickerDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    isDialogShowing = false;
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDialogShowing = false;
            }
        }, DELAY_TIME);

        return true;
    }

}
