package com.example.evandro.delliveryclient;

/**
 * Created by Evandro on 6/27/2016.
 */
public class Delivery {
    int id;
    String address;
    String description;
    String status;
    byte[] image;
    int deliverymanId;

    public Delivery(){

    }

    public Delivery(int id, String address){
        super();
        this.id = id;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getDeliverymanId() {
        return deliverymanId;
    }

    public void setDeliverymanId(int deliverymanId) {
        this.deliverymanId = deliverymanId;
    }
}
