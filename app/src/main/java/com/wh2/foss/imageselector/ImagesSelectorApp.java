package com.wh2.foss.imageselector;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class ImagesSelectorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
