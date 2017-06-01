package com.javatest.gf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("systemout");
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
//        test7();
        test8();
    }

    static void test1(){
        List<String> list1 = new ArrayList<String>();
        for(int i = 0;i < 10;i ++){
            list1.add(">>" + i);
        }

        Iterator<String> iterator = list1.iterator();
        while (iterator.hasNext()){
            String string = iterator.next();
            if(string.equals(">>5")){
                iterator.remove();
                continue;
            }
        }

        for(String string:list1){
            System.out.println("---" + string);
        }
        /*for(String string:list1){
            if(string.equals(">>5")){
                list1.remove(string);
            }
            System.out.println("---" + string);
        }*/

    }

    static void test2(){

        String string = "大家AbC";
        System.out.println("<1>" + string.equals("大家abc"));
        System.out.println("<2>" + string.equalsIgnoreCase("大家aBc"));


    }


    static void test3(){
        String time = "2017-04-25";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = simpleDateFormat.parse(time);

            String time2 = simpleDateFormat.format(new Date());
            System.out.println("date.time:" + date.getTime());
            System.out.println("Current.time:" + simpleDateFormat.parse(time2).getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    static void test4(){
        char a = 'a';
        boolean bl = String.valueOf(a).matches("^[a-zA-Z]*");
        System.out.println("result:" + bl);
    }

    static void test5(){
        /*
        String email = "1uuaisdf你好@23123cc.xx";
        boolean result = email.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
        System.out.println("判断结果:" + result);
        */
        String string = "";
        System.out.println("长度:" + getTextCharLength(string));
    }

    public static int getTextCharLength(String text){

        int sindex = 0;
        int count = 0;
        while (sindex < text.length()) {
            char c = text.charAt(sindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        return count;
    }

    static void test6(){

        String email = "aaa@@uhyg";
        String[] strings = email.split("@");
        System.out.println("长度:" + strings.length);
    }
    private static int a;
    private static boolean bl;
    static void test7(){
//        int a;
        String string = "+0123";
        System.out.println("结果:" + string.matches("^\\+?\\d+$"));

        HashMap<String,String> hashMap;
        string.hashCode();
    }

    static void test8(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");

        String time2 = simpleDateFormat.format(new Date());
        System.out.println("Current.time:" + time2);
    }
}
