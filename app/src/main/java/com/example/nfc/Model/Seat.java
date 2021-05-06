package com.example.nfc.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Seat {


    public String seatName, deviceId;
    public int availability;

    public Seat(){

    }

    public Seat(String seatName, String deviceId, int availability){
        this.seatName = seatName;
        this.deviceId = deviceId;
        this.availability = availability;
        //this.imgURL = imgURL;

    }

    public String getSeatName(){
        return seatName;
    }

    public void setSeatName(String SeatName){
        this.seatName = SeatName;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public int getAvailability(){
        return availability;
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
