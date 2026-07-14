package com.gta4l.systemtweaks;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * System Tweaks — on-the-fly control of two SM-T50x graphics settings:
 *   1. UI render resolution (logical display size) — 900p / 1080p / 1200p(native)
 *   2. Screen-recording max resolution cap        — 900p / 1080p / 1200p(native)
 *
 * Render changes drive IWindowManager (see {@link Tweaks}) like the `wm size`/
 * `wm density` shell commands and are re-applied on boot by {@link BootReceiver}
 * (default 1080p), so they persist across reboot and factory reset. Recording
 * writes SETTING_SCREENREC (long-side px) which the patched SystemUI
 * ScreenMediaRecorder reads as its cap.
 *
 * The activity declares configChanges for density/screenSize so a render change
 * does NOT recreate it — recreation used to re-run the initial check() +
 * state-restore and bounce the selection back to the default. setSaveEnabled(false)
 * on the groups is a second guard against state restoration.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad = dp(20);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        root.addView(header(getString(R.string.app_name)));
        root.addView(caption(getString(R.string.intro)));

        // ---- Render resolution ----
        root.addView(sectionTitle(getString(R.string.render_title)));
        final RadioGroup renderGroup = new RadioGroup(this);
        renderGroup.setSaveEnabled(false);
        addRadio(renderGroup, 0, getString(R.string.render_900));
        addRadio(renderGroup, 1, getString(R.string.render_1080));
        addRadio(renderGroup, 2, getString(R.string.render_1200));
        renderGroup.check(renderToId(Tweaks.getRender(this)));
        renderGroup.setOnCheckedChangeListener((g, id) -> {
            int r = idToRender(id);
            // restartUi=true: relaunch launcher+SystemUI so the bottom home gesture
            // re-registers for the new size.
            if (Tweaks.applyRender(this, r, true, true)) toast(getString(R.string.toast_render, r));
            else toast(getString(R.string.err_render));
        });
        root.addView(renderGroup);

        // ---- Screen recording resolution ----
        root.addView(sectionTitle(getString(R.string.rec_title)));
        final RadioGroup recGroup = new RadioGroup(this);
        recGroup.setSaveEnabled(false);
        addRadio(recGroup, 0, getString(R.string.rec_900));
        addRadio(recGroup, 1, getString(R.string.rec_1080));
        addRadio(recGroup, 2, getString(R.string.rec_1200));
        recGroup.check(recToId(Tweaks.getScreenrec(this)));
        recGroup.setOnCheckedChangeListener((g, id) -> applyRec(idToRec(id)));
        root.addView(recGroup);

        root.addView(caption(getString(R.string.rec_note)));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(scroll);
    }

    private void applyRec(int longSide) {
        try {
            Settings.Global.putInt(getContentResolver(), Tweaks.SETTING_SCREENREC, longSide);
        } catch (SecurityException e) {
            toast(getString(R.string.err_perm));
            return;
        }
        int p = (longSide == 1500) ? 900 : (longSide == 2000) ? 1200 : 1080;
        toast(getString(R.string.toast_rec, p));
    }

    // ---- id <-> value mapping ----
    private static int renderToId(int r) { return r == 900 ? 0 : r == 1200 ? 2 : 1; }
    private static int idToRender(int id) { return id == 0 ? 900 : id == 2 ? 1200 : 1080; }
    private static int recToId(int ls) { return ls == 1500 ? 0 : ls == 2000 ? 2 : 1; }
    private static int idToRec(int id) { return id == 0 ? 1500 : id == 2 ? 2000 : 1800; }

    // ---- tiny programmatic-UI helpers ----
    private void addRadio(RadioGroup g, int id, String text) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        rb.setText(text);
        rb.setPadding(0, dp(8), 0, dp(8));
        g.addView(rb);
    }

    private TextView header(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(24);
        t.setPadding(0, 0, 0, dp(4));
        return t;
    }

    private TextView sectionTitle(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(18);
        t.setPadding(0, dp(20), 0, dp(4));
        return t;
    }

    private TextView caption(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(13);
        t.setPadding(0, dp(4), 0, dp(4));
        t.setGravity(Gravity.START);
        return t;
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
