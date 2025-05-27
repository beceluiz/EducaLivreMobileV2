package com.example.educalivre_mobilev2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v1/exams/{year}/questions")
    Call<RespostaAPI> listarQuestoes(@Path("year")int year);

    @GET("v1/exams/{year}/questions")
    Call<RespostaAPI> listarQuestoesPaginadas(
            @Path("year") int year,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}

