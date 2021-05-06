package com.example.nfc.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String index;
    public String tagId;
    public String firstname, lastname;
    public String SeatName;

    public User(){

    }

    public User(String index, String tagId, String firstname, String lastname,String SeatName){
        this.index = index;
        this.tagId = tagId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.SeatName = SeatName;
        //this.imgURL = imgURL;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("index",index);
        result.put("SeatName",SeatName);
        result.put("tagId", tagId);
        result.put("firstname",firstname);
        result.put("lastname",lastname);
        //result.put("deviceId",deviceId);
        //result.put("availability", availability);
        return  result;
    }
}
