package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;

public class ViewModel extends BaseObservable {

    private Context context;

    ViewModel(Context context) {
        this.context = context;
    }

    public String getString(int resourceId) {
        return context.getString(resourceId);
    }
}
