package com.example.calmify.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeditationSession {
    private long duration;
    private long timestamp;

    public MeditationSession() {
        // Default constructor required for calls to DataSnapshot.getValue(MeditationSession.class)
    }

    public MeditationSession(long duration, long timestamp) {
        this.duration = duration;
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Duration: " + formatDuration(duration) + ", Date: " + formatDate(timestamp);
    }

    private String formatDuration(long durationMillis) {
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / 1000) / 60) % 60;
        int hours = (int) ((durationMillis / 1000) / 3600);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String formatDate(long timestampMillis) {
        Date date = new Date(timestampMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
}