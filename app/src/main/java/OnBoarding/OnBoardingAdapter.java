package OnBoarding;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquote.MainActivity;
import com.example.dailyquote.R;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {

    List<Integer> layouts;
    Context context;

    public OnBoardingAdapter(Context context, List<Integer> layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new OnBoardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        if (position == layouts.size() - 1) {
            Button btnFinish = holder.itemView.findViewById(R.id.btnFinished);
            btnFinish.setOnClickListener(v -> {
                // Set the onboarding completed flag in SharedPreferences
                context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("completed", true)
                        .apply();

                context.startActivity(new Intent(context, MainActivity.class));
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layouts.get(position);
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }

    static class OnBoardingViewHolder extends RecyclerView.ViewHolder {
        OnBoardingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
