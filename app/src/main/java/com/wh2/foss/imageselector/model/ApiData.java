
package com.wh2.foss.imageselector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiData {

    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("pictures")
    @Expose
    private List<Image> pictures = null;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<Image> getPictures() {
        return pictures;
    }

    public void setPictures(List<Image> pictures) {
        this.pictures = pictures;
    }

}
