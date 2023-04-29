package com.example.dutmaintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class signup extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextPassword1;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button buttonSignup;
    TextView textView;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent= new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        editTextPassword1=findViewById(R.id.password1);
        buttonSignup=findViewById(R.id.signUp_btn);
        progressBar=findViewById(R.id.ProgressBar);
        textView=findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(view.VISIBLE);
                String email,password,password1;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText());
                password1=String.valueOf(editTextPassword1.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(signup.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signup.this, "Enter Your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password1)){
                    Toast.makeText(signup.this, "Please Re-enter Password", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (!password.equals(password1)) {
                    progressBar.setVisibility(view.GONE);
                    Toast.makeText(signup.this, "Your passwords do not match", Toast.LENGTH_SHORT).show();
                    return;


                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(view.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(signup.this, "Account Created",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent= new Intent(getApplicationContext(),login.class);
                                        startActivity(intent);
                                        finish();


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(signup.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }


            }
        });
    }
}