package com.example.calmify.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calmify.R;
import com.example.calmify.model.MeditationSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFragment extends Fragment {


    private TextView totalSessionsTextView;
    private TextView totalTimeTextView;
    private ListView sessionsListView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<MeditationSession> sessionsList;
    private ArrayAdapter<MeditationSession> sessionsAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        totalSessionsTextView = view.findViewById(R.id.totalSessionsTextView);
        totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
        sessionsListView = view.findViewById(R.id.sessionsListView);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("meditations").child(firebaseAuth.getCurrentUser().getUid());

        sessionsList = new ArrayList<>();
        sessionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, sessionsList);
        sessionsListView.setAdapter(sessionsAdapter);

        loadMeditationSessions();

        return view;
    }

    private void loadMeditationSessions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sessionsList.clear();
                long totalTime = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MeditationSession session = snapshot.getValue(MeditationSession.class);
                    if (session != null) {
                        sessionsList.add(session);
                        totalTime += session.getDuration();
                    }
                }

                sessionsAdapter.notifyDataSetChanged();

                totalSessionsTextView.setText("Total Sessions: " + sessionsList.size());
                totalTimeTextView.setText("Total Time: " + formatDuration(totalTime));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ActivityFragment", "Failed to load sessions", databaseError.toException());
            }
        });
    }

    private String formatDuration(long durationMillis) {
        int seconds = (int) (durationMillis / 1000) % 60;
        int minutes = (int) ((durationMillis / 1000) / 60) % 60;
        int hours = (int) ((durationMillis / 1000) / 3600);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}