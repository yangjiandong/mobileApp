package com.ek.mobileapp;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

//单例
public class MainApplication extends Application {
    private List<Activity> activitys = null;
    private static MainApplication instance;

    private MainApplication() {
        activitys = new LinkedList<Activity>();
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return
     */
    public static MainApplication getInstance() {
        if (null == instance) {
            instance = new MainApplication();
        }
        return instance;

    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        } else {
            activitys.add(activity);
        }

    }

    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }
}
