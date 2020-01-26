package com.tamuhack.tamuhackairlines;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashMap;
import java.util.Map;

public class SignUpActivity extends MainActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText verifyEmailField;
    private EditText verifyPasswordField;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        verifyEmailField = findViewById(R.id.fieldEmailVerify);
        verifyPasswordField = findViewById(R.id.fieldPasswordVerify);

        // Buttons
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, String> json = new LinkedHashMap<>();
                            String emailPost = mEmailField.getText().toString();
                            json.put("username", emailPost);
                            String str = Poster.POST("http://tamuflights.tech:5000/registeruser", json);
                            Log.v("Response", str);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        String emailVerify = verifyEmailField.getText().toString();
        if (TextUtils.isEmpty(email) || !email.equals(emailVerify)) {
            mEmailField.setError("Required.");
            valid = false;
        } else if (!email.equals(emailVerify)) {
            mEmailField.setError("Required.");
            verifyEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        String password = mPasswordField.getText().toString();
        String passwordVerify = verifyPasswordField.getText().toString();
        if (TextUtils.isEmpty(password) || !password.equals(passwordVerify)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if (!password.equals(passwordVerify)) {
            mPasswordField.setError("Required.");
            verifyPasswordField.setError("Required.");
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
           // TextView textViewToChange = findViewById(R.id.loggedInUserDisplay);
           // textViewToChange.setText(user.getEmail() + ", Is currently logged in!");
        } else {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }
}