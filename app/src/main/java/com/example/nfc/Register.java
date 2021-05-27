package com.example.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nfc.Model.Seat;
import com.example.nfc.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private String firstname, lastname, index,tagid,dept, zone;
    private EditText et_lastname, et_firstname, et_index;
    private TextView et_tagid;
    private DatabaseReference mDatabase;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            tagid = (String) extras.get("tagid");
        }

        et_tagid = findViewById(R.id.et_tagid);
        et_firstname = findViewById(R.id.et_Firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_index = findViewById(R.id.et_index);
        submit = findViewById(R.id.btnSubmit);
        et_tagid.setText(tagid);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = et_firstname.getText().toString();
                lastname = et_lastname.getText().toString();
                index = et_index.getText().toString();
                String Seat = "null";
                writeNewTag(index,tagid,firstname,lastname,Seat,dept,zone);
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });





    }

    public void writeNewTag(String index, String tagId,String fName, String LName,String Seat,String dept, String zone){
        // key = mDatabase.child("tag").push().getKey();
        String key = mDatabase.child("Users").push().getKey();
        User user = new User(index,tagId,fName,LName, Seat,dept,zone);
        Map<String, Object> UserValues = user.toMap();
       // mDatabase.child(tagId).setValue(user);

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/"+tagid,UserValues);
        mDatabase.updateChildren(childUpdates);
        // Map<String, Object> serialValues = serial.toMap();



        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/tag/" + key,serialValues);
        //mDatabase.updateChildren(childUpdates);


    }

    public void writeNewSeat(String SeatName, String deviceId, Long availability) {
        // key = mDatabase.child("tag").push().getKey();
        //User user = new User(index, tagId, fName, LName, Seat);

        //mDatabase.child("Seat").push().setValue(seat);
        String key = mDatabase.child("Seats").push().getKey();
        Seat seat = new Seat(SeatName, deviceId, availability);
        Map<String, Object> SeatValues = seat.toMap();

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/Seats/",SeatValues);
        mDatabase.updateChildren(childUpdates);

        //Map<String, Object> userUpdates = new HashMap<>();
        //userUpdates.put(tagId, Seat);
        //mDatabase.updateChildren(userUpdates);

        // Map<String, Object> serialValues = serial.toMap();
    }
}