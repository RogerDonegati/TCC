package com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices;
import com.tcc.zeugmaproject.zeugmaproject.Model.Movie;
import com.tcc.zeugmaproject.zeugmaproject.Model.ResultMovie;
import com.tcc.zeugmaproject.zeugmaproject.Model.ResultSerie;
import com.tcc.zeugmaproject.zeugmaproject.Model.Season;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMDBServices {
    //base url: https://api.themoviedb.org/3/
    //ID
    @GET("movie/{query}?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR&" +
            "append_to_response=videos,images&include_image_language=en,null&include_video_language=en,null")
    Call<Movie> getMovieById(@Path("query") String query);

    //Listas: Upcoming, Popular, Top_Rated, now_playing
    @GET("movie/{path}?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR")
    Call<ResultMovie> getMovieLists(@Path("path") String path, @Query("page") int page);

    //ID
    @GET("tv/{query}?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR&" +
            "append_to_response=videos,images&include_image_language=en,null&include_video_language=en,null")
    Call<Serie> getSerieById(@Path("query") String query);

    //Listas: Popular, Top_Rated
    @GET("tv/{path}?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR")
    Call<ResultSerie> getSerieList(@Path("path") String path, @Query("page") int page);

    //SEASON
    @GET("tv/{path1}/season/{path2}?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR" +
            "append_to_response=videos,images&include_image_language=en,null&include_video_language=en,null")
    Call<Season> getEpisodes(@Path("path1") String path, @Path("path2") String season);

    //Search by Movie Name
    @GET("search/movie?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR&include_adult=true")
    Call<ResultMovie> getMovieSearchByName(@Query("query") String query);

    //Search by Serie Name
    @GET("search/tv?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR")
    Call<ResultSerie> getSerieSearchByName(@Query("query") String query);

    //Recomenadações (nao testado ainda}
    @GET("movie/{id}/recommendations?api_key=0d8d301e2ced1c56b2aa06a1c355d81c&language=pt-BR")
    Call<ResultMovie> getMovieRecommendation(@Path("id") String id);

}
