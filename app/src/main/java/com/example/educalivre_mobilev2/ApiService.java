package com.example.educalivre_mobilev2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("v1/exams/2020/questions")
    Call<RespostaAPI> listarQuestoes();
}

