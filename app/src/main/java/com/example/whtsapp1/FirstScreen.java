package com.example.whtsapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstScreen extends AppCompatActivity {
    private Button RegButton,LogButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        InitializeFields();
        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent=new Intent(FirstScreen.this,RegisterActivity.class);
                startActivity(rIntent);
            }
        });
        LogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logIntent=new Intent(FirstScreen.this,LoginActivity.class);
                startActivity(logIntent);
            }
        });
    }

    private void InitializeFields() {
        RegButton = (Button) findViewById(R.id.reg_button);
        LogButton = (Button) findViewById(R.id.login_button);
    }
}