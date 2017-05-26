package com.example.anna.colorgame;

import java.io.Serializable;

/**
 * This class stores data of an Player object. Player is a user who is logged into the application.
 * Player has name, userID, and choosenIP variables.
 */
public class Player implements Serializable {
    private String name;
    private String userID;
    private String choosenIP;

    /**
     * Constructor that creates a Player instance and initiates it with specified values.
     *
     * @param name      String
     * @param userID    String
     * @param choosenIP String
     */
    public Player(String name, String userID, String choosenIP) {
        this.name = name;
        this.userID = userID;
        this.choosenIP = choosenIP;
    }

    /**
     * This method is used to get the name of the Player.
     *
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * This method is used to set name of the Player to specified value.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method is used to get userID of the Player.
     *
     * @return userID String
     */
    public String getUserID() {
        return userID;
    }

    /**
     * This method is used to set userID of the Player to specified value.
     *
     * @param userID String
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * This method is used to get choosenIP of the Player.
     *
     * @return choosenIP String
     */
    public String getChoosenIP() {
        return choosenIP;
    }

    /**
     * This method is used to set choosenIP of the Player to specified vale.
     *
     * @param choosenIP String
     */
    public void setChoosenIP(String choosenIP) {
        this.choosenIP = choosenIP;
    }
}
