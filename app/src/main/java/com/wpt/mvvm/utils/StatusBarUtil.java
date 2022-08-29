package com.wpt.mvvm.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Desc: Status bar tool class
 */
public class StatusBarUtil {

    private static final int DEFAULT_ALPHA = 0;

    /**
     * Set the status bar color (custom color)
     *
     * @param activity target activity
     * @param color    Status bar color value
     */
    public static void setColor(@NonNull Activity activity, @ColorInt int color) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        setColor(activityWeakReference.get(), color, DEFAULT_ALPHA);
    }

    /**
     * Set solid color status bar (custom color, alpha)
     *
     * @param activity target activity
     * @param color    Status bar color value
     * @param alpha    Status Bar Transparency
     */
    public static void setColor(@NonNull Activity activity, @ColorInt int color, @IntRange (from = 0, to = 255) int alpha) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        Window window = activityWeakReference.get().getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(cipherColor(color, alpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
            setRootView(activityWeakReference.get(), true);
        }
    }

    /**
     * Set the status bar gradient color
     *
     * @param activity target activity
     * @param view     target View
     */
    public static void setGradientColor(@NonNull Activity activity, View view) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        ViewGroup decorView = (ViewGroup) activityWeakReference.get().getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(android.R.id.custom);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
        }
        setRootView(activityWeakReference.get(), false);
        setTransparentForWindow(activityWeakReference.get());
        setPaddingTop(activityWeakReference.get(), view);
    }

    /**
     * Set the status bar gradient color
     *
     * @param activity target activity
     * @param view     target View
     */
    public static void setGradientColor(@NonNull Activity activity, View view, boolean fitSystemWindows) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        ViewGroup decorView = (ViewGroup) activityWeakReference.get().getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(android.R.id.custom);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
        }
        setRootView(activityWeakReference.get(), fitSystemWindows);
        setTransparentForWindow(activityWeakReference.get());
        setPaddingTop(activityWeakReference.get(), view);
    }

    /**
     * Set transparent status bar
     *
     * @param activity target activity
     */
    public static void setTransparentForWindow(@NonNull Activity activity) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        Window window = activityWeakReference.get().getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Increase the paddingTop of the View, and the increased value is the height of the status bar (intelligent judgment, and set the height)
     *
     * @param context target Context
     * @param view    View that needs to be increased
     */
    public static void setPaddingTop(Context context, @NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0 && view.getPaddingTop() == 0) {
                lp.height += getStatusBarHeight(context);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                        view.getPaddingRight(), view.getPaddingBottom());
            }
        }
    }


    /**
     * Set the status bar darkMode, font color and icon to black
     * (currently support MIUI6 or above, Flyme4 or above, Android M or above)
     * Set the status bar with black text on a white background
     *
     * @param activity target activity
     */
    public static void setDarkMode(@NonNull Activity activity) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        darkMode(activityWeakReference.get().getWindow(), true);
    }

    /**
     * Set the status bar LightMode, the font color and icon become brighter (currently support MIUI6 or above, Flyme4 or above, Android M or above)
     * Set the status bar with black text on a white background
     *
     * @param activity target activity
     */
    public static void setLightMode(@NonNull Activity activity) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        darkMode(activityWeakReference.get().getWindow(), false);
    }

    @TargetApi (Build.VERSION_CODES.M)
    private static void darkMode(Window window, boolean dark) {
        if (isFlyme4()) {
            setModeForFlyme4(window, dark);
        } else if (isMIUI6()) {
            setModeForMIUI6(window, dark);
        }
        darkModeForM(window, dark);
    }


    /**
     * android 6.0set font color
     *
     * @param window target window
     * @param dark   light or dark
     */
    @RequiresApi (Build.VERSION_CODES.M)
    private static void darkModeForM(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            if (dark) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }


    /**
     * Set the darkMode of the status bar of MIUI6+, the font color and icon in darkMode
     * http://dev.xiaomi.com/doc/p=4769/
     *
     * @param window target window
     * @param dark   light or dark
     */
    private static void setModeForMIUI6(Window window, boolean dark) {
        Class<? extends Window> clazz = window.getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            Log.e("StatusBar", "darkIcon: failed");
        }

    }

    /**
     *
     * @param window target window
     * @param dark   light or dark
     */
    private static void setModeForFlyme4(Window window, boolean dark) {
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            Log.e("StatusBar", "darkIcon: failed");
        }
    }


    /**
     * Determine if Flyme4 or above
     */
    private static boolean isFlyme4() {
        return Build.FINGERPRINT.contains("Flyme_OS_4") || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * Determine whether MIUI6 or above
     */
    private static boolean isMIUI6() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate alpha color value
     *
     * @param color Status bar color value
     * @param alpha Status Bar Transparency
     */
    private static int cipherColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }


    /**
     * Create a transparent view
     *
     * @param viewGroup target viewGroup
     * @param color     Status bar color value
     * @param alpha     Status Bar Transparency
     */
    private static void setTranslucentView(ViewGroup viewGroup, @ColorInt int color, @IntRange (from = 0, to = 255) int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int cipherColor = cipherColor(color, alpha);
            View translucentView = viewGroup.findViewById(android.R.id.custom);
            if (translucentView == null && cipherColor != 0) {
                translucentView = new View(viewGroup.getContext());
                translucentView.setId(android.R.id.custom);
                ViewGroup.LayoutParams params =
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(viewGroup.getContext()));
                viewGroup.addView(translucentView, params);
            }
            if (translucentView != null) {
                translucentView.setBackgroundColor(cipherColor);
            }
        }

    }

    /**
     * Set root layout parameters
     *
     * @param activity         target activity
     * @param fitSystemWindows Whether to reserve the height of the toolbar
     */
    private static void setRootView(Activity activity, boolean fitSystemWindows) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        ViewGroup parent = activityWeakReference.get().findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fitSystemWindows);
                ((ViewGroup) childView).setClipToPadding(fitSystemWindows);
            }
        }
    }

    /**
     * Get the status bar height
     *
     * @param context target Context
     */
    public static int getStatusBarHeight(Context context) {
        // Get the status bar height
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
