package com.example.szonyeg_webshop_mobilaf;

public class ShoppingItem {
    private String name;
    private String info;
    private String price;
    private float retadInfo;
    private final int imageResourceId;

    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.retadInfo = ratedInfo;
        this.imageResourceId = imageResource;
    }

    public int getImageResource() {
        return imageResourceId;
    }

    public float getRetadInfo() {
        return retadInfo;
    }

    String getPrice() {
        return price;
    }

    String getInfo() {
        return info;
    }

    String getName() {
        return name;
    }
}
