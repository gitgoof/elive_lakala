LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := JniTest
LOCAL_SRC_FILES := com_test_gf_jni_JniMethod.cpp

include $(BUILD_SHARED_LIBRARY)

