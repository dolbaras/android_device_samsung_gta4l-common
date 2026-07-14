package com.gta4l.systemtweaks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Re-applies the stored render resolution on every boot (default 1080p when
 * unset). This is what makes the render choice persistent and factory-reset-safe
 * without a build.prop default. directBootAware so it can fire at
 * LOCKED_BOOT_COMPLETED (before unlock) to minimise the brief native-res window
 * during boot.
 *
 * On LOCKED_BOOT_COMPLETED we only apply the size (early, less flash). On
 * BOOT_COMPLETED we apply and — ONLY if a non-native size is in effect — restart
 * the launcher, because the launcher registers its bottom home/recents gesture for
 * the size it started at and won't recompute it otherwise. At native (the default)
 * the launcher already starts at the right size, so no restart/flash is needed.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int render = Tweaks.getRender(context);
        boolean bootDone = Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction());
        boolean restartUi = bootDone && render != 1200; // native needs no relaunch
        Tweaks.applyRender(context, render, false, restartUi);
    }
}
