package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SendMsg;
    private EditText uMsg;
    private ScrollView mScroll;
    private TextView dispText;
    private String currentGroupName,currentUserName,currentUserID,currentDate;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupRef,GroupKeyRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        Initialize();
        getUserInfo();
        SendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMsg();
                uMsg.setText("");
                mScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    DispMsg(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void SaveMsg() {
        String msg = uMsg.getText().toString();
        String msgKey=GroupRef.push().getKey(); //creates and assigns a unique key for each  group
        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat cDate= new SimpleDateFormat("MMM dd,yyyy");
            currentDate=cDate.format(cal.getTime());

            HashMap<String,Object> groupKey = new HashMap<>();
            GroupRef.updateChildren(groupKey);
            GroupKeyRef=GroupRef.child(msgKey);
            HashMap<String,Object> msgInfoMap = new HashMap<>();
            msgInfoMap.put("name",currentUserName);
            msgInfoMap.put("message",msg);
            msgInfoMap.put("date",currentDate);
            GroupKeyRef.updateChildren(msgInfoMap);

        }
    }

    private void getUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    currentUserName = snapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Initialize() {
        mToolbar=(Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        SendMsg=(ImageButton) findViewById(R.id.send_button);
        uMsg=(EditText)  findViewById(R.id.input_group);
        dispText=(TextView) findViewById(R.id.text_display);
        mScroll=(ScrollView) findViewById(R.id.scroll_view);
    }
    private void DispMsg(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            dispText.append(chatName + " :\n" + chatMsg +" \n" + chatDate + "\n\n\n"); //Appends chat data to the textview
            mScroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}