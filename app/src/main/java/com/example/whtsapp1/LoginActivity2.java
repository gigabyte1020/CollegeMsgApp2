package com.example.whtsapp1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity2 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button LoginButton;
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
    }

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
                                Toast.makeText(LoginActivity2.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String msg=task.getException().toString();
                                Toast.makeText(LoginActivity2.this, msg, Toast.LENGTH_SHORT).show();
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

    }


    private void SendUsertoMain() {
        Intent mainIntent=new Intent(LoginActivity2.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Prevents user going back to login/register activity
        startActivity(mainIntent);
        finish();
    }
    private void SendUsertoReg() {
        Intent regIntent=new Intent(LoginActivity2.this,RegisterActivity.class);
        startActivity(regIntent);
        finish();
    }

}