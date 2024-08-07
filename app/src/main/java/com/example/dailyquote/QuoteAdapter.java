package com.example.dailyquote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class QuoteAdapter extends ArrayAdapter<Quote> {
    private final Context context;
    private final List<Quote> quotes;
    private final AppDatabase db;

    public QuoteAdapter(Context context, List<Quote> quotes, AppDatabase db) {
        super(context, R.layout.list_item_quote, quotes);
        this.context = context;
        this.quotes = quotes;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);
        }

        Quote quote = quotes.get(position);

        TextView quoteTextView = convertView.findViewById(R.id.quoteTextView);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        ImageButton shareButton = convertView.findViewById(R.id.shareButton);

        quoteTextView.setText(quote.quote);

        deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                db.quoteDao().deleteQuote(quote);
                quotes.remove(position);
                ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
            }).start();
        });

        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, quote.quote);
            context.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
        });

        return convertView;
    }
}
