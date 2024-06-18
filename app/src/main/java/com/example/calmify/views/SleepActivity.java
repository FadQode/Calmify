package com.example.calmify.views;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calmify.R;

import java.text.MessageFormat;
import java.util.Locale;


public class SleepActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "SleepTimerChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    TextView textView;
    WebView webView;
    Button reset, start, awake, stop;
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

            updateNotification();
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showNotification();
                } else {
                    Toast.makeText(SleepActivity.this, "Please enable the notification permission", Toast.LENGTH_SHORT).show();
                }
            });
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
        awake = findViewById(R.id.awake);
        stop = findViewById(R.id.stop);
        webView = findViewById(R.id.webView);
        handler = new Handler(Looper.getMainLooper());

        createNotificationChannel();

        start.setOnClickListener(view -> {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            reset.setEnabled(false);
            stop.setEnabled(true);
            start.setEnabled(false);
            awake.setEnabled(true);
            webView.setVisibility(View.GONE);
            requestNotificationPermission();
        });

        stop.setOnClickListener(view -> {
            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            stop.setEnabled(false);
            start.setEnabled(true);
            awake.setEnabled(false);
            String url = "https://www.google.com/search?q=";
            url += "how+to+sleep";
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
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
            textView.setText("00:00:00:000");
            removeNotification();
        });

        awake.setOnClickListener(v -> {

            timeBuff += millisecondTime;
            handler.removeCallbacks(runnable);
            reset.setEnabled(true);
            stop.setEnabled(false);
            start.setEnabled(true);
            awake.setEnabled(false);
            String url = "https://www.google.com/search?q=";
            url += "things+to+do+after+awake";
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
            removeNotification();
        });

        textView.setText("00:00:00:000");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sleep Timer";
            String description = "Channel for Sleep Timer notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "Please enable the notification permission", Toast.LENGTH_SHORT).show();
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            showNotification();  // No need to request permission for lower API levels
        }
    }

    private void showNotification() {
        Intent intent = new Intent(this, SleepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_timer)  // Replace with your own icon
                .setContentTitle("Sleep Timer")
                .setContentText("Timer is running")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void updateNotification() {
        Intent intent = new Intent(this, SleepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_timer)  // Replace with your own icon
                .setContentTitle("Sleep Timer")
                .setContentText(String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliSeconds))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void removeNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification();
            } else {
                Toast.makeText(this, "Please enable the notification permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateTextView() {
        textView.setText(MessageFormat.format("{0}:{1}:{2}:{3}", hours, String.format(Locale.getDefault(), "%02d", minutes), String.format(Locale.getDefault(), "%02d", seconds), String.format(Locale.getDefault(), "%03d", milliSeconds)));
    }
}