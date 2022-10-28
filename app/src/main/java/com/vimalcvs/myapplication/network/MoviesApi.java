package com.vimalcvs.myapplication.network;

import com.vimalcvs.myapplication.MovieModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesApi {

    @GET("movies.php")
    Call<List<MovieModel>> getMovies(@Query("index") int index);
}
