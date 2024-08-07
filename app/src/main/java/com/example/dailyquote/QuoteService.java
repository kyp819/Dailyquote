package com.example.dailyquote;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteService {
    @GET("/api")
    Call<ApiResponse> getQuote();
}
