package com.wh2.foss.imageselector.api;

import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.model.Config;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ApiService {

    String BASE_URL = "http://127.0.0.1:3000";

    @GET("/config")
    Single<Config> getConfigurations();

    @GET("/companies")
    Observable<List<Company>> getCompanies();

    @PATCH("/companies/{id}")
    Completable ignoreCompany(@Path("id") int id, @Body Company company);
}
