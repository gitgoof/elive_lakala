package com.javatest.gf.classtest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaofeng on 2017/4/6.
 */

public class OutBeanMain {
    public static void main(String[] args) {
        ValueBean valueBean = new ValueBean();

        System.out.println("init--" + valueBean);

        test1(valueBean);
    }

    private static void test1(ValueBean valueBean){
        System.out.println("method:" + valueBean.toString());
    }
    private static void test2(){
        // 16字节，可以放入一个中文
        char ch = '中';

        Lock lock = new ReentrantLock();
        lock.lock();

        lock.unlock();



    }
}
