package com.bundung.models;

import java.util.List;
import java.util.Map;

public class Statistics {
    private int totalOrders;
    private double totalRevenue;
    private Map<String, Integer> ordersByStatus;
    private List<TopDish> topDishes;
    private Map<String, Double> revenueByPayment;

    public Statistics() {}

    // Getters and Setters
    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Integer> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Integer> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public List<TopDish> getTopDishes() {
        return topDishes;
    }

    public void setTopDishes(List<TopDish> topDishes) {
        this.topDishes = topDishes;
    }

    public Map<String, Double> getRevenueByPayment() {
        return revenueByPayment;
    }

    public void setRevenueByPayment(Map<String, Double> revenueByPayment) {
        this.revenueByPayment = revenueByPayment;
    }

    // Inner class for top dishes
    public static class TopDish {
        private String name;
        private int quantitySold;
        private double revenue;

        public TopDish() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantitySold() {
            return quantitySold;
        }

        public void setQuantitySold(int quantitySold) {
            this.quantitySold = quantitySold;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }
    }
}