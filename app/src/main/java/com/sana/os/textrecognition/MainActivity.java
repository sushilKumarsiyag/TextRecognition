package com.sana.os.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button captureimgbtn,detecttextbtn;
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureimgbtn=(Button)findViewById(R.id.captureimage);
        detecttextbtn=(Button)findViewById(R.id.detecttextimage);

        imageView=(ImageView)findViewById(R.id.image_view);
        textView=(TextView)findViewById(R.id.text_display);


        captureimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
                textView.setText("");

            }
        });
        detecttextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    detecttextformimg();
            }
        });
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap= (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


    private void detecttextformimg() {

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector= FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                displayTextfromimg(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextfromimg(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks=firebaseVisionText.getBlocks();
        if (blocks.size()==0)
        {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
        }
        else {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {
                String string=block.getText();
                textView.setText(string);
            }
        }

    }

}
