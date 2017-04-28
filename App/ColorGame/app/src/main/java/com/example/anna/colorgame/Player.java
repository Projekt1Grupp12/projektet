package com.example.anna.colorgame;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017-04-28.
 */

public class Player implements Serializable {
    private String name;
    private String userID;
    private String choosenIP;

    public Player(String name, String userID, String choosenIP){
        this.name=name;
        this.userID=userID;
        this.choosenIP=choosenIP;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChoosenIP() {
        return choosenIP;
    }

    public void setChoosenIP(String choosenIP) {
        this.choosenIP = choosenIP;
    }
}
