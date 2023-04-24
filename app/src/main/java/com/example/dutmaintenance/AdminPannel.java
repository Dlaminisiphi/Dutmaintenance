package com.example.dutmaintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminPannel extends AppCompatActivity implements AdminImageAdaptor.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AdminImageAdaptor mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mstorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pannel);
        mRecyclerView=findViewById(R.id.recycler_vieww);
        mRecyclerView.setHasFixedSize(true);
        mProgressCircle=findViewById(R.id.progress_circlee);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads= new ArrayList<>();
        mAdapter= new AdminImageAdaptor(AdminPannel.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminPannel.this);
        mstorage=FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        mDBListener= mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren()){
                    String uid = userSnapshot.getKey();
                    DatabaseReference userRef = mDatabaseRef.child(uid);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot uploadSnapshot : snapshot.getChildren()){
                                Upload upload=uploadSnapshot.getValue(Upload.class);
                                upload.setkey(uploadSnapshot.getKey());
                                upload.setemail(uploadSnapshot.getKey());
                                mUploads.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();

                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AdminPannel.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminPannel.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onQueryRecieved(int position) {
        Upload selecteEmail=mUploads.get(position);
        String selectedemail=selecteEmail.getemail();

    }

    @Override
    public void onQueryFixed(int position) {

    }

    @Override
    public void onArchiveQuery(int position) {
        Upload selectedItem=mUploads.get(position);
        String selectedkey = selectedItem.getkey();

        StorageReference imageRef=mstorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mDatabaseRef.child(selectedkey).removeValue();
                Toast.makeText(AdminPannel.this, "Query Archived", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
