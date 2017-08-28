
package com.wh2.foss.imageselector.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiData {

    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("companies")
    @Expose
    private List<Company> companies = null;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

}
