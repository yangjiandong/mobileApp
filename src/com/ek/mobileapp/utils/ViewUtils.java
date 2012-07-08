package com.ek.mobileapp.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

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

    public static Bitmap textAsBitmap(Bitmap image, String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        //paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        int width = (int) (paint.measureText(text) + 0.5f); // round
        float baseline = (int) (paint.ascent() + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap newMapBitmap = image.copy(Bitmap.Config.ARGB_8888, true);
        //Bitmap newMapBitmap=null;
        try {

            //newMapBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888); // this creates a MUTABLE bitmap
            //Canvas canvas = new Canvas(bmp);

            //Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(newMapBitmap);
            //canvas.drawColor(Color.GREEN);
            canvas.drawText(text, 10, 20, paint);
        } catch (Exception e) {
            Log.e("textAsBitmap", e.getMessage());
        }

        String filename = "version.jpg";
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            newMapBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newMapBitmap;
    }
}
