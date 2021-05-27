package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nfc.Adapter.CustomAdapter;
import com.example.nfc.Model.Seat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class seatSelecttion extends AppCompatActivity {

    GridView gridView;
    TextView GridViewItems, backselecteditem;
    int backposition = -1,state=0;
    List<Seat> ZoneA = new ArrayList<>();
    int skyBlue = Color.argb(200,79,223,255);
    String seatNo, URL, tagId;
    private DatabaseReference mDatabase;

    //TextView text = (TextView) findViewById(R.id.txSeatNameView);
    //https://www.youtube.com/watch?v=bff46pNqT8Y
    //https://www.youtube.com/watch?v=K2V6Y7zQ8NU



    static final String[] ZoneB = new String[]{
            " ", " ", "B16", "B17",
            " ", " ", "B14", "B15",
            "B10", "B11", "B12", "B13",
            " ", "B9", "B10", "B11",
            "B5", "B6", "B7", "B8",
            "B1", "B2", "B3","B4"
    };

    static final String[] ZoneC = new String[]{
            "C19", "C20", "C21", " ",
            "C17", "C18", " ", " ",
            "C14", "C15", "C16", " ",
            "C10", "C11", "C12", "C13",
            "C7", "C8", "C9", " ",
            "C3", "C4", "C5", "C6",
            "C1", "C2", " "," "
    };

/*
    static final String[] ZoneA = new String[]{
            "A10", "A11", "A12", "A13",
            "A6", "A7", "A8", "A9",
            "A1", "A4", "A5", " ",
            "A1", "A2", "A3"," "
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selecttion);

        String zone = getIntent().getStringExtra("zone");
        int colNum = getIntent().getIntExtra("columnNum",4);
        tagId = getIntent().getStringExtra("tagId");
        Button btnConfirm = (Button) findViewById(R.id.btnConfirmSeat);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ZoneA.add(new Seat("G-A10"));
        ZoneA.add(new Seat("G-A11"));
        ZoneA.add(new Seat("G-A12"));
        ZoneA.add(new Seat("G-A13"));
        ZoneA.add(new Seat("G-A6"));
        ZoneA.add(new Seat("G-A7"));
        ZoneA.add(new Seat("G-A8"));
        ZoneA.add(new Seat("G-A9"));
        ZoneA.add(new Seat("G-A1"));
        ZoneA.add(new Seat("G-A4"));
        ZoneA.add(new Seat("G-A5"));
        ZoneA.add(new Seat(" "));
        ZoneA.add(new Seat(" "));
        ZoneA.add(new Seat("G-A2"));
        ZoneA.add(new Seat("G-A3"));
        ZoneA.add(new Seat(" "));

        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(4);

        if(zone.equals("A")){
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,ZoneA);

            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.custom_view, ZoneA);
            gridView.setAdapter(customAdapter);
            //gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
            //gridView.setChoiceMode(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    customAdapter.setSelected(position);
                    CheckedTextView text = (CheckedTextView) view.findViewById(R.id.txSeatNameView);
                    seatNo = text.getText().toString();

                }
            });

                /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckedTextView text = (CheckedTextView) view.findViewById(R.id.txSeatNameView);

                        //String selected_item = parent.getItemAtPosition(position).toString();
                        //Log.d("select",selected_item);
                        //.setBackgroundColor(Color.GREEN);

                        Log.d("get","getis: "+text.getText());

                        String content = text.getText().toString();

                        if(state==0){
                            if(content.equals(" ")){

                            }
                            else{
                                text.setBackgroundColor(Color.argb(255,65,186,77));
                                state=1;
                                seatNo = content;


                            }
                        }
                        else if(state==1){
                            text.setBackgroundColor(Color.argb(200,79,223,255));
                            state=0;

                        }
                        else{

                        }


                    }
                });*/


            //gridView.getItemAtPosition(10);
            //Log.d("testgetpo","Postion:"+ gridView.getItemAtPosition(10));


          /*  for(int val=0; val<adapter.getPosition(" ");val++){
                Object obj = adapter.getItem(val);
                CardView cardView = (CardView) gridView.getChildAt(val);  //to match with context
                Log.d("CheckFor",obj.toString());

                if (obj.equals(" ")){
                    adapter.getPosition(" ");
                    gridView.getPositionForView(gridView);
                   ///adapter.getView(3,gridView,gridView);
                    gridView.setBackgroundColor(Color.RED);
                }
                else{

                }
            }*/


        }
        else if(zone.equals("B")){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,ZoneB);
            gridView.setAdapter(adapter);

        }
        else{
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,ZoneC);
            gridView.setAdapter(adapter);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = "http://10.120.51.11/emapp/EMAppServlet?device=SEP64AE0CF72FC7&userid=nannapatm&seq=3690";
                Log.d("Seat",tagId+seatNo);
                onSeatSelectSeat(seatNo, 3);
                onSeatSelectUser(tagId, seatNo);

                //ReadSeatFirebase();
                //URL = "http://10.120.51.11:8080/emapp/EMAppServlet?device=" + DeviceId + "&userid=nannapatm&seq=3690";
                SignalPhone(URL);
                Intent intent = new Intent(seatSelecttion.this, MainActivity.class);
                startActivity(intent);
            }
        });




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

    private void onSeatSelectSeat(String SeatNo, int availability){
        Map<String, Object> updates = new HashMap<>();
        updates.put("availability",availability);
        mDatabase.child("Seats").child(SeatNo).updateChildren(updates);
    }

    private void onSeatSelectUser(String tagId, String SeatNo){
        Map<String, Object> updates = new HashMap<>();
        updates.put("SeatName",SeatNo);
        Log.d("SeatChange",SeatNo);
        mDatabase.child("Users").child(tagId).updateChildren(updates);
    }

    /*private void ReadSeatFirebase() {
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
                    String deviceId = ds.child("deviceId").getValue(String.class);
                    Log.d("finalSeat", SeatName + avaiability);
                    seatTest.put(SeatName,avaiability);
                    seatDevice.put(SeatName,deviceId);
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
    }*/





}