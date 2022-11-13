package edu.uncc.hw08;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

public class Message implements Serializable {
    String creator, creatorID, message, messageID, date, receiver, receiverID;
    public Date createdAt;
    public Message(){
    }

    public Message(String creator, String creatorID, String message, String messageID, String date, String receiver, String receiverID, Date createdAt) {
        this.creator = creator;
        this.creatorID = creatorID;
        this.message = message;
        this.messageID = messageID;
        this.date = date;
        this.receiver = receiver;
        this.receiverID = receiverID;
        this.createdAt = createdAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
