#include <jni.h>
#include <string>

#include "art_method.h"
using namespace art::mirror;

extern "C"{
JNIEXPORT jstring JNICALL
Java_com_test_gf_jni_MyNativeMethod_getAdd(JNIEnv *env, jobject instance, jint x,
                                           jint y) {

    std::string nat = "is my native methodd";

    return env->NewStringUTF(nat.c_str());
}

JNIEXPORT void JNICALL Java_com_test_gf_jni_MyNativeMethod_replaceArt
        (JNIEnv *env, jobject jobject1,jobject jobject2,jobject jobject3)
{
}

JNIEXPORT void JNICALL Java_com_test_gf_tool_BugFixManager_replaceArt
        (JNIEnv *env, jobject jobject1,jobject jobject2,jobject jobject3)
{
    ArtMethod* smeth = (ArtMethod*)env->FromReflectedMethod(jobject2);
    ArtMethod* dmeth = (ArtMethod*)env->FromReflectedMethod(jobject3);

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

}
