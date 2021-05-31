package com.example.nfc;

import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.nfc.Adapter.ConnectionHelper;
import com.example.nfc.Model.Seat;
import com.example.nfc.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NFCCardReader implements NfcAdapter.ReaderCallback {
    private MainActivity mainActivity;
    private DatabaseReference mDatabase;
    private String tagId;
    private String index;
    private Bitmap bmp;
    private String eqtrack_id, org, division=null, section, username, name=" ";
    //private Serial serial;
    public NFCCardReader(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
        mDatabase = FirebaseDatabase.getInstance().getReference();



    }

    @Override
    //Display on Current page
    public void onTagDiscovered(Tag tag) {
        tagId = bytesToHexString(tag.getId());

        mainActivity.displayTagId(tagId,name,division,org, section,eqtrack_id,username);
        findUsername findUsername = new findUsername();
        findUsername.execute("");

    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        long dec =0L;
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
            dec = Long.parseLong(stringBuilder.toString(),16);
            }

        return String.valueOf(dec);
    }

    public class findUsername extends AsyncTask<String, Void, String> {

        String id;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            id =tagId;
        }



        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try{
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();


                String query = "select cas_primarypin_ext.primarypin, cat_validation.id,View_UserProfile.ORG, View_UserProfile.DIVISION,View_UserProfile.SECTION, cat_validation.name, cat_validation.description \n" +
                        "from cas_primarypin_ext, cat_validation, View_UserProfile "+
                        "where cas_primarypin_ext.primarypin = '"+ id +"' AND cas_primarypin_ext.x_id = cat_validation.id AND cat_validation.name = View_UserProfile.USERID";

                Log.d("Query",query);

                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                //Log.d("conn_result",rs.getString("x_id"));
                while(rs.next()){
                    String result_eqid = rs.getString(2);
                    String result_name = rs.getString(7);
                    String result_org = rs.getString(3);
                    String result_division = rs.getString(4);
                    String result_section = rs.getString(5);
                    String result_username = rs.getString(6);
                    name =result_name;
                    division = result_division;
                    org = result_org;
                    section = result_section;
                    username = result_username;
                    eqtrack_id = result_eqid;
                    System.out.println(result_division +  division);


                }



                return "successful connection";
            }
            catch (SQLException e){
                e.printStackTrace();
                return e.getMessage().toString();
            }catch (Exception e){
                return "Exception. Check DB";
            }

        }
        @Override
        protected  void onPostExecute(String result){
            //name = re_name;
            if(result.equals("successful connection")){
                Log.d("conn","connection Success");
                if(name.equals(" ")){

                }
                else{
                    String seatname = "null";
                    writeNewTag(tagId,name,org,division,section,username,seatname,eqtrack_id);
                }
            }
        }
    }

    public void writeNewTag(String tagId, String name, String org, String division, String section, String username,String seatName,String eqtrack_id){
        // key = mDatabase.child("tag").push().getKey();
        //String seatname = "null";
        String key = mDatabase.child("Users").push().getKey();
        User user = new User(tagId,name,org,division,section,username,seatName,eqtrack_id);
        Map<String, Object> UserValues = user.toMap();
         mDatabase.child("/Users/"+tagId).setValue(user);

        Map<String,Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/"+tagId,UserValues);
        mDatabase.updateChildren(childUpdates);


    }


}
