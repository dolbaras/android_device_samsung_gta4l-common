#
# Copyright (C) 2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

# Apps the Pixel GApps set ships that only work on Pixel phones or with US carriers
# (Google Fi, Verizon, Sprint, US Cellular device management, CBRS, Pixel modem
# config, Pixel crash detection / Now Playing / diagnostics / retail demo). On this
# tablet they cannot work but still take space and, for some, run in the background.
# Overriding removes them from the product without editing the GApps package list.
include $(CLEAR_VARS)
LOCAL_MODULE := RemovePackages
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := /dev/null
LOCAL_UNINSTALLABLE_MODULE := true
LOCAL_OVERRIDES_MODULES := \
    AmbientSensePrebuilt \
    CarrierWifi \
    CbrsNetworkMonitor \
    ConnMO \
    DCMO \
    DMService \
    DiagnosticsToolPrebuilt \
    MyVerizonServices \
    NovaBugreportWrapper \
    RilConfigService \
    SafetyHubPrebuilt \
    Showcase \
    SprintDM \
    SprintHM \
    Tycho \
    USCCDM \
    VZWAPNLib \
    WfcActivation \
    grilservice
include $(BUILD_PREBUILT)
