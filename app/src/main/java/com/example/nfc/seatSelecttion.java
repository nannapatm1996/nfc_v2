package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.example.nfc.Adapter.ConnectionHelper;
import com.example.nfc.Adapter.CustomAdapter;
import com.example.nfc.Model.Seat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class seatSelecttion extends AppCompatActivity {

    GridView gridView;
    TextView GridViewItems, backselecteditem;
    int backposition = -1, state = 0;
    List<Seat> ZoneA = new ArrayList<>();
    List<Seat> ZoneB = new ArrayList<>();
    List<Seat> ZoneC = new ArrayList<>();
    int skyBlue = Color.argb(200, 79, 223, 255), i = 0;
    String seatNo, URL, tagId, adUser;
    private DatabaseReference mDatabase;
    public Map<String, Long> seatFree = new HashMap<>();
    public Map<String, String> seatDevice = new HashMap<>();

    //TextView text = (TextView) findViewById(R.id.txSeatNameView);
    //https://www.youtube.com/watch?v=bff46pNqT8Y
    //https://www.youtube.com/watch?v=K2V6Y7zQ8NU

/*
    static final String[] ZoneC = new String[]{
            "C19", "C20", "C21", " ",
            "C17", "C18", " ", " ",
            "C14", "C15", "C16", " ",
            "C10", "C11", "C12", "C13",
            "C7", "C8", "C9", " ",
            "C3", "C4", "C5", "C6",
            "C1", "C2", " ", " "
    };*/





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selecttion);

        String zone = getIntent().getStringExtra("zone");
        int colNum = getIntent().getIntExtra("columnNum", 4);
        tagId = getIntent().getStringExtra("tagId");
        Button btnConfirm = (Button) findViewById(R.id.btnConfirmSeat);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ReadSeatFirebase(zone);



        for (Map.Entry<String, Long> entry : seatFree.entrySet()) {
            Log.d("hashmapglobal", entry.getKey() + entry.getValue());
            ZoneA.add(new Seat(entry.getKey(), entry.getValue()));

        }

        //Log.d("seatmap","seatmap: "+seatFree.get("G-A1"));
        //Log.d("seatmap","seatmap: "+s.getAvailability());


        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(4);


        if (zone.equals("A")) {
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

        } else if (zone.equals("B")) {
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ZoneB);
            //gridView.setAdapter(adapter);

        } else {
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ZoneC);
            //gridView.setAdapter(adapter);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onLogin(tagId,seatNo);
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


    private void onSeatSelectSeat(String SeatNo, int availability) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("availability", availability);
        mDatabase.child("Seats").child(SeatNo).updateChildren(updates);
    }

    private void onSeatSelectUser(String tagId, String SeatNo) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("SeatName", SeatNo);
        Log.d("SeatChange", SeatNo);
        mDatabase.child("Users").child(tagId).updateChildren(updates);
    }

    public void ReadSeatFirebase(String zone) {
        DatabaseReference mSnap = FirebaseDatabase.getInstance().getReference().child("Seats");
        mDatabase.child("Seats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Seat seat = dataSnapshot.getValue(Seat.class);
                String key = dataSnapshot.getKey();
                Map<String, Long> seatAvailabilty = (Map<String, Long>) dataSnapshot.child("availability").getValue();
                //Map<String, Object> SeatName = (Map<String, Object>) dataSnapshot.child(key).child("SeatName").getValue();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d("key", ds.getKey());

                    String SeatName = ds.child("SeatName").getValue(String.class);
                    long avaiability = ds.child("availability").getValue(Long.class);
                    String deviceId = ds.child("deviceId").getValue(String.class);
                    Log.d("finalSeat", SeatName + avaiability);


                    seatFree.put(SeatName, avaiability);
                    seatDevice.put(SeatName, deviceId);
                    //s.setAvailability(avaiability);

                }
                Log.d("Hashmap", "hash " + seatFree.get("G-A1"));
                //Log.d("class","class"+seat.getAvailability());
                createZone(seatFree,zone);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", error.getMessage()); //Don't ignore potential errors!
            }

        });

    }

    public void createZone(Map<String, Long> seat,String zone) {

        if (zone.equals("A")) {
            ZoneA.add(new Seat("G-A10", seat.get("G-A10")));
            ZoneA.add(new Seat("G-A11", seat.get("G-A11")));
            ZoneA.add(new Seat("G-A12", seat.get("G-A12")));
            ZoneA.add(new Seat("G-A13", seat.get("G-A13")));
            ZoneA.add(new Seat("G-A6", seat.get("G-A6")));
            ZoneA.add(new Seat("G-A7", seat.get("G-A7")));
            ZoneA.add(new Seat("G-A8", seat.get("G-A8")));
            ZoneA.add(new Seat("G-A9", seat.get("G-A9")));
            ZoneA.add(new Seat("G-A1", seat.get("G-A1")));
            ZoneA.add(new Seat("G-A4", seat.get("G-A4")));
            ZoneA.add(new Seat("G-A5", seat.get("G-A5")));
            ZoneA.add(new Seat(" ", 0L));
            ZoneA.add(new Seat(" ", 0L));
            ZoneA.add(new Seat("G-A2", seat.get("G-A2")));
            ZoneA.add(new Seat("G-A3", seat.get("G-A3")));
            ZoneA.add(new Seat(" ", 0L));
            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.custom_view, ZoneA);
            gridView.setAdapter(customAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    customAdapter.setSelected(position);
                    CheckedTextView text = (CheckedTextView) view.findViewById(R.id.txSeatNameView);
                    seatNo = text.getText().toString();

                }
            });

        } else if (zone.equals("B")) {
            ZoneB.add(new Seat(" ", 0L));
            ZoneB.add(new Seat(" ", 0L));
            ZoneB.add(new Seat("G-B18", seat.get("G-B18")));
            ZoneB.add(new Seat("G-B19", seat.get("G-B19")));
            ZoneB.add(new Seat(" ", 0L));
            ZoneB.add(new Seat(" ", 0L));
            ZoneB.add(new Seat("G-B16", seat.get("G-B16")));
            ZoneB.add(new Seat("G-B17", seat.get("G-B17")));
            ZoneB.add(new Seat("G-B12", seat.get("G-B12")));
            ZoneB.add(new Seat("G-B13", seat.get("G-B13")));
            ZoneB.add(new Seat("G-B14", seat.get("G-B14")));
            ZoneB.add(new Seat("G-B15", seat.get("G-B15")));
            ZoneB.add(new Seat(" ", 0L));
            ZoneB.add(new Seat("G-B9", seat.get("G-B9")));
            ZoneB.add(new Seat("G-B10", seat.get("G-B10")));
            ZoneB.add(new Seat("G-B11", seat.get("G-B11")));
            ZoneB.add(new Seat("G-B5", seat.get("G-B5")));
            ZoneB.add(new Seat("G-B6", seat.get("G-B6")));
            ZoneB.add(new Seat("G-B7", seat.get("G-B7")));
            ZoneB.add(new Seat("G-B8", seat.get("G-B8")));
            ZoneB.add(new Seat("G-B1", seat.get("G-B1")));
            ZoneB.add(new Seat("G-B2", seat.get("G-B2")));
            ZoneB.add(new Seat("G-B3", seat.get("G-B3")));
            ZoneB.add(new Seat("G-B4", seat.get("G-B4")));

            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.custom_view, ZoneB);
            gridView.setAdapter(customAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    customAdapter.setSelected(position);
                    CheckedTextView text = (CheckedTextView) view.findViewById(R.id.txSeatNameView);
                    seatNo = text.getText().toString();

                }
            });

        }
        else if(zone.equals("C")){

        }
    }

    public class findUsername extends AsyncTask<String, Void, String> {

        String id, primarypin, User, nameDesc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = tagId;
        }

        //select cas_primarypin_ext.x_id, cas_primarypin_ext.primarypin, cat_validation.name, cat_validation.description
        //from cas_primarypin_ext, cat_validation
        //where cas_primarypin_ext.primarypin = '2218065470' AND cas_primarypin_ext.x_id = cat_validation.id

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String query = "select cas_primarypin_ext.x_id, cas_primarypin_ext.primarypin, cat_validation.name, cat_validation.description from cas_primarypin_ext, cat_validation where cas_primarypin_ext.primarypin =" + "'" +
                        id + "'" + "AND cas_primarypin_ext.x_id = cat_validation.id ";

                //PreparedStatement preparedStatement = connect.prepareStatement(query);

                //preparedStatement.executeQuery();
                Log.d("Query", query);

                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                //Log.d("conn_result",rs.getString("x_id"));
                while (rs.next()) {
                    String re = rs.getString(3);
                    result = re;
                    System.out.println("result " + re);
                }

                //preparedStatement.close();

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getMessage().toString();
            } catch (Exception e) {
                return "Exception. Check DB";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            adUser = result;
            /*if(result.equals("successful connection")){
                Log.d("conn","connection Success");
            }*/
        }
    }

    private void onLogin(String tagId, String seatname) {
        mDatabase.child("Seats").child(seatname).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                } else
                    {
                    String value = String.valueOf(task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String deviceId = String.valueOf(task.getResult().child("deviceId").getValue());
                    onSeatSelectSeat(seatname, 3);
                    onSeatSelectUser(tagId, seatname);
                    Log.d("deviceId", deviceId);
                    findUsername findUsername = new findUsername();
                    findUsername.execute("");
                    //adUser = findUsername.adUser;
                    Log.d("adUser", "aduser is: " + findUsername.doInBackground(adUser));

                    URL = "http://10.120.51.11:8080/emapp/EMAppServlet?device=" + deviceId + "&userid=" + findUsername.doInBackground(adUser) + "&seq=3690";
                    SignalPhone(URL);
                    Intent intent = new Intent(seatSelecttion.this, MainActivity.class);
                    startActivity(intent);

                }

            }
        });


    }
}