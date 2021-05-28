package com.example.nfc.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nfc.Model.Seat;
import com.example.nfc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter<Seat>{

    List<Seat> seat_list = new ArrayList<>();
    int custom_layout_id,color, selectedPosition;
    String value,content;
    CheckedTextView textView;
    Seat se ;
    int skyBlue = Color.argb(200,79,223,255);
    private DatabaseReference mDatabase;
    private Map<String, Long> seatFree = new HashMap<>();
    private Map<String, String> seatDevice = new HashMap<>();

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Seat> objects) {
        super(context, resource, objects);
        seat_list = objects;
        custom_layout_id = resource;
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public int getCount() {
        return seat_list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(custom_layout_id, null);
        }

        // initializing the imageview and textview and
        // setting data
        //ImageView imageView = v.findViewById(R.id.imageView);
        textView = v.findViewById(R.id.txSeatNameView);

        // get the item using the  position param
        ReadSeatFirebase();
       //Log.d("global","avail= "+ se.getAvailability());

        Seat seat = seat_list.get(position);
        //Long availability = seat_list.get(position).getAvailability();
        //imageView.setImageResource(seat.getImage_id());
        textView.setText(seat.getSeatName());
        textView.setBackgroundColor(seat.getColor());
        content = textView.getText().toString();

        if (selectedPosition != -1 && seat.availability == 0L)
            if (selectedPosition == position ) {
                //your drawable code
                textView.setBackgroundColor(Color.parseColor("#fcba03"));
                textView.setChecked(true);
                //your other stuff : changing color etc
            }
        else{
            if(content.equals(" ")){
                textView.setBackgroundColor(Color.WHITE);
                textView.setVisibility(View.GONE);
            }
            else if(seat.availability == 0L){
                textView.setBackgroundColor(skyBlue);
                textView.setChecked(false);
                //your other stuff : changing color etc
            }
            else if(seat.availability == 3L){
                textView.setBackgroundColor(Color.LTGRAY);
                textView.setChecked(false);

            }
        }


        return v;

    }

    public void setSelected(int pos){
        selectedPosition = pos;
        notifyDataSetChanged();
    }

    private void ReadSeatFirebase() {
        DatabaseReference mSnap = FirebaseDatabase.getInstance().getReference().child("Seats");
        mDatabase.child("Seats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Seat seat = dataSnapshot.getValue(Seat.class);
                se = dataSnapshot.getValue(Seat.class);
                String key = dataSnapshot.getKey();
                //Map<String, Object> SeatName = (Map<String, Object>) dataSnapshot.child(key).child("SeatName").getValue();

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    Log.d("key", ds.getKey());
                    String SeatName = ds.child("SeatName").getValue(String.class);
                    long avaiability = ds.child("availability").getValue(Long.class);
                    Long a = ds.child("availability").getValue(Long.class);
                    String deviceId = ds.child("deviceId").getValue(String.class);
                    Log.d("finalSeat", SeatName + avaiability);

                    se.setAvailability(ds.child("availability").getValue(Long.class));

                    seatFree.put(SeatName,avaiability);
                    seatDevice.put(SeatName,deviceId);

                    //seat.setAvailability(3L);
                    //Log.d("set avail","avail = "+seat.setAvailability(avaiability);)
                    seat.setSeatName(SeatName);
                    seat.setAvailability(avaiability);
                    //se = seat;
                    //se.availability = seat.getAvailability();
                    Log.d("Hashmap","seatlist ="+se.getAvailability());




                    /*for (Map.Entry<String, Long> entry : seatTest.entrySet()){
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
                    }*/
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






