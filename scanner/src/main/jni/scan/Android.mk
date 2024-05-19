LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -lm -llog 

LOCAL_SRC_FILES := \
    scan_core.cpp \
    libscan_jni.cpp

LOCAL_MODULE := libscanjni
LOCAL_PROPRIETARY_MODULE := true

include $(BUILD_SHARED_LIBRARY)
