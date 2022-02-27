package com.ishuinzu.parentside.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.Calendar;
import java.util.Locale;

public class Utils {
    private static final long A_DAY = 86400 * 1000;

    private Utils() {
    }

    public static String getAndroidVersion(int sdkVersion) {
        String androidVersion = "";
        switch (sdkVersion) {
            case 7:
                androidVersion = "Android 2.1 (Eclair)";
                break;

            case 8:
                androidVersion = "Android 2.2 (Froyo)";
                break;

            case 9:
                androidVersion = "Android 2.3 (Gingerbread)";
                break;

            case 10:
                androidVersion = "Android 2.3.3 (Gingerbread)";
                break;

            case 11:
                androidVersion = "Android 3.0 (Honeycomb)";
                break;

            case 12:
                androidVersion = "Android 3.1 (Honeycomb)";
                break;

            case 13:
                androidVersion = "Android 3.2 (Honeycomb)";
                break;

            case 14:
                androidVersion = "Android 4.0 (IceCreamSandwich)";
                break;

            case 15:
                androidVersion = "Android 4.0.3 (IceCreamSandwich)";
                break;

            case 16:
                androidVersion = "Android 4.1 (Jelly Bean)";
                break;

            case 17:
                androidVersion = "Android 4.2 (Jelly Bean)";
                break;

            case 18:
                androidVersion = "Android 4.3 (Jelly Bean)";
                break;

            case 19:
                androidVersion = "Android 4.4 (Kitkat)";
                break;

            case 20:
                androidVersion = "Android 4.4W (Kitkat Wear)";
                break;

            case 21:
                androidVersion = "Android 5.0 (Lollipop)";
                break;

            case 22:
                androidVersion = "Android 5.1 (Lollipop)";
                break;

            case 23:
                androidVersion = "Android 6.0 (Marshmallow)";
                break;

            case 24:
                androidVersion = "Android 7.0 (Nougat)";
                break;

            case 25:
                androidVersion = "Android 7.1.1 (Nougat)";
                break;

            case 26:
                androidVersion = "Android 8.0 (Oreo)";
                break;

            case 27:
                androidVersion = "Android 8.1 (Oreo)";
                break;

            case 28:
                androidVersion = "Android 9.0 (Pie)";
                break;

            case 29:
                androidVersion = "Android 10.0 (Q)";
                break;

            case 30:
                androidVersion = "Android 11.0 (R)";
                break;

            case 31:
                androidVersion = "Android 12.0 (S)";
                break;
        }
        return androidVersion;
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        return Utils.getBitmap(drawable);
    }

    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported Drawable Type");
        }
    }

    public static int formatMilliMinutes(long milliSeconds) {
        return (int) (milliSeconds / 1000L);
    }

    public static String formatMilliSeconds(long milliSeconds) {
        long second = milliSeconds / 1000L;
        if (second < 60) {
            return String.format("%ss", second);
        } else if (second < 60 * 60) {
            return String.format("%sm %ss", second / 60, second % 60);
        } else {
            return String.format("%sh %sm %ss", second / 3600, second % (3600) / 60, second % (3600) % 60);
        }
    }

    public static boolean isSystemApp(PackageManager manager, String packageName) {

        boolean isSystemApp = false;
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                        || (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isSystemApp;
    }

    public static boolean isInstalled(PackageManager packageManager, String packageName) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo != null;
    }

    public static boolean openable(PackageManager packageManager, String packageName) {
        return packageManager.getLaunchIntentForPackage(packageName) != null;
    }

    public static int getAppUid(PackageManager packageManager, String packageName) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long[] getTimeRange(SortEnum sort) {
        long[] range;
        switch (sort) {
            case TODAY:
                return getTodayRange();
            case YESTERDAY:
                return getYesterday();
        }
        return getTodayRange();
    }


    private static long[] getTodayRange() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new long[]{cal.getTimeInMillis(), timeNow};
    }

    public static long getYesterdayTimestamp() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeNow - A_DAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private static long[] getYesterday() {
        long timeNow = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeNow - A_DAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        long end = start + A_DAY > timeNow ? timeNow : start + A_DAY;
        return new long[]{start, end};
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getDateID() {
        return getValueInDoubleFigure(Calendar.getInstance().get(Calendar.MONTH) + 1) + getValueInDoubleFigure(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String getValueInDoubleFigure(int value) {
        if (value <= 9) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }
}