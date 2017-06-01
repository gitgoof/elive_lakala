package com.test.gf.inttest;

/**
 * Created by gaofeng on 2017/4/6.
 */

public class OutBeanMain {
    public static void main(String[] args) {
        ValueBean valueBean = new ValueBean();

//        System.out.println("---init--" + valueBean);

//        test1(valueBean);

        test2();
    }

    private static void test1(ValueBean valueBean){
        System.out.println("method-one:" + valueBean.toString());
        valueBean = new ValueBean();
        System.out.println("method:" + valueBean.toString());
    }

    private static void test2(){
        String string = "test2";
        test3(string);
        System.out.println("test out:" + string);
    }

    private static void test3(String string){
        System.out.println("输出1:" + string);
        string = "test3";
        System.out.println("输出2:" + string);


    }
}
