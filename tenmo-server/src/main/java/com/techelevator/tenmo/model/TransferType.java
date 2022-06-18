package com.techelevator.tenmo.model;

public enum TransferType {
    REQUEST(1), SEND(2);

    int typeId;

    TransferType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
