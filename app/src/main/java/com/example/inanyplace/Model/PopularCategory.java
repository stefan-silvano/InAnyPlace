package com.example.inanyplace.Model;

public class PopularCategory {

    private String image;
    private String name;
    private String foodId;
    private String menuId;

    public PopularCategory() {
    }

    public PopularCategory(String image, String name, String foodId, String menuId) {
        this.image = image;
        this.name = name;
        this.foodId = foodId;
        this.menuId = menuId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
