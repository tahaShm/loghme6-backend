package com.loghme.loghme5b.repo.utils;

public class RestaurantPartyFood {
    private PartyFood food;
    private String restaurantName;
    private String restaurantId;

    public RestaurantPartyFood(PartyFood food, String restaurantName, String restaurantId) {
        this.food = food;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
    }

    public PartyFood getFood() { return food; }

    public void setFood(PartyFood food) { this.food = food; }

    public String getRestaurantName() { return restaurantName; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantId() { return restaurantId; }

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}
