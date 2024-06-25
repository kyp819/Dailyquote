package Ui;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyquote.R;

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.appName);

        // Load animations
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Start animations
        logo.startAnimation(scaleAnimation);
        appName.startAnimation(fadeInAnimation);
    }
}
