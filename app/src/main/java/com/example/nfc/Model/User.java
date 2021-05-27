package com.example.nfc.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String index;
    public String tagId;
    public String firstname, lastname;
    public String seatName;
    public String dept, zone;

    public User(){

    }

    public User(String index, String tagId, String firstname, String lastname,String seatName, String dept, String zone){
        this.index = index;
        this.tagId = tagId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.seatName = seatName;
        this.dept = dept;
        this.zone = zone;

        //this.imgURL = imgURL;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("index",index);
        result.put("SeatName",seatName);
        result.put("tagId", tagId);
        result.put("firstname",firstname);
        result.put("lastname",lastname);
        result.put("dept",dept);
        result.put("zone",zone);
        //result.put("deviceId",deviceId);
        //result.put("availability", availability);
        return  result;
    }
}
