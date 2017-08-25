package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;

/**
 * Created by wilmerh on 8/25/17.
 */

public class ViewModel {

    Context context;

    public ViewModel(Context context) {
        this.context = context;
    }

    public String getStringFromResource(int resourceId) {
        return context.getString(resourceId);
    }
}
