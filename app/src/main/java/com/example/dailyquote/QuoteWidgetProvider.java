package com.example.dailyquote;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class QuoteWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Get the saved quote from SharedPreferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String quote = preferences.getString("widget_quote", "Default Quote");

            views.setTextViewText(R.id.widget_quote_text, quote);

            appWidgetManager.updateAppWidget(appWidgetId, views);





        }
    }
}
