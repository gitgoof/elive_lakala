package com.lakala.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.ui.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangMY on 2015/3/22.
 */
public class AreaSelecteDialog extends android.app.AlertDialog.Builder {

    private Spinner spinnerProvice;
    private Spinner spinnerCity;
    private Spinner spinnerArea;

    private List<AreaInfo> allAreaInfos;

    private AreaInfo curentProvice;
    private AreaInfo currentCity;
    private AreaInfo currentArea;

    public AreaSelecteDialog(Context context) {
        super(context);
        init(context);
    }

    public AreaSelecteDialog(Context context,List<AreaInfo> allAreaInfos){
        super(context);
        this.allAreaInfos = allAreaInfos;
        init(context);
    }

    public void init(Context context){
        setTitle("请选择地区");
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.ui_alert_area_select_dialog, null);

        spinnerProvice = (Spinner) rootView.findViewById(R.id.spinner_provice);
        spinnerCity = (Spinner) rootView.findViewById(R.id.spinner_city);
        spinnerArea = (Spinner) rootView.findViewById(R.id.spinner_area);

        if(null != allAreaInfos){
            initSpinner();
        }
        setView(rootView);
    }


    private void initSpinner(){
        AreaSpinnerAdapter priviceAdapter = new AreaSpinnerAdapter(allAreaInfos);
        spinnerProvice.setAdapter(priviceAdapter);//省
        spinnerProvice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                curentProvice = allAreaInfos.get(i);
                final List<AreaInfo> cityInfo = curentProvice.getChildInfos();
                if(null != cityInfo&& cityInfo.size()>0){
                    spinnerCity.setAdapter(new AreaSpinnerAdapter(cityInfo));//市
                    spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            currentCity = cityInfo.get(i);
                            final List<AreaInfo> areaInfos = currentCity.getChildInfos();
                            if(null != areaInfos && areaInfos.size()>0){
                                spinnerArea.setAdapter(new AreaSpinnerAdapter(areaInfos));//地区
                                spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        currentArea = areaInfos.get(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setAllAreaInfos(List<AreaInfo> allAreaInfos){
        this.allAreaInfos = allAreaInfos;
    }

    public String getCurrentCityInfo()throws Exception{
        JSONObject area = packAreaInfo(currentArea);
        JSONObject city = packAreaInfo(currentCity);
        JSONObject provice = packAreaInfo(curentProvice);

        city.put("children",area);
        provice.put("children",city);
        return provice.toString();
    }

    public JSONObject packAreaInfo(AreaInfo areaInfo)throws Exception{
        JSONObject area = new JSONObject();
        if(null != areaInfo){
            area.put("areaName",areaInfo.getText());
            area.put("idx",areaInfo.getValue());
            area.put("areaCode",areaInfo.getCode());
            area.put("leaf",areaInfo.isLeaf());
        }
        return area;
    }

    class AreaSpinnerAdapter extends BaseAdapter{

        private List<AreaInfo> areaInfos;

        public AreaSpinnerAdapter(List<AreaInfo> areaInfos){
            this.areaInfos = areaInfos;
        }

        @Override
        public int getCount() {
            return areaInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return areaInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(null == view){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ui_layout_area_dialog_item,null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) view.findViewById(R.id.tv_area_name);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            AreaInfo areaInfo = (AreaInfo) getItem(i);
            viewHolder.tvName.setText(areaInfo.getText());
            return view;
        }

        class ViewHolder{
            TextView tvName;
        }
    }

    public static class AreaInfo{
        public String text;
        public String code;
        public String value;
        public String phoneCode;
        public boolean leaf;

        public boolean isLeaf() {
            return leaf;
        }

        public void setLeaf(boolean leaf) {
            this.leaf = leaf;
        }

        public List<AreaInfo> childInfos;

        public String getPhoneCode() {
            if(TextUtils.isEmpty(phoneCode)){
                return "";
            }
            return phoneCode;
        }

        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }


        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<AreaInfo> getChildInfos() {
            if(null == childInfos){
                childInfos = new ArrayList<AreaInfo>();
            }
            return childInfos;
        }

        public void setChildInfos(List<AreaInfo> childInfos) {
            this.childInfos = childInfos;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }


    }

}
