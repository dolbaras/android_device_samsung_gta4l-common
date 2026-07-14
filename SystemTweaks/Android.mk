LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_PACKAGE_NAME := SystemTweaks
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PRODUCT_MODULE := true
# Needs @hide IWindowManager (setForcedDisplaySize/Density) like the `wm` shell cmd.
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_DEX_PREOPT := false
LOCAL_REQUIRED_MODULES := privapp-permissions-com.gta4l.systemtweaks.xml
include $(BUILD_PACKAGE)

# Privileged-permission allowlist for WRITE_SECURE_SETTINGS.
include $(CLEAR_VARS)
LOCAL_MODULE := privapp-permissions-com.gta4l.systemtweaks.xml
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_PATH := $(TARGET_OUT_PRODUCT)/etc/permissions
LOCAL_SRC_FILES := privapp-permissions-com.gta4l.systemtweaks.xml
include $(BUILD_PREBUILT)
