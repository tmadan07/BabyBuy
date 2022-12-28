package com.Asarfi.BabyBuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText dbEmail, dbPassword;
    private TextView newUserTV;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbEmail = findViewById(R.id.Email);
        dbPassword = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.BtnLogin);
        loadingPB = findViewById(R.id.PBLoading);
        newUserTV = findViewById(R.id.TVNewUser);
        mAuth = FirebaseAuth.getInstance();

        // adding click listener for new user
        newUserTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a login activity.
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        // adding on click listener to login button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingPB.setVisibility(View.VISIBLE);
                String email = dbEmail.getText().toString();
                String password = dbPassword.getText().toString();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter your credentials..", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling a sign in method and passing email and password to it
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // checking if the task is success or not.
                        if (task.isSuccessful()) {

                            //  hiding our progress bar.
                            loadingPB.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Successful..", Toast.LENGTH_SHORT).show();

                            // opening our mainactivity.
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // hiding progress bar and displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Please enter valid user credentials..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // if the user is not null then main activity is open
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }

    }
}