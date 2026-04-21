package com.example.checkincheckout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

public class UserRegisterActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 100;
    EditText userName, userEmail,userPhone,userPass;
    ImageView  faceImage,tickMark;
    Button registerBtn,camera ,gotoBtn;
    Bitmap capturedFaceBitmap = null;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        db = DataBase.getInstance(this);
        userName = findViewById(R.id.UserName);
        userEmail = findViewById(R.id.UserEmail);
        userPhone=findViewById(R.id.Phone);
        camera = findViewById(R.id.Camera);
        userPass=findViewById(R.id.Password);
        faceImage = findViewById(R.id.FaceImage);
        tickMark = findViewById(R.id.tickMark);
        registerBtn = findViewById(R.id.Register);
        //gotoBtn = findViewById(R.id.CheckinCheckout);
        camera.setOnClickListener(v -> openCamera());
        registerBtn.setOnClickListener(v -> registerUser());
    }
    private void openCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Bitmap original = (Bitmap) data.getExtras().get("data");
            capturedFaceBitmap = processFace(original);
            faceImage.setImageBitmap(capturedFaceBitmap);
            tickMark.setVisibility(View.VISIBLE);
        }
    }
    private Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private void registerUser()
    {
        String name = userName.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String phone =userPhone.getText().toString().trim();
        String pass=userPass.getText().toString().trim();
        if (email.isEmpty())
        {
            userEmail.setError("Enter email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            userEmail.setError("Invalid email");
            return;
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||pass.isEmpty() )
        {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capturedFaceBitmap == null)
        {
            Toast.makeText(this, "Capture face image", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap resized = resizeBitmap(capturedFaceBitmap);
        String faceBase64 = convertToBase64(resized);
        byte[] faceBytes = convertToBytes(capturedFaceBitmap);
        boolean success = db.insertUser(name, email, faceBytes,phone,pass);
        //boolean success = db.insertUser(name, email, faceBase64);
        getSharedPreferences("MyApp", MODE_PRIVATE)
                .edit()
                .putString("username", name)
                .apply();
        if (success)
        {
            Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserRegisterActivity.this, DashBoardActivity.class);
            intent.putExtra("username", name);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        int maxSize = 300;
        float ratio = (float) width / height;
        if (ratio > 1)
        {
            width = maxSize;
            height = (int) (width / ratio);
        }
        else
        {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private byte[] convertToBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        return baos.toByteArray();
    }
    private String convertToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
    private Bitmap processFace(Bitmap original)
    {

        Bitmap rotated = rotateBitmap(original, -90);
        int width = rotated.getWidth();
        int height = rotated.getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        Bitmap cropped = Bitmap.createBitmap(rotated, x, y, size, size);
        return Bitmap.createScaledBitmap(cropped, 200, 200, true);
    }
}