package com.techelevator.tenmo.model;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {
    private long senderId;
    private long receiverId;

    @Positive
    private BigDecimal amount;

    public Transfer() {}

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
