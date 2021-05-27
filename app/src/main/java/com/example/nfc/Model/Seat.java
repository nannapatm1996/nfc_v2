package com.example.nfc.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Seat implements Serializable {


    public String seatName, deviceId;
    public Long availability;
    public int Gcolor;
    public boolean isChecked = false;

    public Seat(String seat){
        seatName = seat;
        //Gcolor = color;


    }
    public Seat(){

    }

    public Seat(String seatName, String deviceId, Long availability){
        this.seatName = seatName;
        this.deviceId = deviceId;
        this.availability = availability;
        //this.imgURL = imgURL;

    }

    public String getSeatName(){
        return seatName;
    }
    public int getColor(){
        return Gcolor;
    }

    public void setSeatName(String SeatName){
        this.seatName = SeatName;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public Long getAvailability(){
        return availability;
    }

    public void setAvailability(Long availability){
        this.availability = availability;
    }

    public boolean isChecked(){
        return isChecked;
    }
    public void setChecked(boolean checked){
        isChecked = checked;
    }



    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("SeatName",seatName);
        result.put("deviceId",deviceId);
        result.put("availability", availability);
        return  result;
    }
}
