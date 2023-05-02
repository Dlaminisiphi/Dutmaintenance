package com.example.dutmaintenance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button buttonLog_query,buttonLogout,buttonReview,buttonView;
    FirebaseUser user;
    FirebaseAuth auth;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        buttonLog_query=findViewById(R.id.log_query);
        buttonReview=findViewById(R.id.review);
        buttonLogout=findViewById(R.id.log_out);
        textView=findViewById(R.id.username_text);
        buttonView=findViewById(R.id.VView);
        user=auth.getCurrentUser();
        if(user==null){
            Intent intent= new Intent(getApplicationContext(),login.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());
        }

        buttonLog_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),logQuery.class);
                startActivity(intent);
                finish();

            }
        });
        buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),Review.class);
                startActivity(intent);
                finish();

            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();

            }

        });
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AllQueries.class);
                startActivity(intent);
                finish();

            }
        });
    }
}