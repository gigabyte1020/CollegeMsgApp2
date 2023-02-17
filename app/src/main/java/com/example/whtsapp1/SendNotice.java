package com.example.whtsapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
public class SendNotice extends AppCompatActivity {
    EditText sendTitle,sendBody;
    Button Msg;
    private String checker="",myUrl="";
    private Uri fileuri;private ProgressDialog loadingBar;
    ImageButton sendFilesBtn;
    String titleText="",BodyText="",dateText="",allclasses="";
    TextView selectClass,dispfileuri;
    private Button mPickDateButton;
    private TextView mShowSelectedDateText;
    boolean[] selectedClass;
    ArrayList<Integer> langList = new ArrayList<>();
    String[] langArray = {"BCA", "BBA", "Kotlin", "C", "Python", "Javascript"};
    private DatabaseReference RootRef,NoticeRef;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notice);
        RootRef= FirebaseDatabase.getInstance().getReference("Notice");
        NoticeRef=RootRef.push();
        Initialize();
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        loadingBar=new ProgressDialog(this);

        materialDateBuilder.setTitleText("SELECT A DATE");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        mPickDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        mShowSelectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                        dateText=materialDatePicker.getHeaderText();


                    }
                });
        Msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Sending File");
                loadingBar.setMessage("please wait, we are sending that file...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                titleText=sendTitle.getText().toString();
                BodyText=sendBody.getText().toString();
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Notice Files");
                DatabaseReference Usermessagekeyref=NoticeRef.push();
                final String messagePushID=Usermessagekeyref.getKey();
                final StorageReference filepath=storageReference.child(messagePushID+"."+checker);

                filepath.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        Map messageDocsBody = new HashMap();
                        messageDocsBody.put("title",titleText);
                        messageDocsBody.put("body",BodyText);
                        messageDocsBody.put("date",dateText);
                        messageDocsBody.put("class",allclasses);
                        messageDocsBody.put("messageID", messagePushID);
                        messageDocsBody.put("file", downloadUrl);
                        Map messageBodyDDetail = new HashMap();
//                                messageBodyDDetail.put(messageReceiverRef + "/" + messagePushID, messageDocsBody);

                        NoticeRef.updateChildren(messageDocsBody);
                        loadingBar.dismiss();
                    }
                });
            }
        });
            }
        });



        selectClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendNotice.this);

                // set title
                builder.setTitle("Select Language");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(langArray, selectedClass, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            langList.add(i);
                            // Sort array list
                            Collections.sort(langList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < langList.size(); j++) {
                            // concat array value
                            stringBuilder.append(langArray[langList.get(j)]);
                            // check condition
                            if (j != langList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        selectClass.setText(stringBuilder.toString());
                        allclasses=stringBuilder.toString();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                // show dialog
                builder.show();
            }

        });
        sendFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[]=new CharSequence[]{
                            "Images","PDF Files","Ms Word Files"
                    };

                    AlertDialog.Builder builder=new AlertDialog.Builder(SendNotice.this);
                    builder.setTitle("Select File");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0)
                            {
                                checker="image";
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent.createChooser(intent,"Select Image"),555);

                            }else if(which==1)
                            {
                                checker="pdf";
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                startActivityForResult(intent.createChooser(intent,"Select PDF File"),555);


                            }else if(which==2)
                            {
                                checker="docx";
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/msword");
                                startActivityForResult(intent.createChooser(intent,"Select Ms Word File"),555);
                            }
                        }
                    });

                    builder.show();
                }

        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==555 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fileuri=data.getData();
            dispfileuri.setText(fileuri.toString());


        }
    }
    private void Initialize() {
        sendTitle=findViewById(R.id.noticeTitle);
        sendBody=findViewById(R.id.noticeText);
        Msg=findViewById(R.id.noticesendMsg);

        mPickDateButton = findViewById(R.id.pick_date_button);
        mShowSelectedDateText = findViewById(R.id.show_selected_date);
        selectClass = findViewById(R.id.classes);
        sendFilesBtn= findViewById(R.id.send_files_btn);
        dispfileuri=findViewById(R.id.uridisp);

    }
}