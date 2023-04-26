package com.example.dutmaintenance;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.google.firebase.storage.OnProgressListener;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Random;


public class logQuery extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST =1;

    private Spinner campusSpinner;
    private Spinner blockSpinner;
    private EditText floorEditText;
    private Spinner maintenanceSpinner, problemSpinner;
    private EditText problemEditText;
    private TextView GobackQ;
    private Button uploadButton, SendButton,ViewQueryButton;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_SELECT = 1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_query);
        uploadButton = findViewById(R.id.upload_button);
        SendButton = findViewById(R.id.send_query);
        campusSpinner = findViewById(R.id.campus_spinner);
        blockSpinner = findViewById(R.id.building_spinner);
        floorEditText = findViewById(R.id.floor_edit_text);
        problemEditText = findViewById(R.id.problem_edit_text);
        problemSpinner = findViewById(R.id.maintenance_spinner);
        mImageView= findViewById(R.id.image_view);
        mProgressBar= findViewById(R.id.progress_bar);
        ViewQueryButton=findViewById(R.id.see_query);
        GobackQ=findViewById(R.id.go_backToQ);
        mStorageRef=FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef=FirebaseDatabase.getInstance().getReference("uploads");
        GobackQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });





        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String floor,problem;
                floor=String.valueOf(floorEditText.getText());
                problem=String.valueOf(problemEditText.getText());
                if(TextUtils.isEmpty(floor)){
                    Toast.makeText(logQuery.this, "Please Fill In the Block Floor", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(problem)){
                    Toast.makeText(logQuery.this, "Please write A Detailed Description", Toast.LENGTH_SHORT).show();
                }else if (mUploadTask!=null && mUploadTask.isInProgress()){
                    Toast.makeText(logQuery.this, "Upload in progress", Toast.LENGTH_SHORT).show();


                }else {
                    uploadFile();

                }

            }
        });
        ViewQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AllQueries.class);
                startActivity(intent);

            }
        });
        String[] campusOptions = getResources().getStringArray(R.array.campus_options);
        String[] blockOptionsA = getResources().getStringArray(R.array.block_options_a);
        String[] blockOptionsB = getResources().getStringArray(R.array.block_options_b);
        String[] blockOptionsC = getResources().getStringArray(R.array.block_options_c);
        String[] blockOptionsD = getResources().getStringArray(R.array.block_options_d);
        String[] problemOptions = getResources().getStringArray(R.array.problem_options);

        //MAINTENANCE SPINNER
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, problemOptions);
        problemSpinner.setAdapter(adapter);
        problemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                // save the selected option to a variable or do something else with it
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        //CAMPUS SPINNER || CAMPUS BLOCK
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, campusOptions);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusAdapter);
        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCampus = parent.getItemAtPosition(position).toString();
                ArrayAdapter<String> blockAdapter;

                switch (selectedCampus) {
                    case "Steve Biko Campus":
                        blockAdapter = new ArrayAdapter<>(logQuery.this, android.R.layout.simple_spinner_item, blockOptionsA);
                        break;
                    case "Ritson Campus":
                        blockAdapter = new ArrayAdapter<>(logQuery.this, android.R.layout.simple_spinner_item, blockOptionsB);
                        break;
                    case "Ml Sultan Campus":
                        blockAdapter = new ArrayAdapter<>(logQuery.this, android.R.layout.simple_spinner_item, blockOptionsC);
                        break;
                    case "City Campus":
                        blockAdapter = new ArrayAdapter<>(logQuery.this, android.R.layout.simple_spinner_item, blockOptionsD);
                        break;
                    default:
                        blockAdapter = new ArrayAdapter<>(logQuery.this, android.R.layout.simple_spinner_item, new String[]{});
                        break;
                }

                blockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                blockSpinner.setAdapter(blockAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });


    }
    private void openFileChooser(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch(intent);
    }
    private ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            mImageUri = data.getData();
                            Picasso.with(logQuery.this).load(mImageUri).into(mImageView);
                        }
                    }
                }
            });
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask=fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(logQuery.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // get the current user's ID
                                            Upload upload = new Upload(campusSpinner.getSelectedItem().toString(), blockSpinner.getSelectedItem().toString(), floorEditText.getText().toString(), problemEditText.getText().toString(), problemSpinner.getSelectedItem().toString(), uri.toString());
                                            String uploadId = mDatabaseRef.child(userId).push().getKey(); // store the uploaded data under the current user's ID
                                            mDatabaseRef.child(userId).child(uploadId).setValue(upload);


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(logQuery.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }


                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(logQuery.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(logQuery.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}

