package com.bundung.models;

public class OrderDetail {
    private int id;
    private int menuItemId;
    private String name;
    private int quantity;
    private double price;
    private String note;

    public OrderDetail() {}

    public OrderDetail(int menuItemId, String name, int quantity, double price, String note) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.note = note;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // Helper methods
    public double getTotalPrice() {
        return this.price * this.quantity;
    }
}