package com.example.votegvp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

public class ViewProfileActivity extends AppCompatActivity {
    Button b2;
    EditText e1, e2;
    String vid;
    DBConnection db;
    TextView t1, t2, t3;
    ImageView dpImage;
    long userId = -1;
    private static final String TAG = "ViewProfileActivity";
    final int CAMERA_PIC_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();//Getting voter id from home page
        if (b != null) {
            vid = (String) b.get("voterid");
        }
        b2 = (Button) findViewById(R.id.button2);
        e1 = (EditText) findViewById(R.id.npass);
        e2 = (EditText) findViewById(R.id.cpass);
        t1 = (TextView) findViewById(R.id.textView6);
        t2 = (TextView) findViewById(R.id.textView7);
        t3 = (TextView) findViewById(R.id.textView8);
        dpImage = (ImageView) findViewById(R.id.dp_image);

        db = new DBConnection(this);
        Cursor cursor = db.getUserprofile(vid);


        if (cursor.moveToFirst()) {
            try {
                userId = cursor.getLong(0);
                Log.d(TAG, "onCreate: data: " + Arrays.toString(cursor.getColumnNames()));
                Log.d(TAG, "onCreate: data: " + cursor.getColumnIndex("_id"));
                Log.d(TAG, "onCreate: userId:" + userId);
                File file = Utils.getDpFile(this, userId);
                Glide.with(this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(dpImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String name = cursor.getString(1);
            String aid = cursor.getString(2);
            t1.setText("Name: " + name);
            t2.setText("Aadhar ID: " + aid);
            t3.setText("Voter ID: " + vid);
        }
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePass();
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

    public void updatePass() {

        String p = e1.getText().toString();
        String cp = e2.getText().toString();


        if ((!p.isEmpty() && !cp.isEmpty() && (p.equals(cp))) || bitmap != null) {
            if (bitmap != null) {
                Log.d(TAG, "updatePass: bitmap is not null, userId is " + userId);
                File imgFile = Utils.getDpFile(this, userId);
                try {
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    Toast.makeText(ViewProfileActivity.this, "Profile updated!!", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (p.equals(cp)) {
                Boolean updated = db.updatePassword(vid, p);
                if (updated)
                    Toast.makeText(this, "Password updated!!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Error updating password!!", Toast.LENGTH_LONG).show();
            }
            startActivity(new Intent(ViewProfileActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Both passwords must be same!!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "All fields are required!!", Toast.LENGTH_LONG).show();
        }
    }
}