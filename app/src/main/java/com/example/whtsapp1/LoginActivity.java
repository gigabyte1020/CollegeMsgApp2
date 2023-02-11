package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button LoginButton,scanlink;;
    private EditText email,password;
    private TextView reglink;
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

        scanlink.setOnClickListener(view -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this));
    }
    private void ScanUser() {


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
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        words = element.getValue();

                        Log.d("MainActivity",words);
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
                        final String regex = "\\d{10}";
                        final String string = words;

                        final Pattern pattern = Pattern.compile(regex);
                        final Matcher matcher = pattern.matcher(string);

                        while (matcher.find()) {

                            Toast.makeText(this, words, Toast.LENGTH_SHORT).show();
                            dob=words;
                            password.setText(dob);
                            //   stringBuilder.append(words +"\n");
                        }
                        //End of REGEX
                    }
                }

            }
        }}
    private void UserLogin() {
        String em=email.getText().toString();
        String pw=password.getText().toString();
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
            mAuth.signInWithEmailAndPassword(em,pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUsertoMain();
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String msg=task.getException().toString();
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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