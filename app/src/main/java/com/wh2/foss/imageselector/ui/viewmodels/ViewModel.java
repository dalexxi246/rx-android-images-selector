package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.PropertyChangeRegistry;

public class ViewModel extends BaseObservable {

    private Context context;

    private transient PropertyChangeRegistry propertyChangeRegistry = new PropertyChangeRegistry();

    ViewModel(Context context) {
        this.context = context;
    }

    public String getString(int resourceId) {
        return context.getString(resourceId);
    }

    void notifyChange(int propertyId) {
        if (propertyChangeRegistry == null) {
            propertyChangeRegistry = new PropertyChangeRegistry();
        }
        propertyChangeRegistry.notifyChange(this, propertyId);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (propertyChangeRegistry == null) {
            propertyChangeRegistry = new PropertyChangeRegistry();
        }
        propertyChangeRegistry.add(callback);

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (propertyChangeRegistry != null) {
            propertyChangeRegistry.remove(callback);
        }
    }
}
