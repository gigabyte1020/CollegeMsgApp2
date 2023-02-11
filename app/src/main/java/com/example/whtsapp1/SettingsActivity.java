package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Button UpButton;
    private EditText uName, uStatus;
    private CircleImageView uImage;
    private String currentUserID,downloadUrl,str;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserImgRef;
    private static final int GalleryPick=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Initialize();

        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserImgRef = FirebaseStorage.getInstance().getReference().child("ProfImg");
        currentUserID=mAuth.getCurrentUser().getUid();
        UpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();
        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if((snapshot.exists()) &&(snapshot.hasChild("name"))&&snapshot.hasChild("image"))
                        {
                            String retrieveuName=snapshot.child("name").getValue().toString();
                            String retrieveuStatus=snapshot.child("class").getValue().toString();
                            String retrieveuImg=snapshot.child("image").getValue().toString(); //url stored here is wrong

                            uName.setText(retrieveuName);
                            uStatus.setText(retrieveuStatus);
                            Picasso.get().load(retrieveuImg).into(uImage);

                        }
                        else if((snapshot.exists()) &&(snapshot.hasChild("name")))
                        {
                            String retrieveuName=snapshot.child("name").getValue().toString();
//                            String retrieveuImg=snapshot.child("image").getValue().toString();

                            uName.setText(retrieveuName);

                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void Initialize() {
        UpButton=(Button) findViewById(R.id.update_set);
        uName=(EditText) findViewById(R.id.set_user_name);
        uStatus=(EditText) findViewById(R.id.set_status);
        uImage=(CircleImageView) findViewById(R.id.set_profile_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK &&data!=null){
            Uri ImageUri = data.getData();
            StorageReference filepath = UserImgRef.child(currentUserID + ".jpg");


            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downU)
                {
                    str = downU.toString();
                }
            });
            filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingsActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();

                        final String downloadUrl= str;


                        RootRef.child("Users").child(currentUserID).child("image")
                                .setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(SettingsActivity.this, "Image saved in db", Toast.LENGTH_SHORT).show();

                                        }
                                        else{
                                            Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    }
                    else{
                        Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    private void UpdateSettings() {
    String setUName=uName.getText().toString();
    String setUStatus=uStatus.getText().toString();
        if (TextUtils.isEmpty(setUName))
        {
            Toast.makeText(this, "Enter UserName", Toast.LENGTH_SHORT).show();
        }

        else
        {
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUName);
            profileMap.put("class",setUStatus);
            RootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendUsertoMain();
                                Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String msg=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
}
    private void SendUsertoMain() {
        Intent mainIntent=new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Prevents user going back to login/register activity
        startActivity(mainIntent);
        finish();
    }
}