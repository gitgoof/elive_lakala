/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_test_gf_jni_JniMethod */
/* #include "common.h" */
#include "art_method.h"

#ifndef _Included_com_test_gf_jni_JniMethod
#define _Included_com_test_gf_jni_JniMethod
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_test_gf_jni_JniMethod
 * Method:    jniAdd
 * Signature: (II)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_test_gf_jni_JniMethod_jniAdd
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_test_gf_jni_JniMethod
 * Method:    getName
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_test_gf_jni_JniMethod_getName
  (JNIEnv *, jobject);
/**
*com.test.gf.tool
*/
JNIEXPORT void JNICALL Java_com_test_gf_tool_BugFixManager_replaceArt
    (JNIEnv *, jobject,jobject,jobject);

#ifdef __cplusplus
}
#endif
#endif