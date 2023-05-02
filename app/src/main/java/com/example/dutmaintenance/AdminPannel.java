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
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AdminPannel extends AppCompatActivity implements AdminImageAdaptor.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AdminImageAdaptor mAdapter;
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
        setContentView(R.layout.activity_admin_pannel);
        mRecyclerView=findViewById(R.id.recycler_vieww);
        mRecyclerView.setHasFixedSize(true);
        mProgressCircle=findViewById(R.id.progress_circlee);
        LogOutBtn=findViewById(R.id.AHome_button);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mUploads= new ArrayList<>();
        mAdapter= new AdminImageAdaptor(AdminPannel.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminPannel.this);
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
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot uploadSnapshot : snapshot.getChildren()){
                                Upload upload=uploadSnapshot.getValue(Upload.class);
                                if(upload != null && "NOT SEEN".equals(upload.getStatus())) {
                                    upload.setkey(uploadSnapshot.getKey());
                                    mUploads.add(upload);
                                }
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
    public void  onQueryRecieved(int position) {



    }

    @Override
    public void onArchiveQuery(int position) {



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
    @Override
    public void onQueryFixed(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPannel.this);
        builder.setTitle("Enter person Assigned to fix the problem");

        // Set up the input
        final EditText input = new EditText(AdminPannel.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String assignedPerson = input.getText().toString();
                new SendEmailTask(assignedPerson).execute();
                Upload selectedItem = mUploads.get(position);
                String selectedKey = selectedItem.getkey();
                String uid = selectedItem.getUserId();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(uid).child(selectedKey);
                databaseRef.child("status").setValue("PROBLEM SEEN");
                databaseRef.child("assignedPerson").setValue(assignedPerson);

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
    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        private String assignedPerson;

        public SendEmailTask(String assignedPerson) {
            this.assignedPerson = assignedPerson;

        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {

                UUID uuid = UUID.randomUUID();
                String referenceNumber = uuid.toString();


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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("22149618@dut4life.ac.za"));
                message.setSubject("Query [" + referenceNumber + "]");
                message.setText("Hello,\n" +
                        "Thank you for reaching out to us. This is an automated message to confirm that we have received your query [" + referenceNumber + "] and our team is currently working on fixing it. We have assigned " + assignedPerson + " to fix your query. We understand the importance of your issue and we are doing our best to resolve it as quickly...");


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
                Toast.makeText(AdminPannel.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminPannel.this, "Email failed to send", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
