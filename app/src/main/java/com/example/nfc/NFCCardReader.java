package com.example.nfc;

import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.nfc.Model.Seat;
import com.example.nfc.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NFCCardReader implements NfcAdapter.ReaderCallback {
    private MainActivity mainActivity;
    private DatabaseReference mDatabase;
    private String tagId;
    private String index;
    private Bitmap bmp;
    //private Serial serial;
    public NFCCardReader(MainActivity mainActivity) {
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfccardreader);*/
        this.mainActivity = mainActivity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
       // mainActivity.displayTagId(tagId);



    }

    @Override
    //Display on Current page
    public void onTagDiscovered(Tag tag) {
        tagId = bytesToHexString(tag.getId());
        mainActivity.displayTagId(tagId);


        /*if (tagId.equals("0xc40ff93e")){
            mainActivity.displayTagId("Kijjawat Poopong");
        }
        else {
            mainActivity.displayTagId("Unknown");
        }*/
        //final String tagId = getTagId();
        //mainActivity.displayTagId(tagId);
        //index = "10110067";
        //TODO: 1. Flash Tag 2.Query data in FB 3. If found --> Fetch at Tag<User>, if not Found --> Check in Index

        //writeNewTag("10110067",tagId,"Nannapat","Meemongkolkiat");
        //writeNewSeat("1A", "SEP64AE0CF72FC7", 0);
        //writeNewSeat("2A", "SEP64AE0CF72FC7", 0);
        //Read by using tagId


    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

   /* public void writeNewTag(String index, String tagId,String fName, String LName){
       // key = mDatabase.child("tag").push().getKey();
        User user = new User(index,tagId,fName,LName);
        mDatabase.child(tagId).setValue(user);
       // Map<String, Object> serialValues = serial.toMap();


        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/tag/" + key,serialValues);
        //mDatabase.updateChildren(childUpdates);


    }*/

   /* private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Serial tag = new Serial();
            //User user = new User();
            tag.setIndex();
            alarm.setName(ds.child(userID).getValue(Alarm.class).getName()); //set the name
            alarm.setFormat(ds.child(userID).getValue(Alarm.class).getFormat());
            alarm.setAddReminder(ds.child(userID).getValue(Alarm.class).getAddReminder());
//            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail()); //set the email
//            uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num()); //set the phone_num

            //display all the information
          //*  Log.d(TAG, "showData: name: " + alarm.getName());
            Log.d(TAG, "showData: format: " + alarm.getFormat());
            Log.d(TAG, "showData: Reminder "+ alarm.getAddReminder());
//            Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

        }
    }*/

    public void writeNewSeat(String SeatName, String deviceId, int availability) {
        // key = mDatabase.child("tag").push().getKey();
        //User user = new User(index, tagId, fName, LName, Seat);

        //mDatabase.child("Seat").push().setValue(seat);
        String key = mDatabase.child("Seats").push().getKey();
        Seat seat = new Seat(SeatName, deviceId, availability);
        Map<String, Object> SeatValues = seat.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Seats/" + key, SeatValues);
        mDatabase.updateChildren(childUpdates);
    }






}
