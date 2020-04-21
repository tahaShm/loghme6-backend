package com.loghme.loghme5b.repo.utils;

import java.util.ArrayList;

public class OrderList {
    private String status;
    private String restaurantName;
    private ArrayList<FoodInOrder> foods;

    public OrderList(String status, String restaurantName, ArrayList<FoodInOrder> foods) {
        this.status = status;
        this.restaurantName = restaurantName;
        this.foods = foods;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getRestaurantName() { return restaurantName; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public ArrayList<FoodInOrder> getFoods() { return foods; }

    public void setFoods(ArrayList<FoodInOrder> foods) { this.foods = foods; }
}
