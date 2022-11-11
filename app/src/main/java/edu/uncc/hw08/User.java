package edu.uncc.hw08;

import java.io.Serializable;

public class User implements Serializable {
    public String userName, userID;
    public boolean status;

    public User(){

    }

    public User(String userName, String userID, boolean status) {
        this.userName = userName;
        this.userID = userID;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public boolean getStatus() {
        return status;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
