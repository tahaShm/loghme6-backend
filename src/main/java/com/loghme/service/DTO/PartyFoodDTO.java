package com.loghme.service.DTO;

import com.loghme.domain.utils.PartyFood;

public class PartyFoodDTO {
    private PartyFood food;
    private String restaurantName;
    private String restaurantId;

    public PartyFoodDTO(PartyFood food, String restaurantName, String restaurantId) {
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
