package com.northcoders.tatooine.service;

import com.northcoders.tatooine.model.Artist;
import com.northcoders.tatooine.model.Tattoo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArtistAPIService {
    @GET("artist")
    Call<Artist> login(
            @Query("email") String email,
            @Query("password") String password);
}