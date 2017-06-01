package com.lakala.platform.swiper.devicemanager.connection;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.devicemanager.bluetooth.NLDevice;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by More on 15/1/23.
 */
public class DeviceSp {
    private static DeviceSp ourInstance = new DeviceSp();

    private static final String KEY = "key";

    public static DeviceSp getInstance() {
        return ourInstance;
    }

    private ApplicationEx applicationEx = ApplicationEx.getInstance();

    private SharedPreferences sp;

    private SharedPreferences.Editor editor;

    private DeviceSp() {
        sp = PreferenceManager.getDefaultSharedPreferences(ApplicationEx.getInstance());
        editor = sp.edit();
    }

    private static final String divideStr = "KCUF";

    public NLDevice getDefaultDevice(){
        String allDevice = sp.getString(KEY + applicationEx.getSession().getUser().getLoginName(), "");
        if("".equals(allDevice)){
            return null;
        }
        String[] devices = allDevice.split(divideStr);

        for(String device : devices){
            if("".equals(device)){
                continue;
            }
            NLDevice temp = new NLDevice(device);
            if(getDefaultName().equals(temp.getName())){
                return temp;
            }
        }
        return null;
    }

    private static final String DEFAULT_NAME = "default_name";

    public void saveDeviceDefault(NLDevice nlDevice){
        saveDevice(nlDevice);
        editor.putString(DEFAULT_NAME, nlDevice.getName());
        editor.commit();
        return;
    }

    public boolean saveDevice(NLDevice nlDevice){

        String key = KEY + applicationEx.getSession().getUser().getLoginName();
        String allDevice = sp.getString(key, "");
        if(!"".equals(allDevice)){
            String[] devices = allDevice.split(divideStr);
            for(int i=0; i<devices.length; i++){
                if("".equals(devices[i])){
                    continue;
                }
                if(new NLDevice(devices[i]).equals(nlDevice)){
                    return true;
                }
            }
        }

        allDevice = allDevice + divideStr + nlDevice.toString();

        editor.putString(key,allDevice);
        editor.commit();
        return false;

    }

    public String getDefaultName(){

        return sp.getString(DEFAULT_NAME,"");
    }

    public Set<NLDevice> getAllDevices(){
        Set<NLDevice> nlDevices = new HashSet<NLDevice>();
        String key = KEY + applicationEx.getSession().getUser().getLoginName();
        String allDevice = sp.getString(key, "");
        if(!"".equals(allDevice)){
            String[] devices = allDevice.split(divideStr);
            for(String device : devices){
                if("".equals(device)){
                    continue;
                }
                nlDevices.add(new NLDevice(device));
            }
        }
        return nlDevices;
    }

    public void deleteDevice(NLDevice nlDevice){

        Set<NLDevice> nlDevices = getAllDevices();

        String key = KEY + applicationEx.getSession().getUser().getLoginName();
        StringBuffer stringBuffer = new StringBuffer();

        for(NLDevice tempDevice : nlDevices){
            if(!nlDevice.equals(tempDevice))
                stringBuffer.append(divideStr + tempDevice.toString());
        }

        editor.putString(key,stringBuffer.toString());
        editor.commit();

    }


}
