package com.kalhara.eshopfinal.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kalhara.eshopfinal.R;
import com.kalhara.eshopfinal.databinding.ActivitySignupBinding;
import com.kalhara.eshopfinal.model.User;

public class SignupActivity extends AppCompatActivity {


    private ActivitySignupBinding binding;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.signupBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.signupInputEmail.getText().toString().trim();
                String name = binding.signupInputName.getText().toString().trim();
                String password = binding.signupInputPassword.getText().toString().trim();
                String rPassword = binding.signupInputRetypePassword.getText().toString().trim();

                if (name.isEmpty()) {
                    binding.signupInputName.setError("Name is required");
                    binding.signupInputName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    binding.signupInputEmail.setError("Email is required");
                    binding.signupInputEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.signupInputEmail.setError("Please provide valid email");
                    binding.signupInputEmail.requestFocus();
                    return;
                }


                if (password.isEmpty()) {
                    binding.signupInputPassword.setError("Password is Required");
                    binding.signupInputPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    binding.signupInputPassword.setError("Password must have minimum 6 characters");
                    binding.signupInputPassword.requestFocus();
                    return;
                }

//                if (rPassword.isEmpty()) {
//                    binding.signupInputRetypePassword.setError("Re-Enter Your Password");
//                    binding.signupInputRetypePassword.requestFocus();
//                    return;
//                }

                if (!password.equals(rPassword)) {
                    binding.signupInputRetypePassword.setError("Passwords doesn't match");
                    binding.signupInputRetypePassword.requestFocus();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String uid = task.getResult().getUser().getUid();

                            User user = User.builder()
                                    .uid(uid)
                                    .name(name)
                                    .email(email).build();

                            firebaseFirestore.collection("users")
                                    .document(uid)
                                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(SignupActivity.this, "Saved success", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, "Saved failed", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        } else {
                        }
                    }
                });
            }
        });
    }
}