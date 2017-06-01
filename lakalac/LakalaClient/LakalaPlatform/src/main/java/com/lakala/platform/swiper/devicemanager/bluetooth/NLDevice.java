package com.lakala.platform.swiper.devicemanager.bluetooth;


public class NLDevice implements Comparable<Object> {
    /**
     * 设备名
     */
    private String name;
    /**
     * 设备 mac 地址
     */
    private String macAddress;
    /**
     * 是否是默认连接设备
     */
    private boolean isDefault = false;
    /**
     * 连接方式
     */
    private ConnectType connectType;

    public NLDevice(String name, String mac, ConnectType type) {
        this.name = name;
        this.macAddress = mac;
        this.connectType = type;
    }

    public NLDevice() {

    }

    /**
     * 获取设备名
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置设备名
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取设备连接类型
     *
     * @return
     */
    public ConnectType getConnectType() {
        return connectType;
    }

    /**
     * 获取设备连接类型
     *
     * @param connectType
     */
    public void setConnectType(ConnectType connectType) {
        this.connectType = connectType;
    }

    /**
     * 获取设备是否为默认连接
     *
     * @return
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * 设置为默认连接设备
     *
     * @param isDefault
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public int compareTo(Object s) {
        NLDevice temp = (NLDevice) s;
        return (temp.getMacAddress().compareTo(macAddress));
    }

    /**
     * 获取设备 mac
     *
     * @return
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * 设置 mac
     *
     * @param macAddress
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public int hashCode() {

        return this.macAddress.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof NLDevice) {

            if(this.macAddress == null || this.name == null){
                return false;
            }

            NLDevice temp = (NLDevice) o;
            if (this.macAddress.equals(temp.getMacAddress()) && this.name.equals(temp.getName())) {
                result = true;
            }

        }
        return result;
    }

    public void clearData() {
        name = "unknown";
        macAddress = "unknown";
        isDefault = false;
        connectType = null;
    }

    public NLDevice(String nlDevice) {

        String[] strings = nlDevice.split(SPLITER);
        if(strings.length <4){
            throw new IllegalArgumentException("String cant turn to NlDevice, length error:" + nlDevice);
        }
        name = strings[0];
        macAddress = strings[1];
        isDefault = new Boolean(strings[2]);
        connectType = "BLUETOOTH".equals(strings[3]) ? ConnectType.BLUETOOTH : ConnectType.AUDIO;

    }

    public static final String SPLITER = "FUCK";

    @Override
    public String toString() {
        String result = name + SPLITER  + macAddress +
                SPLITER + isDefault +  SPLITER + connectType;


        return result;
    }
}