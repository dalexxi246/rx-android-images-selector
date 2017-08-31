package com.wh2.foss.imageselector.api;

import com.wh2.foss.imageselector.model.Image;
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

    String BASE_URL = "http://192.168.0.109:3000";

    @GET("/config")
    Single<Config> getConfigurations();

    @GET("/pictures")
    Observable<List<Image>> getPictures();

    @PATCH("/pictures/{id}")
    Completable ignorePicture(@Path("id") int id, @Body Image image);
}
