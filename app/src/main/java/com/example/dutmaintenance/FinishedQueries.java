package com.example.dutmaintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FinishedQueries extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FinalAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mstorage;
    private DatabaseReference mDatabaseRef;
    private Button LogOutBtn;
    private ValueEventListener mDBListener;
    private Upload selectedUpload;
    private void processProblemDetails(String problemDetails) {
        // Handle the problem details here
    }
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_queries);
        mRecyclerView=findViewById(R.id.FRecycler_view);
        mRecyclerView.setHasFixedSize(true);
        mProgressCircle=findViewById(R.id.FProgress_circle);
        LogOutBtn=findViewById(R.id.FHome_button);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mUploads= new ArrayList<>();
        mAdapter= new FinalAdapter(FinishedQueries.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mstorage=FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AdminHome.class);
                startActivity(intent);
                finish();
            }
        });

        mDBListener= mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren()){
                    String uid = userSnapshot.getKey();
                    DatabaseReference userRef = mDatabaseRef.child(uid);
                    //Gets data from database
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot uploadSnapshot : snapshot.getChildren()){
                                Upload upload=uploadSnapshot.getValue(Upload.class);
                                if(upload != null && "PROBLEM FIXED".equals(upload.getStatus())) {
                                    upload.setkey(uploadSnapshot.getKey());
                                    mUploads.add(upload);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(FinishedQueries.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FinishedQueries.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }
}