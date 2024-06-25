package com.example.calmify.views.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.calmify.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeditateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeditateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MeditateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeditateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeditateFragment newInstance(String param1, String param2) {
        MeditateFragment fragment = new MeditateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final int REQUEST_CODE_PICK_AUDIO = 1;
    private static final int PERMISSION_REQUEST_READ_MEDIA_AUDIO = 2;
    private static final String KEY_TIME_LEFT_IN_MILLIS = "timeLeftInMillis";
    private static final String KEY_IS_TIMER_RUNNING = "isTimerRunning";
    private static final String KEY_MUSIC_URI = "musicUri";
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


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        chooseMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMusic();
            }
        });

        mediaPlayer = new MediaPlayer();

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
        // Validate and parse minutes and seconds
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

        timeLeftInMillis = (minutes * 60 + seconds) * 1000;

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
        Log.d(TAG, "chooseMusic: Button pressed");
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "chooseMusic: Requesting permission");
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Log.d(TAG, "chooseMusic: Permission already granted");
            pickAudio();
        }
    }

    private void pickAudio() {
        Log.d(TAG, "pickAudio: Launching intent to pick audio");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Received result");
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                pickAudio();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                Toast.makeText(getContext(), "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == getActivity().RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: Audio selected");
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