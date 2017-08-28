package com.wh2.foss.imageselector.api;

import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    String BASE_URL = "http://localhost:3000";

    @GET("/config")
    Single<Config> getConfigurations();

    @GET("/companies")
    Observable<List<Company>> getCompanies();
}
