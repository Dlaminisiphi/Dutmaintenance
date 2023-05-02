package com.example.dutmaintenance;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class QueryRecieved extends AppCompatActivity implements AdminImageAdaptor.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AdminImageAdaptor mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mstorage;
    private DatabaseReference mDatabaseRef;
    private Button LogOutButtton;
    private ValueEventListener mDBListener;
    private Upload selectedUpload;
    private void processProblemDetails(String problemDetails) {
        // Handle the problem details here
    }
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_recieved);
        mRecyclerView=findViewById(R.id.QRecycler_view);
        mRecyclerView.setHasFixedSize(true);
        mProgressCircle=findViewById(R.id.QProgress_circle);
        LogOutButtton=findViewById(R.id.QHome_button);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LogOutButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AdminHome.class);
                startActivity(intent);
                finish();
            }
        });


        mUploads= new ArrayList<>();
        mAdapter= new AdminImageAdaptor(QueryRecieved.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(QueryRecieved.this);
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
                                if(upload != null && "PROBLEM SEEN".equals(upload.getStatus())) {
                                    upload.setkey(uploadSnapshot.getKey());
                                    mUploads.add(upload);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QueryRecieved.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressCircle.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QueryRecieved.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }
    @Override
    public void onItemClick(int position) {


    }
    @Override
    public void onQueryFixed(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QueryRecieved.this);
        builder.setTitle("What Was Done To Fix The problem");

        // Set up the input
        final EditText input = new EditText(QueryRecieved.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String FixedProblem = input.getText().toString();
                new QueryRecieved.SendEmailTask(FixedProblem).execute();
                Upload selectedItem = mUploads.get(position);
                String selectedKey = selectedItem.getkey();
                String uid = selectedItem.getUserId();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(uid).child(selectedKey);
                databaseRef.child("status").setValue("PROBLEM FIXED");
                databaseRef.child("adminComment").setValue(FixedProblem);








            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }









    @Override
    public void onQueryRecieved(int position) {



    }

    @Override
    public void onArchiveQuery(int position) {
        mUploads.remove(position);
        mAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Query successfully archived", Toast.LENGTH_SHORT).show();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        private String FixedProblem;

        public SendEmailTask(String FixedProblem) {
            this.FixedProblem = FixedProblem;

        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                // Create a new session with an authenticator
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("flaskschoolproject@gmail.com", "rwdgtyxxnyxlzmcf");
                    }
                });
                // Create a new message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("flaskschoolproject@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dlaminisiphi3@gmail.com, 22123087@dut4life.ac.za, 22149618@dut4life.ac.za"));
                message.setSubject("Query Fixed");
                message.setText("Hello,\n" +
                        "We are writing to inform you that your maintenance request has been successfully completed. Our team has resolved the issue and everything should be functioning normally now.\n" +
                        "Thank you for bringing this issue to our attention and for your patience while we worked on it.Report From Team:\n"+ FixedProblem+ "\nIf you have any further questions or concerns, please don't hesitate to reach out to us.\n" +
                        "We hope that you continue to have a pleasant experience using our service.\n" +
                        "Best regards,\n" +
                        "Sipho\nDut maintenance Team");

                Transport.send(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }



        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(QueryRecieved.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QueryRecieved.this, "Email failed to send", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendEmailTaskR extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Set up the properties of the SMTP server
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                // Create a new session with an authenticator
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("flaskschoolproject@gmail.com", "rwdgtyxxnyxlzmcf");
                    }
                });

                // Create a new message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("flaskschoolproject@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dlaminisiphi3@gmail.com, 22123087@dut4life.ac.za, 22149618@dut4life.ac.za"));
                message.setSubject("Query Fixed");
                message.setText("Hello,\n" +
                        "We are writing to inform you that your maintenance request has been successfully completed. Our team has resolved the issue and everything should be functioning normally now.\n" +
                        "Thank you for bringing this issue to our attention and for your patience while we worked on it. If you have any further questions or concerns, please don't hesitate to reach out to us.\n" +
                        "We hope that you continue to have a pleasant experience using our service.\n" +
                        "Best regards,\n" +
                        "[Sipho/Dut maintenance Team]");

                // Send the message
                Transport.send(message);

                return true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to send email", e);
                return false;
            }
        }



        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(QueryRecieved.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QueryRecieved.this, "Failed to send email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}