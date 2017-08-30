package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.view.View;

import com.wh2.foss.imageselector.BR;
import com.wh2.foss.imageselector.api.ApiClient;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActivityViewModel extends ViewModel implements Observable {

    private int progress;
    private boolean progressShowing;

    public ActivityViewModel(Context context) {
        super(context);
    }

    public Single<Config> getConfigurations() {
        return ApiClient
                .getAPI()
                .getConfigurations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Company>> getCompanies() {
        return ApiClient
                .getAPI()
                .getCompanies()
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Bindable
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyChange(BR.progress);
    }

    @Bindable
    public int getProgressShowing() {
        return progressShowing ? View.VISIBLE : View.GONE;
    }

    public void setProgressShowing(boolean progressShowing) {
        this.progressShowing = progressShowing;
    }
}
