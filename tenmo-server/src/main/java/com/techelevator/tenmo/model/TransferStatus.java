package com.techelevator.tenmo.model;

public enum TransferStatus {
    PENDING(1), APPROVED(2), REJECTED(3);

    private int statusId;

    TransferStatus(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
