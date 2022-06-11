package com.techelevator.tenmo.model;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {
    private long senderId;
    private long receiverId;

    @Positive(message = "{balance.mustPositive}")
    private BigDecimal amount;

    private TransferStatus status;
    private TransferType type;

    public Transfer() {
        status = TransferStatus.PENDING;
    }

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
}
