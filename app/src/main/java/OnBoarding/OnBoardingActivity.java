package OnBoarding;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dailyquote.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    OnBoardingAdapter adapter;
    DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        adapter = new OnBoardingAdapter(
                this,
                Arrays.asList(
                        R.layout.onboarding_1,
                        R.layout.onboarding_2,
                        R.layout.onboarding_3
                )
        );
        viewPager.setAdapter(adapter);

        // Set up the Dots Indicator
        dotsIndicator.attachTo(viewPager);

    }
}
