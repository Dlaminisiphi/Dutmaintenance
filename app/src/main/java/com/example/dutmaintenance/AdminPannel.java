package com.example.dutmaintenance;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    private SendEmailTask mSendEmailTask = new SendEmailTask();;
    private SendEmailTaskR mSendEmailTaslR = new SendEmailTaskR();;


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
        new SendEmailTask().execute();


    }

    @Override
    public void onQueryFixed(int position) {
        new SendEmailTaskR().execute();

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
    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Generate a reference number
                UUID uuid = UUID.randomUUID();
                String referenceNumber = uuid.toString();

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
                message.setSubject("Query [" + referenceNumber + "]");
                message.setText("Hello,\n" +
                        "Thank you for reaching out to us. This is an automated message to confirm that we have received your query [" + referenceNumber + "] and our team is currently working on fixing it. We understand the importance of your issue and we are doing our best to resolve it as quickly as possible.\n" +
                        "We appreciate your patience while we work to fix the issue. If you have any further questions or concerns, please don't hesitate to reach out to us. We will provide you with an update as soon as we have more information.\n" +
                        "Thank you for contacting us.\n" +
                        "Best regards,\n" +
                        "[Sipho\n Dut maintenance Team]");

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
                Toast.makeText(AdminPannel.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminPannel.this, "Failed to send email", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminPannel.this, "Email sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminPannel.this, "Failed to send email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
