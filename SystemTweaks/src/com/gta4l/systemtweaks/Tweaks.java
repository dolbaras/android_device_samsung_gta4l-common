package com.gta4l.systemtweaks;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.Display;
import android.view.IWindowManager;
import java.util.List;

/**
 * Shared apply logic for the two graphics tweaks, used by both the UI
 * (MainActivity) and the boot receiver.
 *
 * The render resolution is the app's own responsibility (no build.prop
 * ro.config.size_override): BootReceiver re-applies the stored value on every
 * boot, defaulting to 1080p when unset. This makes all three options — including
 * native 1200p — persist across reboot and survive factory reset (the app lives
 * in /product). A build.prop default cannot express "native", because WMS treats
 * a forced size equal to the physical size as "cleared" and then falls back to
 * the prop, so native would always snap back to the prop value.
 *
 * IMPORTANT — launcher restart: the Quickstep launcher registers its bottom
 * home/recents swipe-up input monitor for the display size that was current when
 * it started, and does NOT recompute it when the size is changed via
 * setForcedDisplaySize. So after every render change the launcher (and SystemUI)
 * must be force-restarted, otherwise the bottom gesture stops working at the new
 * size (the back gesture is fine — SystemUI re-reads the size). Verified on device.
 */
public final class Tweaks {

    // Settings.Global keys. SETTING_SCREENREC is also read by SystemUI ScreenMediaRecorder.
    public static final String SETTING_RENDER = "systemtweaks_render";        // 900 / 1080 / 1200
    public static final String SETTING_SCREENREC = "systemtweaks_screenrec";  // long side: 1500 / 1800 / 2000

    // Defaults after a clean install = native (the panel's own resolution). The user
    // opts in to 1080p/900p via the UI; nothing is downscaled out of the box.
    public static final int DEF_RENDER = 1200;      // native 1200x2000
    public static final int DEF_SCREENREC = 2000;   // native long side (no cap)

    static final String TAG = "SystemTweaks";

    private Tweaks() {}

    static IWindowManager wm() {
        return IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
    }

    public static int getRender(Context c) {
        return Settings.Global.getInt(c.getContentResolver(), SETTING_RENDER, DEF_RENDER);
    }

    public static int getScreenrec(Context c) {
        return Settings.Global.getInt(c.getContentResolver(), SETTING_SCREENREC, DEF_SCREENREC);
    }

    /**
     * Apply the render resolution live via IWindowManager. Persists the choice when
     * asked, and restarts the launcher + SystemUI when {@code restartUi} so the
     * bottom home/recents gesture is re-registered for the new size.
     */
    public static boolean applyRender(Context c, int render, boolean persist, boolean restartUi) {
        if (persist) {
            try {
                Settings.Global.putInt(c.getContentResolver(), SETTING_RENDER, render);
            } catch (SecurityException e) {
                return false;
            }
        }
        IWindowManager wm = wm();
        if (wm == null) { android.util.Log.e(TAG, "applyRender: wm null"); return false; }
        // Pass the concrete calling user id (not USER_CURRENT/-2): the *ForUser
        // density calls resolve -2 via handleIncomingUser which demands
        // INTERACT_ACROSS_USERS_FULL, so -2 throws SecurityException here.
        final int user = UserHandle.myUserId();
        try {
            final int d = Display.DEFAULT_DISPLAY;
            if (render == 1200) {
                // Native: no prop to fall back to, so clearing gives true 1200x2000/240.
                wm.clearForcedDisplaySize(d);
                wm.clearForcedDisplayDensityForUser(d, user);
            } else if (render == 900) {
                wm.setForcedDisplaySize(d, 900, 1500);
                wm.setForcedDisplayDensityForUser(d, 180, user);
            } else { // 1080 (default)
                wm.setForcedDisplaySize(d, 1080, 1800);
                wm.setForcedDisplayDensityForUser(d, 216, user);
            }
            android.util.Log.i(TAG, "applyRender OK render=" + render + " user=" + user
                    + " restartUi=" + restartUi);
        } catch (Exception e) {
            android.util.Log.e(TAG, "applyRender render=" + render + " FAILED", e);
            return false;
        }
        if (restartUi) restartUi(c);
        return true;
    }

    /**
     * Force-restart the launcher so it re-registers its bottom home/recents
     * swipe-up input monitor for the current display size. Restarting the launcher
     * alone is sufficient (verified on device) — SystemUI's back gesture already
     * re-reads the size, and SystemUI is force-stop-protected anyway. Needs
     * FORCE_STOP_PACKAGES (granted to this platform-signed priv-app); the launcher
     * is relaunched automatically by the system.
     */
    static void restartUi(Context c) {
        try {
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return;
            String launcher = getLauncherPackage(c);
            if (launcher != null) {
                am.forceStopPackage(launcher);
                android.util.Log.i(TAG, "restartUi: force-stopped launcher " + launcher);
            } else {
                android.util.Log.e(TAG, "restartUi: launcher not found");
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "restartUi failed", e);
        }
    }

    private static String getLauncherPackage(Context c) {
        // NB: resolveActivity(HOME) returns com.android.settings' FallbackHome (the
        // boot placeholder), and getHomeActivities() returns null on A11 (home is
        // RoleManager-managed). So query ALL home candidates and pick the first
        // non-settings one — that's the real launcher.
        PackageManager pm = c.getPackageManager();
        Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> homes = pm.queryIntentActivities(home, 0);
        for (ResolveInfo ri : homes) {
            if (ri.activityInfo != null
                    && !"com.android.settings".equals(ri.activityInfo.packageName)) {
                return ri.activityInfo.packageName;
            }
        }
        android.util.Log.e(TAG, "getLauncherPackage: none among " + homes.size() + " homes");
        return null;
    }
}
