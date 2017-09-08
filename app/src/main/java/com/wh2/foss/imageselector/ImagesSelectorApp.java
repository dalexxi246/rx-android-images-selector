package com.wh2.foss.imageselector;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

public class ImagesSelectorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        SharedPreferencesManager.init(this, true);
    }
}
