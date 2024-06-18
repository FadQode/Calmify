package com.example.calmify.views;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.calmify.R;
import com.google.android.material.button.MaterialButton;
import java.text.MessageFormat;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    TextView textView;
    Button reset, start, stop;
    int hours, seconds, minutes, milliSeconds;
    long millisecondTime, startTime, timeBuff, updateTime = 0L;
    Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;

            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            hours = minutes / 60;

            minutes = minutes % 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000);

            textView.setText(MessageFormat.format("{0}:{1}:{2}:{3}",
                    hours,
                    String.format(Locale.getDefault(), "%01d", minutes),
                    String.format(Locale.getDefault(), "%02d", seconds),
                    String.format(Locale.getDefault(), "%03d", milliSeconds)
            ));

            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.textView);
        reset = findViewById(R.id.reset);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        handler = new Handler(Looper.getMainLooper());

        start.setOnClickListener(view -> {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            stop.setEnabled(true);
            start.setEnabled(false);
        });

        stop.setOnClickListener(view -> {
            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            stop.setEnabled(false);
            start.setEnabled(true);
        });

        reset.setOnClickListener(view -> {
            millisecondTime = 0L;
            startTime = 0L;
            timeBuff = 0L;
            updateTime = 0L;
            hours = 0;
            seconds = 0;
            minutes = 0;
            milliSeconds = 0;
            textView.setText("00:00:000");
        });

        textView.setText("00:00:000");
    }
}