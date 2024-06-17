package com.example.calmify.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calmify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    ImageView goToSignUp;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signInButton;
    private FirebaseAuth firebaseAuth;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.signInButton);
        editTextEmail = findViewById(R.id.editTextEmailSignIn);
        editTextPassword = findViewById(R.id.editTextPasswordSignIn);
        goToSignUp = findViewById(R.id.imageView3);
        intent = new Intent(SignInActivity.this, MainActivity.class);
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            goToSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                }

            });

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = String.valueOf(editTextEmail.getText());
                    String password = String.valueOf(editTextPassword.getText());
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Sign in failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            return insets;
        });
    }
}