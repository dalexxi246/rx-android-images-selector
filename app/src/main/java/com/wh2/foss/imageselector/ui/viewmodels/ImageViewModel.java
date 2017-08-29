package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wh2.foss.imageselector.BR;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;

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

    public String getImageSmallUrl() {
        return String.format("%s/%s/%s", config.getUrlBase(), config.getDimensions().getSmall(), company.getUrl());
    }

    public String getImageMediumUrl() {
        return String.format("%s/%s/%s", config.getUrlBase(), config.getDimensions().getMedium(), company.getUrl());
    }

    // TODO: 8/29/17 Guardar imagenes en Bitmap (https://github.com/esafirm/RxDownloader)
}
