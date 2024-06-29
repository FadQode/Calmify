package com.example.calmify.model;

import java.util.List;

public class QuoteResponse {
    private List<Quote> results;

    public List<Quote> getResults() { return results; }
    public void setResults(List<Quote> results) { this.results = results; }
}
