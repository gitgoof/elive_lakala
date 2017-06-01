//
// Created by 高峰 on 2017/3/9.
//
#include "com_test_gf_jni_JniMethod.h"
using namespace art::mirror;

JNIEXPORT jstring JNICALL Java_com_test_gf_jni_JniMethod_jniAdd
  (JNIEnv *env, jobject jobject1, jint x, jint y)
  {
    // 调用java方法getJavaData
    // JNIEnv 代表java环境

    jclass jclass1 = env->GetObjectClass(jobject1);
    jmethodID jmethodID1 = env->GetMethodID(jclass1,"getJavaData","()Ljava/lang/String;");
//    env->CallCharMethod(jclass1,jmethodID1);
//    jstring jstring1 = (jstring) env->CallObjectMethod(jobject1, jmethodID1);
//    env->GetObjectField()  // 获取类的属性

    jmethodID jmethodID2 = env->GetStaticMethodID(jclass1,"getApplicationName","()Ljava/lang/String;");
//    env->CallCharMethod(jclass1,jmethodID1);
    jstring jstring1 = (jstring) env->CallObjectMethod(jobject1, jmethodID1);

    jstring jstring2 = (jstring)env->CallStaticObjectMethod(jclass1,jmethodID2);

   // return jstring2 + jstring1;
  return env->NewStringUTF("add jni native return");
  }

/*
 * Class:     com_test_gf_jni_JniMethod
 * Method:    getName
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_test_gf_jni_JniMethod_getName
  (JNIEnv *env, jobject obj)
  {

  }
JNIEXPORT void JNICALL Java_com_test_gf_tool_BugFixManager_replaceArt
  (JNIEnv *env, jobject jobject1,jobject jobject2,jobject jobject3)
  {
    jclass jclass1 = env->GetObjectClass(jobject1);
    jmethodID jmethodID1 = env->GetMethodID(jclass1,"tellMeCallMethod","()Ljava/lang/String;");
    env->CallObjectMethod(jobject1, jmethodID1);

    ArtMethod* smeth = (ArtMethod*)env->FromReflectedMethod(jobject2);
    ArtMethod* dmeth = (ArtMethod*)env->FromReflectedMethod(jobject3);
    // reinterpret 重新解释
    //reinterpret_cast<Class*>(dmeth->declaring_class_)->class_loader_=reinterpret_cast<Class*>(smeth->declaring_class_)->class_loader_;
    //reinterpret_cast<Class*>(dmeth->declaring_class_)->clinit_thread_id_=reinterpret_cast<Class*>(smeth->declaring_class_)->clinit_thread_id_;
    //reinterpret_cast<Class*>(dmeth->declaring_class_)->status_=reinterpret_cast<Class*>(smeth->declaring_class_)->status_;

    //reinterpret_cast<Class*>(dmeth->declaring_class_)->super_class_=0;

//    dmeth->declaring_class_->class_loader_ = smeth->declaring_class_->class_loader_;
//      dmeth->declaring_class_->clinit_thread_id_ = smeth->declaring_class_->clinit_thread_id_;
//      dmeth->declaring_class_->status_ = smeth->declaring_class_->status_;
//      dmeth->declaring_class_->super_class_ = 0;

    smeth->declaring_class_=dmeth->declaring_class_;
    smeth->dex_cache_resolved_types_=dmeth->dex_cache_resolved_types_;
    smeth->access_flags_=dmeth->access_flags_;
    smeth->dex_cache_resolved_methods_=dmeth->dex_cache_resolved_methods_;

    smeth->dex_code_item_offset_=dmeth->dex_code_item_offset_;
    smeth->method_index_=dmeth->method_index_;
    smeth->dex_method_index_=dmeth->dex_method_index_;

    smeth->ptr_sized_fields_.entry_point_from_interpreter_=dmeth->ptr_sized_fields_.entry_point_from_interpreter_;
    smeth->ptr_sized_fields_.entry_point_from_jni_=dmeth->ptr_sized_fields_.entry_point_from_jni_;
    smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_=dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_;
  }

