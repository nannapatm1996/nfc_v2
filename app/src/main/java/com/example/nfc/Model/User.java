package com.example.nfc.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    //public String index;
    public String tagId,name,org,division, section, username,eqtrack_id;
    //public String firstname, lastname,name;
    //public String name;
    public String seatName;
    //public String dept, zone;

    public User(){

    }

    public User(String tagId, String name, String org, String division, String section, String username,String seatName,String eqtrack_id){

        this.tagId = tagId;
        this.name = name;
        this.org = org;
        this.division = division;
        this.section = section;
        this.username = username;
        this.seatName = seatName;
        this.eqtrack_id = eqtrack_id;

        //this.imgURL = imgURL;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("tagId",tagId);
        result.put("name", name );
        result.put("org",org);
        result.put("division", division);
        result.put("section",section);
        result.put("username",username);
        result.put("SeatName",seatName);
        result.put("eqtrack_id",eqtrack_id);

        return  result;
    }
}
