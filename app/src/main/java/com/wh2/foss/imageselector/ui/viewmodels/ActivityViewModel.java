package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;

import com.wh2.foss.imageselector.api.ApiClient;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.model.Image;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActivityViewModel extends ViewModel {

    public final ObservableBoolean progressShowing;
    public final ObservableInt progress;

    public ActivityViewModel(Context context) {
        super(context);
        progressShowing = new ObservableBoolean();
        progress = new ObservableInt(0);
    }

    public Single<Config> getConfigurations() {
        return ApiClient
                .getAPI()
                .getConfigurations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Image> getPictures() {
        return ApiClient
                .getAPI()
                .getPictures()
                .flatMap(Observable::fromIterable)
                .filter(picture -> !picture.isIgnored())
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setProgress(int progress) {
        this.progress.set(progress);
    }

    public int getProgressShowing() {
        return progressShowing.get() ? View.VISIBLE : View.GONE;
    }
}
