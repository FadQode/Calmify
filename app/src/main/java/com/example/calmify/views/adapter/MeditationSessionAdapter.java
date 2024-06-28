package com.example.calmify.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calmify.R;

import java.util.Date;
import java.util.List;

public class MeditationSessionAdapter extends RecyclerView.Adapter<MeditationSessionAdapter.ViewHolder> {
    private List<MeditationSession> sessions;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timestampTextView;
        public TextView durationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
        }
    }

    public MeditationSessionAdapter(List<MeditationSession> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meditation_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeditationSession session = sessions.get(position);
        holder.timestampTextView.setText(new Date(session.getTimestamp()).toString());

        long durationInSeconds = Math.round(session.getDuration() / 1000.0);
        holder.durationTextView.setText(String.valueOf(durationInSeconds) + " seconds");
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
