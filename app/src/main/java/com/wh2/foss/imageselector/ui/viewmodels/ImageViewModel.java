package com.wh2.foss.imageselector.ui.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wh2.foss.imageselector.BR;
import com.wh2.foss.imageselector.model.Company;

public class ImageViewModel extends ViewModel implements Observable {

    private Company company;

    public ImageViewModel(Context context, Company company) {
        super(context);
        this.company = company;
    }

    @BindingAdapter("app:imageUrl")
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
}
