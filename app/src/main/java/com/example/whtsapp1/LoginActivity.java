package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button LoginButton,scanlink;;
    private EditText email,password;
    private TextView reglink;
    String adno = "";

    String dob = "";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;

    Button bTakePicture;
    private ImageCapture imageCapture;
    private static final int REQUEST_CAMERA_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();
        InitializeFields();
        reglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUsertoReg();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });



        bTakePicture.setOnClickListener(this);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());
    }
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bCapture: {
                capturePhoto();
                break;
            }

        }
    }
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)

                .build();


        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void capturePhoto() {
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        imageCapture.takePicture(Executors.newSingleThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                Bitmap bmap = imageProxyToBitmap(image);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getTextFromImage(bmap);

                    }
                });
            }
        });

    }
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bm= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return rotateImage(bm,90);
    }
    private static Bitmap rotateImage(Bitmap img, int degree)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> sparseArray = recognizer.detect(frame);

            SparseArray<TextBlock> textBlocks = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            String blocks = "";
            String lines = "";
            String words = "";
            String chck="Number:";
            int i=0;
            for (int index = 0; index < textBlocks.size(); index++)
            {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                blocks = blocks + tBlock.getValue() + "\n" + "\n";
                for (Text line : tBlock.getComponents()) {
                    //extract scanned text lines here
                    lines = lines + line.getValue() + "\n";
                    Log.d("MainActivity",lines);
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        words = element.getValue();

                        //  Log.d("MainActivity",words);
                        if(words .equals(chck)) {
                            i=1;
                            continue;
                        }
                        if(i==1){

                            adno=(words+"@ncas.in");
                            Toast.makeText(this, adno, Toast.LENGTH_SHORT).show();
                            //   stringBuilder.append(words +"\n");

                            i=0;
                        }


                        //    REGEX to find date
//                        final String regex = "\\d{2}-\\w{3}-\\d{2}";
                        final String regex = "\\d{10}";
                        final String string = words;

                        final Pattern pattern = Pattern.compile(regex);
                        final Matcher matcher = pattern.matcher(string);

                        while (matcher.find()) {
                            words = words.replaceAll("[^0-9+]", "");
                            Toast.makeText(this, words, Toast.LENGTH_SHORT).show();
                            dob=words;
                            //   stringBuilder.append(words +"\n");
                        }
                        if(adno !="" && dob!="")
                        {
                            UserLogin();
                        }
                        //End of REGEX
                    }
                }

            }
        }
    }
    private void UserLogin() {
        if (TextUtils.isEmpty(adno))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(dob))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(adno,dob)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUsertoMain();
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast t1;
                                String msg=task.getException().toString();
                                t1 =Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
                                t1.show();
                                t1.cancel();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        LoginButton = (Button) findViewById(R.id.login_button);
        email= (EditText) findViewById(R.id.login_email);
        password= (EditText) findViewById(R.id.login_password);
        reglink = (TextView) findViewById(R.id.register_link);
        bTakePicture = findViewById(R.id.bCapture);
        previewView = findViewById(R.id.previewView);
        scanlink = (Button) findViewById(R.id.scan_link);

    }


    private void SendUsertoMain() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Prevents user going back to login/register activity
        startActivity(mainIntent);
        finish();
    }
    private void SendUsertoReg() {
        Intent regIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(regIntent);
        finish();
    }

}