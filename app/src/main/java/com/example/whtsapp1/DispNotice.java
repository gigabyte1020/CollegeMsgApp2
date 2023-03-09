package com.example.whtsapp1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DispNotice extends AppCompatActivity {
    private String messageRecieverId,getMessageRecievername,messagereceiverimage,messageSenderId;

    private Toolbar chattoolbar;
    private ImageButton sendMessageButton,sendFileButton;
    private EditText dispTitle,dispBody;
    private FirebaseAuth mauth;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usermessagerecyclerview;


    private String title,body;
    private String checker="",myUrl="";
    private StorageTask uploadTask;
    private Uri fileuri;

    private String currentNoticeName,currentUserName,currentUserID,currentDate,currentTime;
    private DatabaseReference RootRef,GroupNameRef,GroupMessageRefKey;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_notice);
        loadingBar=new ProgressDialog(this);
        mauth= FirebaseAuth.getInstance();
        messageSenderId=mauth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();

        CalendarView simpleCalendarView = (CalendarView) findViewById(R.id.simpleCalendarView);
        currentNoticeName=getIntent().getExtras().get("noticename").toString(); //currently title+date
        currentUserID=mauth.getCurrentUser().getUid();
        Log.i("title1",currentNoticeName);
        simpleCalendarView.setDate(1463918226920L);
        Initialize();
        GetNoticeData();
        GetNoticeData2();

    }

    private void GetNoticeData2() {
        Query recentPostsQuery = RootRef.child("Notice").child("title")
                .equalTo(currentNoticeName);
            recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    Log.i("TAG",dataSnapshot.getValue().toString());
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                    }
                }
                else{
                    Log.i("Error","NO snapshot");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}

    private void GetNoticeData() {
        RootRef.child("Notice").child(currentNoticeName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if((snapshot.exists()) &&(snapshot.hasChild("title")))
                        {
                            String retrieveTitle=snapshot.child("title").getValue().toString();
                            String retrieveBody=snapshot.child("body").getValue().toString();
                            String retrieveDate=snapshot.child("date").getValue().toString();
                            String retrieveClass=snapshot.child("class").getValue().toString();
                            Log.i("title",retrieveTitle);

                            dispTitle.setText(retrieveTitle);
                            dispBody.setText(retrieveBody);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void Initialize() {
        dispTitle=(EditText) findViewById(R.id.dispTitle);
        dispBody=(EditText) findViewById(R.id.dispBody);
    }
}