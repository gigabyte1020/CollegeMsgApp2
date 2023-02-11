package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whtsapp1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button RegButton,scanlink;

    private EditText email,password,rol;
    private TextView loglink;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ActivityMainBinding binding;
    private static final int REQUEST_CAMERA_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeFields();
        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();
        loglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUsertoLog();
            }
        });
        scanlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanUser();
            }
        });
        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void ScanUser() {

        scanlink.setOnClickListener(view -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this));

    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = (result != null) ? result.getUri() : null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
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
            String adno = "";
            String dob = "";
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
                            Toast.makeText(this, words, Toast.LENGTH_SHORT).show();
                            adno=words;
                            email.setText(adno +"@ncas.in");

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

                            Toast.makeText(this, words, Toast.LENGTH_SHORT).show();
                            dob=words;
                            password.setText(dob);
                            rol.setText("student");
                         //   stringBuilder.append(words +"\n");
                        }
                        //End of REGEX
                    }
                }

            }
        }
    }


    private void CreateNewAccount() {
        String em=email.getText().toString();
        String pw=password.getText().toString();
        String roles=rol.getText().toString();
        if (TextUtils.isEmpty(em))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pw))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(em,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String currentUserID=mAuth.getCurrentUser().getUid();


                        RootRef.child("Users").child(currentUserID).setValue(roles);
                        SendUsertoMain();
                        Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SendUsertoLog() {
        Intent logIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(logIntent);
        finish();
    }
    private void SendUsertoMain() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Prevents user going back to login/register activity
        startActivity(mainIntent);
        finish();
    }

    private void InitializeFields() {
        RegButton = (Button) findViewById(R.id.register_button);
        email= (EditText) findViewById(R.id.register_email);
        password= (EditText) findViewById(R.id.register_password);
        rol= (EditText) findViewById(R.id.roles);
        loglink = (TextView) findViewById(R.id.login_link);
        scanlink = (Button) findViewById(R.id.scan_link);
    }
}