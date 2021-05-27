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

    private void onLogout(String tagId,String seatname){
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

    public void displayTagId(final String tagId) {
        final TextView txtTagId = (TextView) findViewById(R.id.result);
        final ImageView Image = (ImageView) findViewById(R.id.imageView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Intent intent = new Intent(MainActivity.this, seatbooking.class);
                //intent.putExtra("tagid",tagId);

                //String tagid = txtTagId.getText();
                txtTagId.setText(tagId);


                mDatabase.child("Users").child(tagId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            String value = String.valueOf(task.getResult().getValue());
                            String fname = String.valueOf(task.getResult().child("firstname").getValue());
                            String lname = String.valueOf(task.getResult().child("lastname").getValue());
                            String seatname = String.valueOf(task.getResult().child("SeatName").getValue());
                            if (fname.equals("null")) {
                                //mainActivity.displayTagId(tagId,fname);

                                Intent intent = new Intent(MainActivity.this, Register.class);
                                intent.putExtra("tagid", tagId);
                                startActivity(intent);
                            } else if (seatname.equals("null")) {
                                //mainActivity.displayTagId(fname+ " " +lname,fname);
                                Intent intent = new Intent(MainActivity.this, seatbooking.class);
                                intent.putExtra("tagid", tagId);
                                intent.putExtra("fname", fname);
                                intent.putExtra("lname", lname);
                                startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
                                builder.setMessage("Your Current Seat is "+seatname+". Would you like to Logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onLogout(tagId,seatname);
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


}



