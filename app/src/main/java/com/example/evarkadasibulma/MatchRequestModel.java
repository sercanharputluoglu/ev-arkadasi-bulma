package com.example.evarkadasibulma;

public class MatchRequestModel {

    private String senderID;
    private String receiverID;
    private String approvalStatus;



    private String senderName;
    private String senderSurname;
    private String senderEmail;
    private String senderPhoneNumber;

    public MatchRequestModel(String senderID, String receiverID, String approvalStatus, String senderName, String senderSurname, String senderEmail, String senderPhoneNumber) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.approvalStatus = approvalStatus;
        this.senderName = senderName;
        this.senderSurname = senderSurname;
        this.senderEmail = senderEmail;
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public MatchRequestModel() {
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderSurname() {
        return senderSurname;
    }

    public void setSenderSurname(String senderSurname) {
        this.senderSurname = senderSurname;
    }
}
