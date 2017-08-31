package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wh2.foss.imageselector.api.ApiClient;
import com.wh2.foss.imageselector.api.ImagesDownloader;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.model.Image;
import com.wh2.foss.imageselector.model.downloads.DownloadPaths;
import com.wh2.foss.imageselector.model.downloads.Progress;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageViewModel extends ViewModel {

    private Image image;
    private Config config;

    public ImageViewModel(Context context, Image image, Config config) {
        super(context);
        this.image = image;
        this.config = config;
    }

    @BindingAdapter("app:imageUrl")
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    private String getImageUrl(String dimensions) {
        return String.format("%s/%s/%s", config.getUrlBase(), dimensions, image.getUrl());
    }

    @Bindable
    public String getMediumImageUrl() {
        return getImageUrl(config.getDimensions().getMedium());
    }

    private String getSmallImageUrl() {
        return getImageUrl(config.getDimensions().getSmall());
    }

    public Observable<Progress> performDownload(String dirName, String fileName) {
        DownloadPaths downloadPaths = new DownloadPaths(getMediumImageUrl(), dirName, fileName);
        return new ImagesDownloader(downloadPaths).getProgressStream();
    }

    public Completable ignorePicture() {
        image.setIgnored(true);
        return ApiClient.getAPI()
                .ignorePicture(image.getId(), image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
