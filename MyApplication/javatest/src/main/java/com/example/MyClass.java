package com.example;

import java.util.ArrayList;
import java.util.List;

public class MyClass {
    public static void main(String[] args) {
        test1();
    }
    private static void test1(){

        final ArrayList<DataBean> list = new ArrayList<>();
        for (int i = 0;i < 10 ;i++){
            DataBean bean = new DataBean();
            bean.name = "name:" + i;
            bean.index = i+1;
            list.add(bean);
        }
        test2(list);

        for(DataBean bean:list){
            System.out.println("输出:" + bean.name);
        }
    }
    private static void test2(List<DataBean> list){
        for(int i = 0;i < list.size();i++){
            DataBean bean = list.get(i);
            bean.name = "nameSec:" + i;
        }
        test3(list);
    }

    private static void test3(List<DataBean> list){

        ArrayList<DataBean> arrayList = new ArrayList<DataBean>();
        arrayList.addAll(list);
        for(int i = 0; i < arrayList.size();i++){

            DataBean bean = arrayList.get(i);
            bean.name = "nameThr:" + i;

        }

    }

    static class DataBean{
        String name = "zhangyi";
        int index = 0;
    }
}
