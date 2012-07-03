package com.ek.mobileapp.utils;

import android.content.Context;
import android.graphics.Color;

public class ViewUtils {
    public static final int MENU_HOME = 1;
    public static final int MENU_PATIENTS = 2;
    public static final int HOSPITALINFOHOST = 3;

    public static final int[] MENUID = { MENU_HOME, MENU_PATIENTS };

    //public static final int[] MENUIMAGE = { R.drawable.home,
    //        R.drawable.patientsinfo };

    public static final String[] MENUTEXT = { "全院概况", "病人信息" };

    public static final float SCROLL_SPIT = 20.0f;

    public static final int color_spit = Color.BLACK;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int diptopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxtodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
