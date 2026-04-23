package com.example.checkincheckout;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UserCheckinActivity extends AppCompatActivity {

    ImageView capturedImage,tickMark;
    Button btnCheckIn, btnCheckOut, cameraBtn;
    DataBase db;
    String userName;
    private static final int CAMERA_REQUEST = 100;
    Bitmap capturedFaceBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkin);
        capturedImage = findViewById(R.id.capturedImage);
        btnCheckIn = findViewById(R.id.CheckIn);
        btnCheckOut = findViewById(R.id.CheckOut);
        cameraBtn = findViewById(R.id.Camera);
        tickMark=findViewById(R.id.tickMark);
      //  db = new DataBase(this);
        db = DataBase.getInstance(this);
        userName = getIntent().getStringExtra("username");
       // userName = getIntent().getStringExtra("username");

        if (userName == null || userName.isEmpty()) {
            Toast.makeText(this, "Username not received!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cameraBtn.setOnClickListener(v -> openCamera());
        btnCheckIn.setOnClickListener(v ->
        {

            if (capturedFaceBitmap == null)
            {
                Toast.makeText(this, "Capture face first", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap registeredFace = getRegisteredFaceFromDB(userName);

            if (registeredFace == null)
            {
                Toast.makeText(this, "No registered face found", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("DEBUG", "Captured size: " + capturedFaceBitmap.getWidth() + "x" + capturedFaceBitmap.getHeight());
            Log.d("DEBUG", "Registered size: " + registeredFace.getWidth() + "x" + registeredFace.getHeight());
            boolean isMatch = compareFaces(capturedFaceBitmap, registeredFace);

            if (isMatch)
            {
                String time = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());
                Toast.makeText(this, "Check-in Successful", Toast.LENGTH_SHORT).show();
                db.insertAttendance(userName, time);
                startActivity(new Intent(this, DashBoardActivity.class));
            }
            else
            {
                Toast.makeText(this, "Face Not Matched", Toast.LENGTH_SHORT).show();
            }

        });

        btnCheckOut.setOnClickListener(v ->
        {

            if (capturedFaceBitmap == null)
            {
                Toast.makeText(this, "Capture face first!", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap registeredFace = getRegisteredFaceFromDB(userName);
            if (registeredFace == null)
            {
                Toast.makeText(this, "No registered face found!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("DEBUG", "Captured size: " + capturedFaceBitmap.getWidth() + "x" + capturedFaceBitmap.getHeight());
            Log.d("DEBUG", "Registered size: " + registeredFace.getWidth() + "x" + registeredFace.getHeight());
            boolean isMatch = compareFaces(capturedFaceBitmap, registeredFace);

            if (isMatch)
            {
                String time = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());
                db.updateCheckOut(userName, time);
                Log.d("DEBUG", "CheckOut Time: " + time);
                Toast.makeText(this, "Checked Out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashBoardActivity.class));
            }
            else
            {
                Toast.makeText(this, "Face Not Matched", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private Bitmap resize(Bitmap image) {

        return Bitmap.createScaledBitmap(image, 200, 200, true);
    }
    private void openCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Bitmap original = (Bitmap) data.getExtras().get("data");
            capturedFaceBitmap = processFace(original);
            capturedImage.setImageBitmap(capturedFaceBitmap);
            tickMark.setVisibility(View.VISIBLE);
        }
    }
    public boolean compareFaces(Bitmap bmp1, Bitmap bmp2)
    {
        bmp1 = resize(bmp1);
        bmp2 = resize(bmp2);
        int matchCount = 0;
        int totalPixels = bmp1.getWidth() * bmp1.getHeight();

        for (int i = 0; i < bmp1.getWidth(); i++)
        {
            for (int j = 0; j < bmp1.getHeight(); j++)
            {

                int p1 = bmp1.getPixel(i, j);
                int p2 = bmp2.getPixel(i, j);
                int r1 = (p1 >> 16) & 0xff;
                int g1 = (p1 >> 8) & 0xff;
                int b1 = p1 & 0xff;
                int r2 = (p2 >> 16) & 0xff;
                int g2 = (p2 >> 8) & 0xff;
                int b2 = p2 & 0xff;
                int diff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                if (diff < 100) { // tolerance
                    matchCount++;
                }
            }
        }

        float similarity = (float) matchCount / totalPixels;
        Log.d("DEBUG", "Similarity: " + similarity);
        return similarity > 0.45;
    }

    private Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

    }
    private Bitmap getRegisteredFaceFromDB(String username)
    {

        byte[] faceBytes = db.getUserFace(username);

        if (faceBytes == null) return null;

        return android.graphics.BitmapFactory.decodeByteArray(faceBytes, 0, faceBytes.length);
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