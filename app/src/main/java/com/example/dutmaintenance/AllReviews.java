package com.example.dutmaintenance;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.example.dutmaintenance.ReviewsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllReviews extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ReviewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        mRecyclerView = findViewById(R.id.reviews_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Reviews> reviewsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Reviews reviews = dataSnapshot.getValue(Reviews.class);
                    reviewsList.add(reviews);
                }
                mAdapter = new ReviewsAdapter(reviewsList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllReviewsActivity", "Failed to read reviews", error.toException());
            }
        });
    }
}
