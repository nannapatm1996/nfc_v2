package com.example.nfc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        enableReaderMode();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void enableReaderMode() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, new NFCCardReader(this), NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }
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
                        if(!task.isSuccessful()){
                            Log.e("Firebase","Error getting data",task.getException());
                        }
                        else {
                            Log.d("firebase",String.valueOf(task.getResult().getValue()));
                            String value = String.valueOf(task.getResult().getValue());
                            String fname = String.valueOf(task.getResult().child("firstname").getValue());
                            String lname = String.valueOf(task.getResult().child("lastname").getValue());
                            if (fname.equals("null")){
                                //mainActivity.displayTagId(tagId,fname);

                                Intent intent = new Intent(MainActivity.this, Register.class);
                                intent.putExtra("tagid",tagId);
                                startActivity(intent);
                            }
                            else{
                                //mainActivity.displayTagId(fname+ " " +lname,fname);
                                Intent intent = new Intent(MainActivity.this, seatbooking.class);
                                intent.putExtra("tagid",tagId);
                                intent.putExtra("fname", fname);
                                intent.putExtra("lname",lname);
                                startActivity(intent);
                            }


                        }
                    }
                });



                /*txtTagId.setText(tagId);
                if (fname.equals("Nannapat")){
                    Image.setImageResource(R.drawable.kermit_the_frog);
                }
                else if (fname.equals("Kijjawat")){
                    Image.setImageResource(R.drawable.batman);
                }
                else if (fname.equals("user not found")){

                    Intent intent = new Intent(MainActivity.this, Register.class);
                    intent.putExtra("tagid",tagId);
                    startActivity(intent);
                }*/


               /* new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {

                        txtTagId.setText("Please Tap your pass");
                        Image.setImageResource(R.drawable.avatar);

                    }
                }.start();*/


                //Image.setImageBitmap(bmp);
            }
        });
    }


}
