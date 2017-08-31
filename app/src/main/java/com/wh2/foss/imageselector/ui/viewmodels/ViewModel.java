package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;

class ViewModel extends BaseObservable {

    private Context context;

    ViewModel(Context context) {
        this.context = context;
    }

    String getString(int resourceId) {
        return context.getString(resourceId);
    }

    String getString(int resourceFormatID, String... placeholders) {
        return context.getString(resourceFormatID, placeholders);
    }
}
