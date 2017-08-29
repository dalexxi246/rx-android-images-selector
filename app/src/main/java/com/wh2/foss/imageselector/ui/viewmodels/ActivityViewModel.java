package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;

import com.wh2.foss.imageselector.api.ApiClient;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ActivityViewModel extends ViewModel {

    public ActivityViewModel(Context context) {
        super(context);
    }

    public Single<Config> getConfigurations() {
        return ApiClient
                .getAPI()
                .getConfigurations()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Company>> getCompanies() {
        return ApiClient
                .getAPI()
                .getCompanies()
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
