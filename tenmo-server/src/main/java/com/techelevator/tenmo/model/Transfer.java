package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {

    private long transferId;
//    private long senderId;
//    private long receiverId;

    private User sender;
    private User receiver;
    private TransferStatus status;
    private TransferType type;
    @Positive
    private BigDecimal amount;

    public Transfer() {}

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
