package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDTO {

    private Long transferId;

    private UserDTO sender;

    private UserDTO receiver;

    private TransferStatus status;

    private TransferType type;

    private BigDecimal amount;

    public TransferDTO() {}

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
