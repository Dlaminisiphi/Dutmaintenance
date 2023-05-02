package com.example.dutmaintenance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminHome extends AppCompatActivity {
    Button buttonNewQuery,buttonQueryFixed,buttonQuerySeen,buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        buttonNewQuery=findViewById(R.id.new_query);
        buttonQuerySeen=findViewById(R.id.queries_seen);
        buttonQueryFixed=findViewById(R.id.Queries_fixed);
        buttonLogout=findViewById(R.id.log_out_Admin);
        buttonNewQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AdminPannel.class);
                startActivity(intent);
                finish();

            }
        });
        buttonQuerySeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),QueryRecieved.class);
                startActivity(intent);
                finish();

            }
        });
        buttonQueryFixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();

            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();

            }
        });
    }
}