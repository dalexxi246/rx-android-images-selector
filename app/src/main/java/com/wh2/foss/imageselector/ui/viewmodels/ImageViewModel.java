package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wh2.foss.imageselector.BR;
import com.wh2.foss.imageselector.api.ImagesDownloader;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.model.downloads.DownloadPaths;
import com.wh2.foss.imageselector.model.downloads.Progress;

import io.reactivex.Observable;

public class ImageViewModel extends ViewModel {

    private Company company;
    private Config config;

    public ImageViewModel(Context context, Company company, Config config) {
        super(context);
        this.company = company;
        this.config = config;
    }

    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    @Bindable
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
        notifyChange(BR.company);
    }

    private String getImageUrl(String dimensions) {
        return String.format("%s/%s/%s", config.getUrlBase(), dimensions, company.getUrl());
    }

    public String getMediumImageUrl() {
        return getImageUrl(config.getDimensions().getMedium());
    }

    private String getSmallImageUrl() {
        return getImageUrl(config.getDimensions().getSmall());
    }

    public Observable<Progress> performDownload(String dirName, String fileName) {
        DownloadPaths downloadPaths = new DownloadPaths(getSmallImageUrl(), dirName, fileName);
        return new ImagesDownloader(downloadPaths).getProgressStream();
    }
}
