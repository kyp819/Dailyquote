package com.example.dailyquote;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SavedQuotesActivity extends AppCompatActivity {

    private ListView savedQuotesListView;
    private QuoteAdapter quoteAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quotes);

        db = AppDatabase.getDatabase(this);

        savedQuotesListView = findViewById(R.id.savedQuotesListView);

        List<Quote> initialList = new ArrayList<>();
        quoteAdapter = new QuoteAdapter(this, initialList, db);
        savedQuotesListView.setAdapter(quoteAdapter);

        loadSavedQuotes();
    }

    private void loadSavedQuotes() {
        new Thread(() -> {
            List<Quote> quotes = db.quoteDao().getAllQuotes();
            runOnUiThread(() -> {
                if (!quotes.isEmpty()) {
                    quoteAdapter.clear();
                    quoteAdapter.addAll(quotes);
                    quoteAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }
}
