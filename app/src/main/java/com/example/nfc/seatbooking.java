package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.bluetooth.BluetoothClass;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfc.Model.Seat;
import com.example.nfc.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class seatbooking extends AppCompatActivity {

    private TextView txName;
    private ImageView img1, img2, img3, img4;
    private TextView DeviceId;
    private Long choose1 = 0L, choose2 = 0L, choose3 = 0L, choose4 = 0L;
    private String URL, tagId;
    private DatabaseReference mDatabase;
    private Map<String, Long> seatTest = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seatbooking);

        String fname = getIntent().getStringExtra("fname");
        String lname = getIntent().getStringExtra("lname");
        tagId = getIntent().getStringExtra("tagid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder alertBuilder;

        TextView txName = (TextView) findViewById(R.id.txName);
        img1 = (ImageView) findViewById(R.id.img1);
        img2=  (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        Button btnconfirmSeat = (Button) findViewById(R.id.btnSubmitSeat);

        txName.setText(fname + " " + lname);
        alertBuilder = new AlertDialog.Builder(this);

        //Set Up seat 1 time
        //writeNewSeat("1A", "SEP64AE0CF72FC7", 0);
        //writeNewSeat("2A", "SEP64AE0CF72FC7", 0);
        ReadSeatFirebase();



        img1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (choose1 == 0L) {
                    img1.setImageResource(R.drawable.kermit_the_frog);
                    choose1 = 1L;
                } else {
                    img1.setImageResource(R.drawable.avatar);
                    choose1 = 0L;
                }

            }
        });


        img2.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (choose2 == 0L) {
                    img2.setImageResource(R.drawable.kermit_the_frog);
                    choose2 = 1L;
                }
                else {
                    img2.setImageResource(R.drawable.avatar);
                    choose2 = 0L;
                }

            }
        });

        btnconfirmSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = "http://10.120.51.11/emapp/EMAppServlet?device=" + DeviceId + "&userid=nannapatm&seq=3690";

                //TODO: send to server, set img with choose value = 1 to disable

                if (choose1 == 1L && choose2 == 1L) {
                    alertBuilder.setMessage("You can only Choose 1 Seat").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {

                    if (choose1 == 1L) {

                        String DeviceId = "SEP00279080B309"; //Fetch from db
                        String SeatNo = "1A";


                        alertBuilder.setMessage(fname + " " + lname + "Your seat is 1A").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //img1.setEnabled(false);
                                onSeatSelectSeat(SeatNo, 3);
                                onSeatSelectUser(tagId, SeatNo);
                                Log.d("Seat",tagId+SeatNo);
                                //ReadSeatFirebase();
                                URL = "http://10.120.51.11:8080/emapp/EMAppServlet?device=" + DeviceId + "&userid=nannapatm&seq=3690";
                                SignalPhone(URL);
                                Intent intent = new Intent(seatbooking.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else if (choose2 == 1) {
                        String DeviceId = "SEP64AE0CF72FC7"; //Fetch from db
                        String SeatNo = "2A";
                        alertBuilder.setMessage(fname + " " + lname + " Your seat is 2A").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                img2.setEnabled(false);
                                //TODO: Write to Firebase Tag Seat A1, availability: 0/1
                                URL = "http://10.120.51.11:8080/emapp/EMAppServlet?device=SEP00279080B309&userid=nannapatm&seq=3690";
                                SignalPhone(URL);
                                //Todo: Write to Firebase
                                onSeatSelectSeat(SeatNo, 3);
                                onSeatSelectUser(tagId, SeatNo);
                                Intent intent = new Intent(seatbooking.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        alertBuilder.setMessage("Please Choose a seat").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }


            }
        });

    }

    //Todo: send to phone
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

    public void writeNewSeat(String SeatName, String deviceId, int availability) {
        // key = mDatabase.child("tag").push().getKey();
        //User user = new User(index, tagId, fName, LName, Seat);

        //mDatabase.child("Seat").push().setValue(seat);
        String key = mDatabase.child("Seats").push().getKey();
        Seat seat = new Seat(SeatName, deviceId, availability);
        Map<String, Object> SeatValues = seat.toMap();

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/Seats/"+SeatName,SeatValues);
        mDatabase.updateChildren(childUpdates);

        //Map<String, Object> userUpdates = new HashMap<>();
        //userUpdates.put(tagId, Seat);
        //mDatabase.updateChildren(userUpdates);

        // Map<String, Object> serialValues = serial.toMap();
    }

    private void onSeatSelectUser(String tagId, String SeatNo){
        Map<String, Object> updates = new HashMap<>();
        updates.put("SeatName",SeatNo);
        Log.d("SeatChange",SeatNo);
        mDatabase.child("Users").child(tagId).updateChildren(updates);
    }

    public void writeNewTag(String index, String tagId,String fName, String LName,String Seat) {
        // key = mDatabase.child("tag").push().getKey();
        User user = new User(index, tagId, fName, LName, Seat);
        mDatabase.child(tagId).setValue(user);
        // Map<String, Object> serialValues = serial.toMap();

    }
        private void onSeatSelectSeat(String SeatNo, int availability){
        Map<String, Object> updates = new HashMap<>();
        updates.put("availability",availability);
        mDatabase.child("Seats").child(SeatNo).updateChildren(updates);
    }

    private void ReadSeatFirebase() {
        //Read data from Firebase
        //DatabaseReference SeatRef = FirebaseDatabase.getInstance().getReference();
        /*ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Seat seat = snapshot.getValue(Seat.class);

                    Log.d("SeatChild", seat.getSeatName()+" "+seat.getAvailability());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };*/
        DatabaseReference mSnap = FirebaseDatabase.getInstance().getReference().child("Seats");
        mDatabase.child("Seats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Seat seat = dataSnapshot.getValue(Seat.class);
                String key = dataSnapshot.getKey();
                //Map<String, Object> SeatName = (Map<String, Object>) dataSnapshot.child(key).child("SeatName").getValue();

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    Log.d("key", ds.getKey());
                    String SeatName = ds.child("SeatName").getValue(String.class);
                    long avaiability = ds.child("availability").getValue(Long.class);
                    Log.d("finalSeat", SeatName + avaiability);
                    seatTest.put(SeatName,avaiability);
                    Log.d("Hashmap","hash "+seatTest);
                    for (Map.Entry<String, Long> entry : seatTest.entrySet()){
                        Log.d("hashmapfor", entry.getKey()+ entry.getValue());
                        if(entry.getKey().equals("1A")){
                            choose1 = entry.getValue();
                            Log.d("choose1","Choose1: "+choose1);
                            if(choose1==3L){
                                img1.setImageResource(R.drawable.kermit_the_frog);
                                img1.setEnabled(false);
                            }
                        }
                        else{
                            choose2 = entry.getValue();
                            Log.d("choose2","Choose2: "+choose2);
                            if(choose2==3L){
                                img2.setImageResource(R.drawable.kermit_the_frog);
                                img2.setEnabled(false);
                            }

                        }
                    }
                   // seat.getAvailability()
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", error.getMessage()); //Don't ignore potential errors!
            }
        });
    }



    }
