package com.example.dutmaintenance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Review extends AppCompatActivity {
    RatingBar ratingBar;
    EditText reviewText;
    TextView rateCount, showRating,GoBack;
    float rateValue;String temp;
    Button submitButton;
    DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        rateCount=findViewById(R.id.rateCount);
        ratingBar = findViewById(R.id.rating_bar);
        reviewText = findViewById(R.id.review_text);
        submitButton = findViewById(R.id.submit_button);
        showRating=findViewById(R.id.shhowRating);
        GoBack=findViewById(R.id.go_backTo);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("reviews");

        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateValue=ratingBar.getRating();
                if (rateValue<=1 && rateValue>0)
                    rateCount.setText("Bad"+rateValue +"/5");
                else if (rateValue<=2 && rateValue>1)
                    rateCount.setText("Ok"+rateValue+"/5");
                else if (rateValue<=3 && rateValue>2)
                    rateCount.setText("Good"+rateValue+"/5");
                else if (rateValue<=4 && rateValue>3)
                    rateCount.setText("Very Good"+rateValue+"/5");
                else if (rateValue<=5 && rateValue>4)
                    rateCount.setText("Exceptionally Good"+rateValue+"/5");

            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a Review object and populate it with the review data
                Reviews reviews = new Reviews();
                reviews.setRating(rateValue);
                reviews.setReviewText(reviewText.getText().toString());
                reviews.setTimestamp(System.currentTimeMillis());

                // Call setValue() on the DatabaseReference object to store the data
                databaseRef.push().setValue(reviews);

                // Display the review to the user as before
                temp = rateCount.getText().toString();
                showRating.setText("Your Rating: \n" + temp + "\n" + reviewText.getText());
                ratingBar.setRating(0);
                rateCount.setText("");
            }
        });
    }
}