package edu.uncc.hw08;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Roomchat implements Serializable {

    public ArrayList<String> userIds = new ArrayList<>();
    public ArrayList<String> userNames = new ArrayList<>();
    public String roomId;
    public Date time;
    Message message;

    public Roomchat(){

    }

    public Roomchat(ArrayList<String> userIds, ArrayList<String> userNames, String roomId, Date time, Message message) {
        this.userIds = userIds;
        this.userNames = userNames;
        this.roomId = roomId;
        this.time = time;
        this.message = message;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
