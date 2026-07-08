LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES := \
    bootable/recovery \
    bootable/recovery/edify/include \
    bootable/recovery/otautil/include \
    bootable/recovery/updater/include
# Бэкпорт 18.1: в Android 11 libbase лежит в system/core/base (в A12+ вынесен в system/libbase).
# Вместо хардкода пути используем Soong header-lib libbase_headers (путь-независимо).
LOCAL_HEADER_LIBRARIES := libbase_headers
LOCAL_SRC_FILES := recovery_updater.cpp
LOCAL_MODULE := librecovery_updater_samsung
include $(BUILD_STATIC_LIBRARY)
