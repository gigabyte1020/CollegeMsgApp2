package com.example.whtsapp1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String currentUserID,retrieveuClass="";
    private View groupView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of= new ArrayList<>();
    private DatabaseReference GroupRef;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserImgRef;
    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupView = inflater.inflate(R.layout.fragment_groups, container, false);
        RootRef= FirebaseDatabase.getInstance().getReference();

        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups1");
        mAuth=FirebaseAuth.getInstance();
       UserImgRef = FirebaseStorage.getInstance().getReference().child("ProfImg");
        currentUserID=mAuth.getCurrentUser().getUid();
        Initialize();
        RetrieveUserInfo();


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String currentGroupName = adapterView.getItemAtPosition(pos).toString();
                Intent groupIntent = new Intent(getContext(),GroupChatActivity.class);
                groupIntent.putExtra("groupname",currentGroupName);
                startActivity(groupIntent);
            }
        });
        return groupView;
        

    }private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists())) {
                    retrieveuClass = snapshot.child("class").getValue().toString();
                    RetrieveGroups();
                }
                else{
                    retrieveuClass ="AAA";
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
 //Reads and gets groups from database
    private void RetrieveGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while(iterator.hasNext())
                {
                    String gr1 =  ((DataSnapshot)iterator.next()).getKey();
//                    Log.d("Status",retrieveuClass);
                  //  Log.d("GroupName",gr1);
                    if (gr1.equals(retrieveuClass)) {
                        set.add(gr1); //Gets names
                    }
                    else{
                    }

                }
                list_of.clear();
                list_of.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Initialize() {
        list_view=(ListView) groupView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list_of);
        list_view.setAdapter(arrayAdapter);
    }
}