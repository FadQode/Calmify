package com.example.calmify.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.calmify.R;
import com.example.calmify.views.adapter.MeditationSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MeditateFragment extends Fragment {

    private static final String KEY_TIME_LEFT_IN_MILLIS = "timeLeftInMillis";
    private static final String KEY_IS_TIMER_RUNNING = "isTimerRunning";
    private static final String KEY_MUSIC_URI = "musicUri";
    private static final int REQUEST_CODE_PICK_AUDIO = 1;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final String TAG = "MeditateFragment";

    private TextView timerTextView;
    private EditText minutesEditText, secondsEditText;
    private boolean isTimerRunning;
    private Button startButton, resetButton, chooseMusicButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private MediaPlayer mediaPlayer;
    private Uri musicUri;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meditate, container, false);

        timerTextView = view.findViewById(R.id.timerTextView);
        minutesEditText = view.findViewById(R.id.minutesEditText);
        secondsEditText = view.findViewById(R.id.secondsEditText);
        startButton = view.findViewById(R.id.startButton);
        resetButton = view.findViewById(R.id.resetButton);
        chooseMusicButton = view.findViewById(R.id.chooseMusicButton);

        startButton.setOnClickListener(v -> startTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        chooseMusicButton.setOnClickListener(v -> chooseMusic());

        mediaPlayer = new MediaPlayer();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("meditations").child(firebaseAuth.getCurrentUser().getUid());

        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong(KEY_TIME_LEFT_IN_MILLIS);
            isTimerRunning = savedInstanceState.getBoolean(KEY_IS_TIMER_RUNNING);
            musicUri = savedInstanceState.getParcelable(KEY_MUSIC_URI);

            if (isTimerRunning) {
                startTimer();
            } else {
                updateTimerText();
            }
        }

        return view;
    }

    private void startTimer() {
        int minutes = 0;
        int seconds = 0;
        try {
            minutes = Integer.parseInt(minutesEditText.getText().toString());
        } catch (NumberFormatException e) {
            minutesEditText.setText("0");
        }
        try {
            seconds = Integer.parseInt(secondsEditText.getText().toString());
        } catch (NumberFormatException e) {
            secondsEditText.setText("0");
        }

        if (minutes == 0 && seconds == 0) {
            Toast.makeText(getContext(), "Please enter a valid time.", Toast.LENGTH_SHORT).show();
            return;
        }

        timeLeftInMillis = (minutes * 60 + seconds) * 1000;

        int finalMinutes = minutes;
        int finalSeconds = seconds;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00:00");
                stopMusic();
                saveSession((finalMinutes * 60 + finalSeconds) * 1000); // Save the initial set duration
            }
        }.start();

        isTimerRunning = true;

        if (musicUri != null) {
            playMusic();
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerTextView.setText("00:00:00");
        minutesEditText.setText("0");
        secondsEditText.setText("0");
        stopMusic();
        isTimerRunning = false;
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void chooseMusic() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            pickAudio();
        }
    }

    private void pickAudio() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickAudio();
            } else {
                Toast.makeText(getContext(), "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == getActivity().RESULT_OK && data != null) {
            musicUri = data.getData();
            Toast.makeText(getContext(), "Music selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void playMusic() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getContext(), musicUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void saveSession(long duration) {
        long timestamp = System.currentTimeMillis();
        MeditationSession session = new MeditationSession(timestamp, duration);
        databaseReference.push().setValue(session)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Session saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save session", e));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_TIME_LEFT_IN_MILLIS, timeLeftInMillis);
        outState.putBoolean(KEY_IS_TIMER_RUNNING, isTimerRunning);
        outState.putParcelable(KEY_MUSIC_URI, musicUri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
