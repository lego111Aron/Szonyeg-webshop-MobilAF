package com.example.szonyeg_webshop_mobilaf;

public class ShoppingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private float retadInfo;
    private String imageUrl; // <-- int helyett String

    private int cartedCount;

    public ShoppingItem() {}

    public ShoppingItem(String name, String info, String price, float ratedInfo, String imageUrl, int cartedCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.retadInfo = ratedInfo;
        this.imageUrl = imageUrl;
        this.cartedCount = cartedCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getRetadInfo() {
        return retadInfo;
    }

    public String getPrice() {
        return price;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public int getCartedCount() {return cartedCount; }
    public String _getId() {return id;}
    public void setId(String id) {this.id = id; }
}
// ...existing code...