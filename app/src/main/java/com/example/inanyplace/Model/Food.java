package com.example.inanyplace.Model;

import java.util.List;

public class Food {

    private String id;
    private String name;
    private String image;
    private String description;
    private Long price;
    private List<Addon> addon;
    private List<Size> size;
    private String key;
    private double ratingValue;
    private long ratingCount;

    //For cart
    private List<Addon> userSelectedAddon;
    private Size userSelectedSize;

    public Food() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<Addon> getAddon() {
        return addon;
    }

    public void setAddon(List<Addon> addon) {
        this.addon = addon;
    }

    public List<Size> getSize() {
        return size;
    }

    public void setSize(List<Size> size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<Addon> getUserSelectedAddon() {
        return userSelectedAddon;
    }

    public void setUserSelectedAddon(List<Addon> userSelectedAddon) {
        this.userSelectedAddon = userSelectedAddon;
    }

    public Size getUserSelectedSize() {
        return userSelectedSize;
    }

    public void setUserSelectedSize(Size userSelectedSize) {
        this.userSelectedSize = userSelectedSize;
    }
}
