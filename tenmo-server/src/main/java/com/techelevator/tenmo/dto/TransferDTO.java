package com.techelevator.tenmo.dto;

import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;

public class TransferDTO {

    private Long transferId;

    private User sender;

    private User receiver;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferDTO transferDTO = (TransferDTO) o;

        System.out.println(type + " " + transferDTO.getType());

        return transferId.equals(transferDTO.getTransferId()) &&
                sender.equals(transferDTO.getSender()) &&
                receiver.equals(transferDTO.getReceiver()) &&
                status.equals(transferDTO.getStatus()) &&
                type.equals(transferDTO.getType()) &&
                amount.equals(transferDTO.getAmount());
    }
}
