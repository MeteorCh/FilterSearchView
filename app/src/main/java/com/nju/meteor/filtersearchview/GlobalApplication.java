package com.nju.meteor.filtersearchview;

import android.app.Application;
import android.content.Context;
import org.litepal.LitePal;


public class GlobalApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
