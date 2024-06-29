package com.example.calmify.views.api;

import com.example.calmify.model.Quote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface QuotesApiService {
    @GET("v1/quotes")
    @Headers("X-Api-Key: DCOqt1BijJIt9SOg2cK4entQcbMH3yMPgzR1p11l")  // Replace with your actual API key
    Call<List<Quote>> getQuotes(@Query("category") String category);
}

