package com.xinheng;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.xinheng.logic.user.model.UserInfo;

/**
 * App application
 * @author jack
 */
public class AppDroid extends Application {

    private static AppDroid instance;
    private UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        //全局Application
        instance = this;
        //内存泄漏
        LeakCanary.install(this);
    }

    public static AppDroid getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}