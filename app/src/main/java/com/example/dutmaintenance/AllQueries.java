package com.example.dutmaintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllQueries extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_queries);
        mRecyclerView=findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mProgressCircle=findViewById(R.id.progress_circle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mUploads = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // get the current user's ID
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads").child(userId); // modify reference to point to the current user's uploads node

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload= postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter=new ImageAdapter(AllQueries.this,mUploads);
                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllQueries.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }
}