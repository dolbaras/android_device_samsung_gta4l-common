# Dolby Atmos (general DAX3 set with control app) for gta4l.
# Effect + Dolby DMS HAL + control UI (com.dolby.daxappui / com.dolby.daxservice).
# The control app binds to vendor.dolby.hardware.dms@1.0::IDms, so the HAL, its
# init.rc, the VINTF entry (manifest.xml) and sepolicy (sepolicy/vendor/hal_dms*)
# are all required. Effect is registered in audio/configs/audio_effects.xml (dap,
# uuid 9d4921da-8225-4f29-aefa-39537a04bcaa). Samsung's stock libswdap/dax-default
# are removed from gta4l-common-vendor.mk to avoid a duplicate-copy conflict.

DOLBY_PATH := device/samsung/gta4l-common/dolby

# Vendor: effect libs, VQE, Dolby support libs, DMS HAL service + libs, configs
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/vendor/lib/soundfx/libswdap.so:$(TARGET_COPY_OUT_VENDOR)/lib/soundfx/libswdap.so \
    $(DOLBY_PATH)/vendor/lib/soundfx/libswvqe.so:$(TARGET_COPY_OUT_VENDOR)/lib/soundfx/libswvqe.so \
    $(DOLBY_PATH)/vendor/lib64/soundfx/libswdap.so:$(TARGET_COPY_OUT_VENDOR)/lib64/soundfx/libswdap.so \
    $(DOLBY_PATH)/vendor/lib64/soundfx/libswvqe.so:$(TARGET_COPY_OUT_VENDOR)/lib64/soundfx/libswvqe.so \
    $(DOLBY_PATH)/vendor/lib/libdapparamstorage.so:$(TARGET_COPY_OUT_VENDOR)/lib/libdapparamstorage.so \
    $(DOLBY_PATH)/vendor/lib/libstagefrightdolby.so:$(TARGET_COPY_OUT_VENDOR)/lib/libstagefrightdolby.so \
    $(DOLBY_PATH)/vendor/lib/libstagefright_foundation.so:$(TARGET_COPY_OUT_VENDOR)/lib/libstagefright_foundation.so \
    $(DOLBY_PATH)/vendor/lib64/libdapparamstorage.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libdapparamstorage.so \
    $(DOLBY_PATH)/vendor/lib64/libdlbdsservice.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libdlbdsservice.so \
    $(DOLBY_PATH)/vendor/lib64/libsqlite.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libsqlite.so \
    $(DOLBY_PATH)/vendor/lib64/libstagefrightdolby.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libstagefrightdolby.so \
    $(DOLBY_PATH)/vendor/lib64/libstagefright_foundation.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libstagefright_foundation.so \
    $(DOLBY_PATH)/vendor/lib/vendor.dolby.hardware.dms@1.0.so:$(TARGET_COPY_OUT_VENDOR)/lib/vendor.dolby.hardware.dms@1.0.so \
    $(DOLBY_PATH)/vendor/lib64/vendor.dolby.hardware.dms@1.0.so:$(TARGET_COPY_OUT_VENDOR)/lib64/vendor.dolby.hardware.dms@1.0.so \
    $(DOLBY_PATH)/vendor/lib64/vendor.dolby.hardware.dms@1.0-impl.so:$(TARGET_COPY_OUT_VENDOR)/lib64/vendor.dolby.hardware.dms@1.0-impl.so \
    $(DOLBY_PATH)/vendor/bin/hw/vendor.dolby.hardware.dms@1.0-service:$(TARGET_COPY_OUT_VENDOR)/bin/hw/vendor.dolby.hardware.dms@1.0-service \
    $(DOLBY_PATH)/vendor/etc/dolby/dax-default.xml:$(TARGET_COPY_OUT_VENDOR)/etc/dolby/dax-default.xml \
    $(DOLBY_PATH)/init.dolby.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/init.dolby.rc

# System: control-app privileged-permissions whitelist + power-save sysconfig
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/etc/permissions/privapp-com.dolby.daxservice.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/privapp-com.dolby.daxservice.xml \
    $(DOLBY_PATH)/etc/sysconfig/config-com.dolby.daxappui.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/sysconfig/config-com.dolby.daxappui.xml \
    $(DOLBY_PATH)/etc/sysconfig/config-com.dolby.daxservice.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/sysconfig/config-com.dolby.daxservice.xml

# Control app (prebuilt privileged APKs, see dolby/Android.mk)
PRODUCT_PACKAGES += \
    DaxUI \
    daxService
