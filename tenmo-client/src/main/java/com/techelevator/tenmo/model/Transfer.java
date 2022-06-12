package com.techelevator.tenmo.model;


import java.math.BigDecimal;

public class Transfer {

    private long transferId;
//    private long senderId;
//    private long receiverId;

    private User sender;
    private User receiver;

    private TransferStatus status;
    private TransferType type;

    private BigDecimal amount;

//    public Transfer(long senderId, long receiverId, TransferType type, TransferStatus status, BigDecimal amount) {
//        this.senderId = senderId;
//        this.receiverId = receiverId;
//        this.type = type;
//        this.amount = amount;
//        this.status = status;
//    }

    public Transfer(User sender, User receiver, TransferType type, TransferStatus status, BigDecimal amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.status = status;
        this.amount = amount;
    }

    public Transfer(){}

//    public long getSenderId() {
//        return senderId;
//    }
//
//    public void setSenderId(long senderId) {
//        this.senderId = senderId;
//    }
//
//    public long getReceiverId() {
//        return receiverId;
//    }
//
//    public void setReceiverId(long receiverId) {
//        this.receiverId = receiverId;
//    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public TransferType getType() {
        return type;
    }

    public void setType(TransferType type) {
        this.type = type;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}