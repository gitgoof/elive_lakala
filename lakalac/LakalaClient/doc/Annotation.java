package com.lakala.library;

/**
 * 注释说明类<br/>
 * 这个类是个示例，规范下项目中的注释风格；
 * 每个类实现的功能在类头进行说明。
 * 此类不参与编译
 * Created by xyz on 13-12-10.
 */
public class Annotation {

    /**
     * 类中的常量，都以大写命名,两个单词以下划线分割
     * 常量属性在类的开头声明
     */
    public static final String ANNOTATION_CONSTANT = "constantValu";

    /**
     * public 访问权限的属性，在常量之后声明
     * 字符类型一般初始化为空串
     */
    public String publicAttribute = "";

    /**
     * protected 访问权限的属性，在public属性之后声明
     * 字符类型一般初始化为空串
     */
    protected  String publicAttribute = "";

    //私有属性，用单行注释进行说明,私有属性在protected属性之后声明
    private int privateIntAttribute = 0;

    /**
     * 规范方法，有一个参数param，没有返回值
     * 这是一个类中public的方法的注释,方法与属性之间要有换行
     * @param param 此规范方法接收的参数，比如传入java doc规范，表明要遵守java doc的注释规范
     */
    public void standard(String param){

        //方法中对变量的注释，赋值符号前后需要加空格
        String innerValue = param;

        //方法中逻辑处理部分加上功能块的说明，这是一个死循环，别跳进去
        while (true){

        }

        //功能块和功能块之间要有换行，恭喜大侠没被死循环困住，死里逃生
        if(youDoNotWantToDie){
            congratulations;
            you will not die;
        }else {
            go to die;
        }
    }

    /**
     * 保护方法，有一个参数param，返回int数据
     * 这是一个类中protected的方法的注释，保护方法在public方法之后，方法与方法之间要有换行
     * @param param 此规范方法接收的参数，比如传入java doc规范，表明要遵守java doc的注释规范
     * @return 返回一个int型数据
     */
    protected int protectedMethod(String param){

    }

    /**
     * 私有方法，有一个参数param，返回int数据
     * 这是一个类中私有方法的注释，私有方法在protected方法之后
     * 自己的东西，还是靠后些吧，这才真正体现出为人民服务的真谛
     * @param param 此规范方法接收的参数，比如传入java doc规范，表明要遵守java doc的注释规范
     * @return 返回一个int型数据
     */
    private int protectedMethod(String param){

    }
}
