package com.example.regis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.regis.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static final int PICK_IMAGE = 1;
    Uri url;
    Bitmap bitmap;
    FirebaseStorage storage;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.imageview.setOnClickListener(view -> {

            // Dexter library for managing permission

            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"select image"),PICK_IMAGE);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });


        binding.submitBtn.setOnClickListener(view -> {

            // Method
            UploadDataToFirebase();

        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            url = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(url);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.imageview.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UploadDataToFirebase() {

        String name = binding.nameEt.getText().toString().trim();
        String course = binding.courseEt.getText().toString().trim();
        String rollNo = binding.rollNoEt.getText().toString();
        String duration = binding.durationEt.getText().toString();

        if (url==null){
            Toast.makeText(this, "Select image", Toast.LENGTH_SHORT).show();
        }else if (name.isEmpty()){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        }else if (course.isEmpty()){
            Toast.makeText(this, "Enter course", Toast.LENGTH_SHORT).show();
        }else if (rollNo.isEmpty()){
            Toast.makeText(this, "Enter RollNo", Toast.LENGTH_SHORT).show();
        }else if (duration.isEmpty()){
            Toast.makeText(this, "Enter course duration", Toast.LENGTH_SHORT).show();
        }else {

            binding.progressbar.setVisibility(View.VISIBLE);
            binding.submitBtn.setVisibility(View.INVISIBLE);

            StorageReference reference = storage.getReference().child("Image"+ new Random().nextInt(50));



            reference.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            studentsModel student = new studentsModel(name,course,duration,rollNo,uri.toString());

                            DatabaseReference reference = database.getReference().child(name);
                            reference.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();

                                        binding.imageview.setImageResource(R.drawable.img);
                                        binding.nameEt.setText("");
                                        binding.courseEt.setText("");
                                        binding.durationEt.setText("");
                                        binding.rollNoEt.setText("");

                                        binding.progressbar.setVisibility(View.INVISIBLE);
                                        binding.submitBtn.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    });

                }
            });

        }


    }

}