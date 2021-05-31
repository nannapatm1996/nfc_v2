package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.bluetooth.BluetoothClass;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.jetbrains.annotations.NotNull;

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
    private String URL, tagId,globalDeviceId,zone="No Zone",division;
    private DatabaseReference mDatabase;
    private Map<String, Long> seatTest = new HashMap<>();
    private Map<String, String> seatDevice = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seatbooking);

        String fname = getIntent().getStringExtra("fname");
        //String lname = getIntent().getStringExtra("lname");
        division = getIntent().getStringExtra("division");
        tagId = getIntent().getStringExtra("tagid");
        //String zone = getIntent().getStringExtra("zone");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder alertBuilder;

        TextView txName = (TextView) findViewById(R.id.txName);


        Button btnZoneGA = (Button) findViewById(R.id.btnZoneGA);
        Button btnZoneGB = (Button) findViewById(R.id.btnZoneGB);
        Button btnZoneGC = (Button) findViewById(R.id.btnZoneGC);

        Button btnZoneMA = (Button) findViewById(R.id.btnZoneMA);
        Button btnZoneMB = (Button) findViewById(R.id.btnZoneMB);

        Button btnMFloor = (Button) findViewById(R.id.btnMFloor);
        Button btnGFloor = (Button) findViewById(R.id.btnGFloor);

        Button btnCancel = (Button) findViewById(R.id.btnCancelZone);

        ImageView floorplan =(ImageView) findViewById(R.id.imgGFloor);
        ImageView btnBack = (ImageView) findViewById(R.id.btnBackZone);

        btnZoneMA.setVisibility(View.GONE);
        btnZoneMB.setVisibility(View.GONE);

        if(division.equals("DA") || division.equals("ED")){
            zone = "A";
        }
        else{
            zone = "No Zone";
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txName.setText(fname + " " + division);
        alertBuilder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(seatbooking.this);
        if(zone.equals("No Zone")){

        }else {
            new FancyGifDialog.Builder(seatbooking.this)
                    .setTitle("Suggested Zone")
                    .setMessage("Your recommended zone is: " + zone + " Would you like to proceed to that zone?")
                    .setNegativeBtnText("Cancel")
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Ok")
                    .setNegativeBtnBackground("#FFA9A7A8")
                    .setGifResource(R.drawable.walking)//Pass your Gif here
                    .isCancellable(true)
                    .OnPositiveClicked(new FancyGifDialogListener() {
                        @Override
                        public void OnClick() {
                            Toast.makeText(seatbooking.this, "Ok", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(seatbooking.this, seatSelecttion.class);
                            intent.putExtra("zone", "A");
                            intent.putExtra("columnNum", 4);
                            intent.putExtra("tagId", tagId);
                            intent.putExtra("division", division);
                            startActivity(intent);

                        }
                    })
                    .OnNegativeClicked(new FancyGifDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    })
                    .build();
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnMFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorplan.setImageResource(R.drawable.nssb_floorplan_m);
                btnZoneGA.setVisibility(View.GONE);
                btnZoneGB.setVisibility(View.GONE);
                btnZoneGC.setVisibility(View.GONE);

                btnZoneMA.setVisibility(View.VISIBLE);
                btnZoneMB.setVisibility(View.VISIBLE);
            }
        });

        btnGFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorplan.setImageResource(R.drawable.clean_g_floorplan);
                btnZoneGA.setVisibility(View.VISIBLE);
                btnZoneGB.setVisibility(View.VISIBLE);
                btnZoneGC.setVisibility(View.VISIBLE);

                btnZoneMA.setVisibility(View.GONE);
                btnZoneMB.setVisibility(View.GONE);
            }
        });


        btnZoneGA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(seatbooking.this, seatSelecttion.class);
                intent.putExtra("zone", "A");
                intent.putExtra("columnNum", 4);
                intent.putExtra("tagId",tagId);
                intent.putExtra("division",division);
                intent.putExtra("name",fname);
                startActivity(intent);
            }
        });

        btnZoneGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(seatbooking.this, seatSelecttion.class);
                intent.putExtra("zone", "B");
                intent.putExtra("columnNum", 6);
                startActivity(intent);
            }
        });

        btnZoneGC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(seatbooking.this, seatSelecttion.class);
                intent.putExtra("zone", "C");
                intent.putExtra("columnNum", 7);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(seatbooking.this, MainActivity.class);
        tagId = "Please Tap your Ground pass";
        //intent.putExtra("tagId",tagId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
