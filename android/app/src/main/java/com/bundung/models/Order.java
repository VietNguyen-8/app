package com.bundung.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int tableNumber;
    private String status;
    private String serverName;
    private double totalAmount;
    private String paymentMethod;
    private String createdAt;
    private String updatedAt;
    private List<OrderDetail> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int id, int tableNumber, String status, double totalAmount) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderDetail> getItems() {
        return items;
    }

    public void setItems(List<OrderDetail> items) {
        this.items = items;
    }

    // Status constants
    public static final String STATUS_NEW = "moi";
    public static final String STATUS_COOKING = "dang_nau";
    public static final String STATUS_DONE = "hoan_thanh";
    public static final String STATUS_PAID = "da_thanh_toan";

    // Payment method constants
    public static final String PAYMENT_CASH = "tien_mat";
    public static final String PAYMENT_EWALLET = "vi_dien_tu";

    // Helper method
    public String getStatusDisplay() {
        switch (status) {
            case STATUS_NEW:
                return "Đơn mới";
            case STATUS_COOKING:
                return "Đang nấu";
            case STATUS_DONE:
                return "Hoàn thành";
            case STATUS_PAID:
                return "Đã thanh toán";
            default:
                return status;
        }
    }
}