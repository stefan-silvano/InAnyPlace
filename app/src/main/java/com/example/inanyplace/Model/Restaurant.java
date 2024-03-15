package com.example.inanyplace.Model;

public class Restaurant {
    private String uid;
    private String name;
    private String address;
    private String imageUrl;
    private String phone;
    private int deliveryTimeMin;
    private int deliveryTimeMax;

    public Restaurant() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDeliveryTimeMin() {
        return deliveryTimeMin;
    }

    public void setDeliveryTimeMin(int deliveryTimeMin) {
        this.deliveryTimeMin = deliveryTimeMin;
    }

    public int getDeliveryTimeMax() {
        return deliveryTimeMax;
    }

    public void setDeliveryTimeMax(int deliveryTimeMax) {
        this.deliveryTimeMax = deliveryTimeMax;
    }
}
