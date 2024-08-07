package com.example.dailyquote;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "dailyquote";
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Notification triggered");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://quotes-6oci.onrender.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuoteService quoteService = retrofit.create(QuoteService.class);
        quoteService.getQuote().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && !apiResponse.getData().isEmpty()) {
                        //Get current day of the year
                        Calendar calendar = Calendar.getInstance();
                        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                        //Select quote based on the current day
                        int quoteIndex = dayOfYear % apiResponse.getData().size();
                        ApiResponse.Quote quote = apiResponse.getData().get(quoteIndex);

                        //Show notification
                        showNotification(context, quote.getQuote(), quote.getAuthor());

                        //Send broadcast to update UI
                        Intent updateUIIntent = new Intent("com.example.dailyquote.UPDATE_QUOTE");
                        updateUIIntent.putExtra("quote", quote.getQuote());
                        updateUIIntent.putExtra("author", quote.getAuthor());
                        context.sendBroadcast(updateUIIntent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch quote: ", t);
            }
        });
    }

    private void showNotification(Context context, String quote, String author) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Quote Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Daily Motivational Quote")
                .setContentText(quote + " - " + author)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
