package com.example.dailyquote;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView rotatingImageView;
    private static final int REQUEST_CODE_CAMERA_STORAGE = 101;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 102;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 103;

    private Button capturePhotoButton;
    private Button removePhotoButton;
    private Button saveQuoteButton;
    private Button goToSavedQuotesButton;
    private ImageView backgroundImageView;
    private Uri photoUri;
    private AppDatabase db;
    private static final String TAG = "MainActivity";

    private QuoteAdapter quoteAdapter;
    private TextView quoteTextView;
    private TextView authorTextView;
    private QuoteUpdateReceiver quoteUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotatingImageView = findViewById(R.id.rotatingImageView);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotatingImageView.startAnimation(rotation);
        Handler handler = new Handler();
        Runnable rotateRunnable = new Runnable() {
            @Override
            public void run() {
                rotatingImageView.startAnimation(rotation);
                handler.postDelayed(this, 5000); // Rotate every 5 seconds
            }
        };
        handler.postDelayed(rotateRunnable, 5000);

        db = AppDatabase.getDatabase(this);
        goToSavedQuotesButton = findViewById(R.id.goToSavedQuotesButton);
        saveQuoteButton = findViewById(R.id.saveQuoteButton);

        List<Quote> initialList = new ArrayList<>();
        quoteAdapter = new QuoteAdapter(this, initialList, db);
//goToSavedQuotesButton
        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        removePhotoButton = findViewById(R.id.removePhotoButton);
        backgroundImageView = findViewById(R.id.backgroundImageView);

        capturePhotoButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                openCamera();
            }
        });

        removePhotoButton.setOnClickListener(v -> {
            removeBackgroundImage();
        });

        saveQuoteButton.setOnClickListener(v -> {
            saveQuote();
        });
goToSavedQuotesButton.setOnClickListener(v -> {
            goToSavedQuotes();
        });
        requestExactAlarmPermission();
        createNotificationChannel();
        requestNotificationPermission();

        // Initialize and register the receiver
        quoteUpdateReceiver = new QuoteUpdateReceiver();
        IntentFilter filter = new IntentFilter("com.example.dailyquote.UPDATE_QUOTE");
        registerReceiver(quoteUpdateReceiver, filter);

        // Load saved quote and background image
        loadSavedQuote();
        loadSavedImage();
        loadSavedAuthor();
    }

    private void goToSavedQuotes() {
        Intent intent = new Intent(MainActivity.this, SavedQuotesActivity.class);
        startActivity(intent);
    }

    private void loadSavedAuthor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedAuthor = preferences.getString("widget_author", null);
        if (savedAuthor != null) {
            authorTextView.setText(savedAuthor);
        }
    }

    private void loadSavedQuote() {
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

    private void saveQuoteToPreferences(String quote) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("widget_quote", quote);
        editor.apply();
    }

    private void saveQuote() {
        String quoteText = quoteTextView.getText().toString();
        String author = authorTextView.getText().toString();
        Quote quote = new Quote();
        quote.quote = quoteText;
        quote.author = author;

        new Thread(() -> {
            db.quoteDao().insertQuote(quote);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Quote saved!", Toast.LENGTH_SHORT).show();
              //  Intent intent = new Intent(MainActivity.this, SavedQuotesActivity.class);
              //  startActivity(intent);
                //loadSavedQuote();
            });
        }).start();
    }

    private void updateWidget() {
        Intent intent = new Intent(this, QuoteWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, QuoteWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }

    private void updateQuote() {
        Intent intent = new Intent(this, QuoteWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, QuoteWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(quoteUpdateReceiver);
    }

    public void updateQuoteTextViews(String quote, String author) {
        quoteTextView.setText(quote);
        authorTextView.setText(author);
        saveQuoteToPreferences(quote);
        updateWidget();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_CAMERA_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera and storage permissions denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationScheduler.scheduleDailyNotification(this);
            } else {
                Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoUri = createImageUri();
            if (photoUri != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri createImageUri() {
        try {
            File imagePath = new File(getFilesDir(), "images");
            if (!imagePath.exists()) imagePath.mkdirs();
            File newFile = new File(imagePath, "default_image.jpg");
            return FileProvider.getUriForFile(this, "com.example.dailyquote.fileprovider", newFile);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create image Uri", e);
            return null;
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    displayCapturedImage(photoUri);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            });

    private void displayCapturedImage(Uri photoUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                backgroundImageView.setImageBitmap(bitmap);
                backgroundImageView.setVisibility(View.VISIBLE);

                saveImagePath(photoUri.toString());
            } else {
                Toast.makeText(this, "Failed to display image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to display image", e);
            Toast.makeText(this, "Failed to display image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImagePath(String path) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("background_image_path", path);
        editor.apply();
    }

    private void loadSavedImage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String imagePath = preferences.getString("background_image_path", null);

        if (imagePath != null) {
            Uri imageUri = Uri.parse(imagePath);
            try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    backgroundImageView.setImageBitmap(bitmap);
                    backgroundImageView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to load image", e);
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeBackgroundImage() {
        backgroundImageView.setImageBitmap(null);
        backgroundImageView.setVisibility(View.GONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("background_image_path");
        editor.apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DailyQuoteChannel";
            String description = "Channel for Daily Quote Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("dailyquote", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
            } else {
                NotificationScheduler.scheduleDailyNotification(this);
            }
        } else {
            NotificationScheduler.scheduleDailyNotification(this);
        }
    }

    private class QuoteUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String quote = intent.getStringExtra("quote");
            String author = intent.getStringExtra("author");

            updateQuoteTextViews(quote, author);
        }
    }
}
