package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfc.Adapter.ConnectionHelper;
import com.example.nfc.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String URL;
    //public AlertDialog.Builder builder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder builder;



        enableReaderMode();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void enableReaderMode() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, new NFCCardReader(this), NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }
    }

    private void onSeatSelectSeat(String SeatNo, int availability){
        Map<String, Object> updates = new HashMap<>();
        updates.put("availability",availability);
        mDatabase.child("Seats").child(SeatNo).updateChildren(updates);
    }

    private void SignalPhone(String URL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="https://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                        Log.d("VolleySuccess", "Operation Successful");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                Log.e("VolleyError", error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void onLogout(String tagId,final String seatname){
        mDatabase.child("Seats").child(seatname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("Firebase","Error getting data",task.getException());
                }
                else {
                    String value = String.valueOf(task.getResult().getValue());
                    Log.d("firebase",String.valueOf(task.getResult().getValue()));
                    String deviceId = String.valueOf(task.getResult().child("deviceId").getValue());
                    onSeatSelectSeat(seatname,0);
                    onSeatSelectUser(tagId,"null");
                    Log.d("deviceId",deviceId);
                    URL = "http://10.120.51.11:8080/emapp/EMAppServlet?device="+deviceId+"&doLogout=true";
                    SignalPhone(URL);

                }

            }
        }
    );
}

    private void onSeatSelectUser(String tagId, String SeatNo){
        Map<String, Object> updates = new HashMap<>();
        updates.put("SeatName","null");
        Log.d("SeatChange",SeatNo);
        mDatabase.child("Users").child(tagId).updateChildren(updates);
    }

    public void displayTagId(final String tagId, final String name, final String division, final String org, final String section, final String eqTrack_id, final String username) {
        final TextView txtTagId = (TextView) findViewById(R.id.result);
        final ImageView Image = (ImageView) findViewById(R.id.imageView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Intent intent = new Intent(MainActivity.this, seatbooking.class);
                //intent.putExtra("tagid",tagId);

                //String tagid = txtTagId.getText();
                txtTagId.setText(tagId + name);

                //TODO: change to find in db
                mDatabase.child("Users").child(tagId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            String value = String.valueOf(task.getResult().getValue());
                            String name = String.valueOf(task.getResult().child("name").getValue());
                            String division = String.valueOf(task.getResult().child("division").getValue());
                            String seatname = String.valueOf(task.getResult().child("SeatName").getValue());
                            String tagId = String.valueOf(task.getResult().child("tagId").getValue());
                           // String dept = String.valueOf(task.getResult().child("dept").getValue());
                            //String zone = String.valueOf(task.getResult().child("zone").getValue());
                            Log.d("tagid",tagId);
                            if (tagId.equals("null")) {

                                //seatname = "null";
                                //writeNewTag(tagId,name,org,division,section,username,seatname,eqTrack_id);
                                //mainActivity.displayTagId(tagId,fname);
                                txtTagId.setText("Contact Admin");
                                //Intent intent = new Intent(MainActivity.this, Register.class);
                                //intent.putExtra("tagid", tagId);
                                //startActivity(intent);
                            }
                            else if (seatname.equals("null")) {
                                Log.d("seatloop","seatloop");
                                //mainActivity.displayTagId(fname+ " " +lname,fname);
                                Intent intent = new Intent(MainActivity.this, seatbooking.class);
                                intent.putExtra("tagid", tagId);
                                intent.putExtra("fname", name);

                                startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                String finalSeatname = seatname;
                                builder.setMessage("Your Current Seat is "+seatname+". Would you like to Logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onLogout(tagId, finalSeatname);
                                        txtTagId.setText("Please Tap Your Pass");
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        txtTagId.setText("Please Tap Your Pass");
                                        //Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();


                            }

                        }
                    }

                });
            }
        });
    }

       /*public void writeNewTag(String tagId, String name, String org, String division, String section, String username,String seatName,String eqtrack_id){
       // key = mDatabase.child("tag").push().getKey();
        User user = new User(tagId,name,  org, division, section, username,seatName, eqtrack_id);
        mDatabase.child(tagId).setValue(user);
        Map<String, Object> serialValues = serial.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/tag/" + key,serialValues);
        mDatabase.updateChildren(childUpdates);


    }*/

    public void writeNewTag(String tagId, String name, String org, String division, String section, String username,String seatName,String eqtrack_id){
        // key = mDatabase.child("tag").push().getKey();
        seatName = "null";
        String key = mDatabase.child("Users").push().getKey();
        User user = new User(tagId,name,org,division,section,username,seatName,eqtrack_id);
        Map<String, Object> UserValues = user.toMap();
         mDatabase.child(tagId).setValue(user);

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/"+tagId,UserValues);
        mDatabase.updateChildren(childUpdates);
        // Map<String, Object> serialValues = serial.toMap();



        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/tag/" + key,serialValues);
        //mDatabase.updateChildren(childUpdates);


    }


}



