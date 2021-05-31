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
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

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

                            Log.d("tagid",tagId);
                            if (tagId.equals("null")) {

                                txtTagId.setText("Contact Admin");

                            }
                            else if (seatname.equals("null")) {
                                Log.d("seatloop","seatloop");
                                //mainActivity.displayTagId(fname+ " " +lname,fname);
                                Intent intent = new Intent(MainActivity.this, seatbooking.class);
                                intent.putExtra("tagid", tagId);
                                intent.putExtra("fname", name);
                                intent.putExtra("division",division);
                                startActivity(intent);
                            }
                            else {
                                String finalSeatname = seatname;
                                new FancyGifDialog.Builder(MainActivity.this)
                                        .setTitle("Logout")
                                        .setMessage("Your Current Seat is "+seatname+". Would you like to Logout?")
                                        .setNegativeBtnText("Cancel")
                                        .setPositiveBtnBackground("#FF4081")
                                        .setPositiveBtnText("Ok")
                                        .setNegativeBtnBackground("#FFA9A7A8")
                                        .setGifResource(R.drawable.logout_gif)//Pass your Gif here
                                        .isCancellable(true)
                                        .OnPositiveClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                                                onLogout(tagId, finalSeatname);
                                                txtTagId.setText("Please Tap Your Pass");
                                            }
                                        })
                                        .OnNegativeClicked(new FancyGifDialogListener() {
                                            @Override
                                            public void OnClick() {
                                                Toast.makeText(MainActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                                                txtTagId.setText("Please Tap Your Pass");
                                            }
                                        })
                                        .build();


                            }

                        }
                    }

                });
            }
        });
    }
    boolean backpress = false;
    @Override
    public void onBackPressed() {
        if(backpress) {
            super.onBackPressed();
            return;
        }

        this.backpress = true;
        Toast.makeText(this, "Press BACK again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backpress = true;
                //finish();

            }
        },2000);

    }
}



