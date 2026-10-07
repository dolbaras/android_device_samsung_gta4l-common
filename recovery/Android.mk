LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES := \
    bootable/recovery \
    bootable/recovery/edify/include \
    bootable/recovery/otautil/include \
    bootable/recovery/updater/include
# libbase lives in system/core/base on Android 11 (system/libbase from Android 12 on), so use
# the libbase_headers header library instead of a path.
LOCAL_HEADER_LIBRARIES := libbase_headers
LOCAL_SRC_FILES := recovery_updater.cpp
LOCAL_MODULE := librecovery_updater_samsung
include $(BUILD_STATIC_LIBRARY)
