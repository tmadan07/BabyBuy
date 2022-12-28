package com.Asarfi.BabyBuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText dbFullName,dbEmail, dbpassword, dbconfirmPwd;
    private TextView loginTV;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbFullName = findViewById(R.id.FullName);
        dbEmail = findViewById(R.id.Email);
        dbpassword = findViewById(R.id.Password);
        dbconfirmPwd = findViewById(R.id.ConfirmPassword);
        registerBtn = findViewById(R.id.BtnRegister);
        loadingPB = findViewById(R.id.PBLoading);
        loginTV = findViewById(R.id.TVLoginUser);
        mAuth = FirebaseAuth.getInstance();

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hiding our progress bar.
                loadingPB.setVisibility(View.VISIBLE);

                // getting data from our edit text.
                String fullName = dbFullName.getText().toString();
                String email = dbEmail.getText().toString();
                String password = dbpassword.getText().toString();
                String confirmPassword = dbconfirmPwd.getText().toString();

                // checking if the password and confirm password is equal or not.
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistrationActivity.this, "Please check both having same password..", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(fullName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword)) {

                    // checking if the text fields are empty or not
                    Toast.makeText(RegistrationActivity.this, "Please enter your credentials..", Toast.LENGTH_SHORT).show();
                } else {

                    // creating a new user by passing email and password.
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // checking if the task is success or not.
                            if (task.isSuccessful()) {

                                // in on success method we are hiding our progress bar and opening a login activity.
                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "User Registered Successfully..", Toast.LENGTH_SHORT).show();
                                OpenNewActivity();
                            } else {

                                // in else condition we are displaying a failure toast message.
                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "Fail to register user..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }

    private void OpenNewActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}