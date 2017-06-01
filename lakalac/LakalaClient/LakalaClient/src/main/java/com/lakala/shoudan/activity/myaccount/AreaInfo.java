package com.lakala.shoudan.activity.myaccount;

/**
 * 地区类
 * @author zmy
 *
 */
public class AreaInfo{

    private String name;

    private String code;

    private AreaInfo child;

    public AreaInfo(){}

    public AreaInfo getChild() {
        if(child == null){
            child = new AreaInfo();
        }
        return child;
    }

    public void setChild(AreaInfo child) {
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "AreaInfo{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", child=" + child  +
                '}';
    }

    public String getInfo(){
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        if(child.getName() != null)
        {
//            sb.append("-");
            sb.append(child.getName());
            if(child.getChild().getName()!= null && !child.getChild().getName().equals("")){
//                sb.append("-");
                sb.append(child.getChild().getName());
            }
        }
        return sb.toString();
    }

}