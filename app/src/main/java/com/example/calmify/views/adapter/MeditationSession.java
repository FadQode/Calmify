package com.example.calmify.views.adapter;

public class MeditationSession {
    private long timestamp;
    private long duration;

    public MeditationSession() {
        // Default constructor required for calls to DataSnapshot.getValue(MeditationSession.class)
    }

    public MeditationSession(long timestamp, long duration) {
        this.timestamp = timestamp;
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}