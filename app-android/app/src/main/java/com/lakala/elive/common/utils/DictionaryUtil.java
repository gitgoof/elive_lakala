package com.lakala.elive.common.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.baidu.mapapi.map.Text;
import com.google.gson.Gson;
import com.lakala.elive.beans.AreaDataBean;
import com.lakala.elive.beans.MccDataBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典存储类
 * Created by gaofeng on 2017/4/17.
 * 开始：
 * 1。 首界面进行初始化。
 * 2。 流程结束，对数据进行清空
 */
public class DictionaryUtil {
    private final String JIN_JIAN = "data_jinjian";//进件存储
    private final String DATA_DICTIONARY = "data_dictionary";// 数据字典存储 省存储
    private final String DATA_CITY_LIST = "data_city_list";// 数据字典存储 市存储
    private final String DATA_AREA_LIST = "data_area_list";// 数据字典存储 县存储
    private final String DATA_OTHER_DICTS_MAP = "data_other_dicts_map";// 数据字典存储 其他类型

    private final String KEY_PROVINCE_VALUE = "key_province_value";// 城市信息
    private final String KEY_CITY_VALUE = "key_city_value";// 城市信息
    private final String KEY_AREA_VALUE = "key_area_value";// 城市信息

    private final String DATA_MCC_BIG = "data_mcc_big";
    private final String DATA_MCC_CENTER = "data_mcc_center";
    private final String DATA_MCC_SMALL = "data_mcc_small";

    private final String KEY_MCC_BIG = "key_mcc_big";
    private final String KEY_MCC_CENTER = "key_mcc_center";
    private final String KEY_MCC_SMALL = "key_mcc_small";

    public static final String NEW_DEFAULT_KEY = "new_default_key";
    private final static DictionaryUtil mDictionaryUtil = new DictionaryUtil();
    public static DictionaryUtil getInstance(){
        return mDictionaryUtil;
    }

    private Application mApplication;
    public void init(Application application){
        mApplication = application;
    }
    public Application getmApplication(){
        return mApplication;
    }

    public void saveOtherDictsMap(String type,Map<String,String> map){
        if(TextUtils.isEmpty(type)){
            return;
        }
        DataForSave save = new DataForSave();
        save.setOtherDicsMap(map);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_OTHER_DICTS_MAP,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(type,valueStr);
        editor.commit();
    }
    public Map<String,String> getOtherDictMap(String type){
        if(TextUtils.isEmpty(type)){
            return null;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_OTHER_DICTS_MAP,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(type,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            return obj.getOtherDicsMap();
        }
        return null;
    }
    public ArrayList<String> getOtherDictList(String type){
        if(TextUtils.isEmpty(type))return null;
        return getStringFromMap(getOtherDictMap(type));
    }
    public Map<String,String> getMapValueByType(String type){
        if(TextUtils.isEmpty(type))return null;
        return getMapFromMap(getOtherDictMap(type));
    }
    private ArrayList<String> getStringFromMap(Map<String,String> map){
        if(map == null)return null;
        ArrayList<String> list = new ArrayList<String>();
        for(String key:map.keySet()){
            list.add(map.get(key));
        }
        return list;
    }
    private Map<String,String> getMapFromMap(Map<String,String> map){
        if(map == null)return null;
        Map<String,String> rMap = new HashMap<String,String>();
        for(String key:map.keySet()){
            rMap.put(map.get(key),key);
        }
        return rMap;
    }

    public void saveMccBig(List<MccDataBean.MccInfoItem> object){
        DataForSave save = new DataForSave();
        save.setMccList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_BIG,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MCC_BIG,valueStr);
        editor.commit();
    }
    private final List<MccDataBean.MccInfoItem> mMccBigList = new ArrayList<MccDataBean.MccInfoItem>();
    public List<String> getMccBigStrList(){
        getMccBig();
        List<String> list = new ArrayList<String>();
        if(mMccBigList.size() == 0){
            return list;
        }
        for(MccDataBean.MccInfoItem mccInfoItem:mMccBigList){
            list.add(mccInfoItem.getMccName());
        }
        return list;
    }
    public String getMccBigCode(int code){
        if(code >= mMccBigList.size()|| code <0){
            return "";
        }
        return mMccBigList.get(code).getMccCode();
    }
    public List<MccDataBean.MccInfoItem> getMccBig(){
        if(mMccBigList.size() != 0){
            return mMccBigList;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_BIG,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_MCC_BIG,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            mMccBigList.addAll(obj.getMccList());
            return mMccBigList;
        }
        return null;
    }

    public void saveMccCenter(List<MccDataBean.MccInfoItem> object){
        DataForSave save = new DataForSave();
        save.setMccList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_CENTER,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MCC_CENTER,valueStr);
        editor.commit();
    }
    private final List<MccDataBean.MccInfoItem> mMccCenterList = new ArrayList<MccDataBean.MccInfoItem>();
    public List<String> getMccCenterStrList(String code){
        getMccCenter(code);
        List<String> list = new ArrayList<String>();
        if(mMccCenterList.size() == 0){
            return list;
        }
        for(MccDataBean.MccInfoItem mccInfoItem:mMccCenterList){
            list.add(mccInfoItem.getMccName());
        }
        return list;
    }
    public String getMccCenterCode(int code){
        if(code >= mMccCenterList.size()|| code <0){
            return "";
        }
        return mMccCenterList.get(code).getMccCode();
    }
    public List<MccDataBean.MccInfoItem> getMccCenter(String code){
        if(mMccCenterList.size() != 0){
            String parent = mMccCenterList.get(0).getParentMcc();
            if(!TextUtils.isEmpty(parent)&& parent.equals(code)){
                return mMccCenterList;
            }
            mMccCenterList.clear();
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_CENTER,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_MCC_CENTER,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            List<MccDataBean.MccInfoItem> list = obj.getMccList();
            for(MccDataBean.MccInfoItem item:list){
                String parent = item.getParentMcc();
                if(!TextUtils.isEmpty(parent)&& parent.equals(code)){
                    mMccCenterList.add(item);
                }
            }
            return mMccCenterList;
        }
        return null;
    }

    public void saveMccSmall(List<MccDataBean.MccInfoItem> object){
        DataForSave save = new DataForSave();
        save.setMccList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_SMALL,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MCC_SMALL,valueStr);
        editor.commit();
    }
    private final List<MccDataBean.MccInfoItem> mMccSmallList = new ArrayList<MccDataBean.MccInfoItem>();
    public List<String> getMccSmallStrList(String code){
        getMccSmall(code);
        List<String> list = new ArrayList<String>();
        if(mMccSmallList.size() == 0){
            return list;
        }
        for(MccDataBean.MccInfoItem mccInfoItem:mMccSmallList){
            list.add(mccInfoItem.getMccName());
        }
        return list;
    }
    public String getMccSmallCode(int code){
        if(code >= mMccSmallList.size()|| code <0){
            return "";
        }
        return mMccSmallList.get(code).getMccCode();
    }
    public List<MccDataBean.MccInfoItem> getMccSmall(String code){
        if(mMccSmallList.size() != 0){
            String parent = mMccSmallList.get(0).getParentMcc();
            if(!TextUtils.isEmpty(parent)&& parent.equals(code)){
                return mMccSmallList;
            }
            mMccSmallList.clear();
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_MCC_SMALL,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_MCC_SMALL,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            List<MccDataBean.MccInfoItem> list = obj.getMccList();
            for(MccDataBean.MccInfoItem item:list){
                String parent = item.getParentMcc();
                if(!TextUtils.isEmpty(parent)&& parent.equals(code)){
                    mMccSmallList.add(item);
                }
            }
            return mMccSmallList;
        }
        return null;
    }

    /**
     * 存储城市数据
     * @param string
     */
    private void setProvinceData(String string){
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_DICTIONARY,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROVINCE_VALUE,string);
        editor.commit();
    }
    /**
     * 数据字典存储
     * @return
     */
    public void saveProvinceData(List<AreaDataBean.AreaInfoItem> object){
        DataForSave save = new DataForSave();
        save.setAreaList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        setProvinceData(valueStr);
    }
    private final List<AreaDataBean.AreaInfoItem> mProvinceList = new ArrayList<AreaDataBean.AreaInfoItem>();
    public List<String> getProvinceStrList(){
        getProvinceValue();
        List<String> list = new ArrayList<String>();
        if(mProvinceList.size() == 0){
            return list;
        }
        for(AreaDataBean.AreaInfoItem areaInfoItem:mProvinceList){
            list.add(areaInfoItem.getAreaName());
        }
        return list;
    }
    public String getProvinceCode(int code){
        if(code >= mProvinceList.size()|| code <0){
            return "";
        }
        return mProvinceList.get(code).getAreaCode();
    }
    /**
     * 数据字典的获取。城市信息
     * @return
     */
    public List<AreaDataBean.AreaInfoItem> getProvinceValue(){
        if(mProvinceList.size() != 0){
            return mProvinceList;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_DICTIONARY,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_PROVINCE_VALUE,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            for(AreaDataBean.AreaInfoItem areaInfoItem:obj.getAreaList()){
                if(areaInfoItem.getAreaCode().equals("000000")){
                    continue;
                }
                mProvinceList.add(areaInfoItem);
            }
            return mProvinceList;
        }
        return null;
    }

    private final List<AreaDataBean.AreaInfoItem> mCityList = new ArrayList<AreaDataBean.AreaInfoItem>();
    public List<String> getCityStrList(String code){
        getCityListValue(code);
        List<String> list = new ArrayList<String>();
        if(mCityList.size() == 0){
            return list;
        }
        for(AreaDataBean.AreaInfoItem areaInfoItem:mCityList){
            list.add(areaInfoItem.getAreaName());
        }
        return list;
    }
    public String getCityCode(int code){
        if(code >= mCityList.size()|| code <0){
            return "";
        }
        return mCityList.get(code).getAreaCode();
    }
    /**
     * 获取市列表
     * @param provinceCode
     * @return
     */
    public List<AreaDataBean.AreaInfoItem> getCityListValue(String provinceCode){
        if(mCityList.size() != 0){
            String province = mCityList.get(0).getProvCode();
            if(!TextUtils.isEmpty(province)&& province.equals(provinceCode)){
                return mCityList;
            }
            mCityList.clear();
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_CITY_LIST,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_CITY_VALUE,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            List<AreaDataBean.AreaInfoItem> list = obj.getAreaList();
            for(AreaDataBean.AreaInfoItem item:list){
                String province = item.getProvCode();
                if(!TextUtils.isEmpty(province)&& province.equals(provinceCode)){
                    mCityList.add(item);
                }
            }
            return mCityList;
        }
        return null;
    }

    /**
     * 存储市列表
     * @param object
     */
    public void saveCityList(List<AreaDataBean.AreaInfoItem> object){
        DataForSave save = new DataForSave();
        save.setAreaList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_CITY_LIST,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CITY_VALUE,valueStr);
        editor.commit();
    }
    private final List<AreaDataBean.AreaInfoItem> mAreaList = new ArrayList<AreaDataBean.AreaInfoItem>();
    public List<String> getAreaStrList(String code){
        getAreaListValue(code);
        List<String> list = new ArrayList<String>();
        if(mAreaList.size() == 0){
            return list;
        }
        for(AreaDataBean.AreaInfoItem areaInfoItem:mAreaList){
            list.add(areaInfoItem.getAreaName());
        }
        return list;
    }
    public String getAreaCode(int code){
        if(code >= mAreaList.size()|| code <0){
            return "";
        }
        return mAreaList.get(code).getAreaCode();
    }
    /**
     * 获取县列表
     * @param cityCode
     * @return
     */
    public List<AreaDataBean.AreaInfoItem> getAreaListValue(String cityCode){
        if(mAreaList.size() != 0){
            String province = mAreaList.get(0).getCityCode();
            if(!TextUtils.isEmpty(province)&& province.equals(cityCode)){
                return mAreaList;
            }
            mAreaList.clear();
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_AREA_LIST,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(KEY_AREA_VALUE,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        DataForSave obj = gson.fromJson(valueStr,DataForSave.class);
        if(obj != null){
            for (AreaDataBean.AreaInfoItem item:obj.getAreaList()){
                String city = item.getCityCode();
                if(!TextUtils.isEmpty(city)&& city.equals(cityCode)){
                    mAreaList.add(item);
                }
            }
            return mAreaList;
        }
        return null;
    }

    /**
     * 存储县列表
     * @param object
     */
    public void saveAreaList(List<AreaDataBean.AreaInfoItem> object){
        DataForSave save = new DataForSave();
        save.setAreaList(object);
        String valueStr = getStringFromObj(save);
        if(TextUtils.isEmpty(valueStr)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(DATA_AREA_LIST,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AREA_VALUE,valueStr);
        editor.commit();
    }

    public void destroyListData(){
        mProvinceList.clear();
        mCityList.clear();
        mAreaList.clear();
    }
    private String getStringFromObj(Object object){
        if(object == null){
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(object);
    }
    public void saveData(String key,Object value){
        String valueStr = getStringFromObj(value);
        if(TextUtils.isEmpty(valueStr)||TextUtils.isEmpty(key)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(JIN_JIAN,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,valueStr);
        editor.commit();
    }
    public <T> T getValue(String key,Class<T> value){
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(JIN_JIAN,Application.MODE_PRIVATE);
        String valueStr = sharedPreferences.getString(key,null);
        if(TextUtils.isEmpty(valueStr)){
            return null;
        }
        Gson gson = new Gson();
        T obj = gson.fromJson(valueStr,value);
        return obj;
    }
    public void clearDataByKey(String key){
        if(TextUtils.isEmpty(key)){
            return;
        }
        SharedPreferences sharedPreferences = mApplication.getSharedPreferences(JIN_JIAN,Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
    public static class DataForSave{
        private Map<String,String> maps = new HashMap<String,String>();
        public Map<String, String> getMaps() {
            if(maps==null){
                return new HashMap<String,String>();
            }
            return maps;
        }
        public void setMaps(Map<String, String> maps) {
            this.maps = maps;
        }
        private Map<String,String> otherDicsMap = new HashMap<>();

        public Map<String, String> getOtherDicsMap() {
            return otherDicsMap;
        }

        public void setOtherDicsMap(Map<String, String> otherDicsMap) {
            this.otherDicsMap = otherDicsMap;
        }

        private List<AreaDataBean.AreaInfoItem> areaList = new ArrayList<AreaDataBean.AreaInfoItem>();
        public List<AreaDataBean.AreaInfoItem> getAreaList() {
            return areaList;
        }
        public void setAreaList(List<AreaDataBean.AreaInfoItem> areaList) {
            this.areaList = areaList;
        }
        private List<MccDataBean.MccInfoItem> mccList = new ArrayList<MccDataBean.MccInfoItem>();

        public List<MccDataBean.MccInfoItem> getMccList() {
            return mccList;
        }

        public void setMccList(List<MccDataBean.MccInfoItem> mccList) {
            this.mccList = mccList;
        }
    }


}
