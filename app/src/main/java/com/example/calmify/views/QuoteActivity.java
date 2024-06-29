package com.example.calmify.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calmify.R;
import com.example.calmify.model.Quote;
import com.example.calmify.views.api.QuoteAdapter;
import com.example.calmify.views.api.QuoteViewModel;

import java.util.List;

public class QuoteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuoteAdapter adapter;
    private QuoteViewModel viewModel;
    private EditText categoryEditText;
    private Button fetchQuotesButton;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        categoryEditText = findViewById(R.id.categoryEditText);
        fetchQuotesButton = findViewById(R.id.fetchQuotesButton);
        errorTextView = findViewById(R.id.errorTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);
        viewModel.getQuotes().observe(this, new Observer<List<Quote>>() {
            @Override
            public void onChanged(List<Quote> quotes) {
                if (quotes != null) {
                    adapter = new QuoteAdapter(quotes);
                    recyclerView.setAdapter(adapter);
                    errorTextView.setVisibility(View.GONE);  // Hide error message
                }
            }
        });

        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    errorTextView.setText(errorMessage);
                    errorTextView.setVisibility(View.VISIBLE);  // Show error message
                }
            }
        });

        fetchQuotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categoryEditText.getText().toString().trim();
                viewModel.fetchQuotes(category);
            }
        });
    }
}
