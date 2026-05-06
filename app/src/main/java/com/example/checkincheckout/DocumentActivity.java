package com.example.checkincheckout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;

public class DocumentActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 101;
    private Uri imageUri;
    ImageView imgDoc;
    TextView txtResult;
    Button btnCapture,goTo;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        // 🔥 Correct IDs (IMPORTANT)
        imgDoc = findViewById(R.id.imgDoc);
        txtResult = findViewById(R.id.Result);
        btnCapture = findViewById(R.id.Capture);
         goTo=findViewById(R.id.GOTO);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                new androidx.appcompat.app.AlertDialog.Builder(DocumentActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
goTo.setOnClickListener( v ->
{
    startActivity(new Intent(this, DashBoardActivity.class));

});
        btnCapture.setOnClickListener(v -> openCamera());
    }

    // 🔥 Open Camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(getExternalFilesDir(null), "doc.jpg");
       // File photoFile = new File(getExternalFilesDir("Pictures"), "doc.jpg");

        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        imageUri
                );

                // 🔥 Rotate (fix orientation)
                bitmap = rotateBitmap(bitmap, 90);

                imgDoc.setImageBitmap(bitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, 1200, 1200, true);
                runOCR(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void runOCR(Bitmap bitmap) {

        // 🔥 Convert to grayscale (improves accuracy)
        Bitmap processed = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true);

        InputImage image = InputImage.fromBitmap(processed, 0);

        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(text -> {

                    String resultText = text.getText();

                    if (resultText.trim().isEmpty()) {
                        txtResult.setText("No clear text found.\n\n👉 Try better lighting / closer image.");
                    } else {
                        txtResult.setText(resultText);
                    }

                })
                .addOnFailureListener(e ->
                        txtResult.setText("Error: " + e.getMessage()));
    }
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(
                source,
                0,
                0,
                source.getWidth(),
                source.getHeight(),
                matrix,
                true
        );
    }
    private void formatResult(Text text) {

        StringBuilder result = new StringBuilder();

        for (Text.TextBlock block : text.getTextBlocks()) {
            result.append(block.getText()).append("\n\n");
        }

        if (result.length() == 0) {
            txtResult.setText("No text found");
        } else {
            txtResult.setText(result.toString());
        }
    }
}