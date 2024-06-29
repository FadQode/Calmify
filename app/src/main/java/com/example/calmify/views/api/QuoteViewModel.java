package com.example.calmify.views.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.calmify.model.Quote;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteViewModel extends ViewModel {
    private MutableLiveData<List<Quote>> quotes;
    private MutableLiveData<String> errorMessage;
    private QuotesApiService apiService;

    public QuoteViewModel() {
        apiService = RetrofitClient.getClient("https://api.api-ninjas.com/").create(QuotesApiService.class);
        quotes = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<List<Quote>> getQuotes() {
        return quotes;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchQuotes(String category) {
        apiService.getQuotes(category).enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    quotes.setValue(response.body());
                    errorMessage.setValue(null);  // Clear previous error message if any
                } else {
                    errorMessage.setValue("No quotes found for the category: " + category);
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                errorMessage.setValue("Error fetching quotes: " + t.getMessage());
            }
        });
    }
}
