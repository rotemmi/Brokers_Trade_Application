package com.example.erbroker.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.erbroker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private final String loginPreferences = "login";

    // if user close the application without sign out , shared Preference triggered , call firebase authentication method.
    // initialize back button on click method (back to main_activity)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getSharedPreferences(loginPreferences, Context.MODE_PRIVATE).getString("Email", "") != "")
        {
            tryLogin();
        }
        else
        {
            setContentView(R.layout.login_activity);
            findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    MainActivity.startFadingAnimation(LoginActivity.this, v);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            });
        }
    }

    // authentication method with exist shared Preference
    public void tryLogin()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(loginPreferences, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("Email", "");
        String password = sharedPreferences.getString("Password", "");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Login Success",
                                    Toast.LENGTH_SHORT).show();
                            goUserActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    // getting user details from UI + authentication with firebase + creating new shared Preference;
    public void loginFunc(View view)
    {
        EditText emailText = findViewById(R.id.email_login);
        String email = emailText.getText().toString();
        EditText passText = findViewById(R.id.pass_login);
        String password = passText.getText().toString();
        if(!email.equals("")&& !password.equals(""))
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                MainActivity.startFadingAnimation(LoginActivity.this, view);
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(LoginActivity.this, "Login Success",
                                        Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences(loginPreferences, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Email", email);
                                editor.putString("Password", password);
                                editor.commit();
                                goUserActivity();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Login failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Wrong Values - Please Enter Details.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // going to user interface
    public void goUserActivity()
    {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }
}