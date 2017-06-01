package com.lakala.shoudan.activity.myaccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/11/26.
 */
public class Region {


    String name;

    String code;

    public Region() {
    }

    List<Region> children = new ArrayList<Region>();

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

    public List<Region> getChildren() {
        return children;
    }

    public void setChildren(List<Region> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Region{" + "name='" + name + '\'' + ", code='" + code
                + '\'' + ", children count =" + children.size() + '}';
    }


}
