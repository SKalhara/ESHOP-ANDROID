package com.kalhara.eshopfinal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

//        setContentView(R.layout.activity_login);
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();


        binding.signinBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        findViewById(R.id.signin_btn_signup).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });


        binding.signinBtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signinInputEmail.getText().toString().trim();
                String password = binding.signinInputPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    binding.signinInputEmail.setError("Email is required");
                    binding.signinInputEmail.requestFocus();
                    return;//to do nothing after this line
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.signinInputEmail.setError("Please provide valid email");
                    binding.signinInputEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    binding.signinInputPassword.setError("Password is required");
                    binding.signinInputPassword.requestFocus();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    updateUI(firebaseAuth.getCurrentUser());

                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT)
                                            .show();
//                                    updateUI(null);
                                }
                            }
                        });
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}