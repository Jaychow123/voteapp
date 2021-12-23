package com.example.votegvp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {
    EditText e1, e2, e3, e4, e5;
    Button b1;
    DBConnection db;
    ImageView dpImage;
    final int CAMERA_PIC_REQUEST = 123;

    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        e1 = (EditText) findViewById(R.id.name);
        e2 = (EditText) findViewById(R.id.aadhar);
        e3 = (EditText) findViewById(R.id.voter);
        e4 = (EditText) findViewById(R.id.password);
        e5 = (EditText) findViewById(R.id.cpass);
        b1 = (Button) findViewById(R.id.button);
        dpImage = (ImageView) findViewById(R.id.dp_image);
        db = new DBConnection(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUser();
            }
        });

        View changeDp = findViewById(R.id.change_dp);
        changeDp.setOnClickListener(view -> {
            Toast.makeText(this, "Change dp", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent, "Select profile picture"
                    ),
                    CAMERA_PIC_REQUEST
            );
        });
    }

    Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Uri selectedPhotoUri = data.getData();
            Log.d(TAG, "onActivityResult: " + selectedPhotoUri);
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            selectedPhotoUri
                    );
                    Glide.with(this).load(bitmap)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(dpImage);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), selectedPhotoUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    Glide.with(this).load(selectedPhotoUri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(dpImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  FileOutputStream(dpFile()).use { out ->
    //      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    //  }

    public void insertUser() {
        String name = e1.getText().toString();
        String a = e2.getText().toString();
        String v = e3.getText().toString();
        String p = e4.getText().toString();
        String cp = e5.getText().toString();
        if (bitmap == null) {
            Toast.makeText(this, "Select profile image", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((!(name.equals(""))) && (!(a.equals(""))) && (!(v.equals(""))) && (!(p.equals(""))) && (!(cp.equals("")))) {
            if (p.equals(cp)) {
                boolean ifexisting = db.checkUserinDB(a, v);
                if (!ifexisting) {
                    Boolean ifexists = db.checkAVinDB(a, v);
                    long userId = db.addUser(name, a, v, p);
                    if (ifexists && (userId != -1)) {
                        // save image with userId.png
                        File imgFile = Utils.getDpFile(this, userId);
                        try {
                            FileOutputStream fos = new FileOutputStream(imgFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            Toast.makeText(this, "You have been successfully registered!!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Sorry your aadhar and voter id are not valid or dont match!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "You are already registered, go to Login!!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Passwords dont match!!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "All fields are required!!", Toast.LENGTH_LONG).show();
        }
    }
}