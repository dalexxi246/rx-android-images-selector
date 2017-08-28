
package com.wh2.foss.imageselector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("dimensions")
    @Expose
    private Dimensions dimensions;
    @SerializedName("urlBase")
    @Expose
    private String urlBase;
    @SerializedName("urlExample")
    @Expose
    private String urlExample;

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public String getUrlExample() {
        return urlExample;
    }

    public void setUrlExample(String urlExample) {
        this.urlExample = urlExample;
    }

}
