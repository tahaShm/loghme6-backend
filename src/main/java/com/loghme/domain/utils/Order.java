package com.loghme.domain.utils;

import com.loghme.domain.utils.exceptions.*;
import com.loghme.service.DTO.FoodDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private HashMap<Food, Integer> foods = new HashMap<>();
    private HashMap<PartyFood, Integer> partyFoods = new HashMap<>();
    private int id;
    private double remainingTime;
    private String status;
    Restaurant restaurant;
    long deliveryBeginTime;

    public Order(int id, Restaurant restaurant) {
        this.id = id;
        this.restaurant = restaurant;
    }

    public void setId(int id) {this.id = id;}

    public void setRemainingTime(double remainingTime) {this.remainingTime = remainingTime;}

    public void setStatus(String status) {this.status = status;}

    public HashMap<Food, Integer> getFoods() {return foods;}

    public HashMap<PartyFood, Integer> getPartyFoods() {return partyFoods;}

    public int getId() {return id;}

    public double getRemainingTime() {return remainingTime;}

    public String getStatus() {return status;}

    public Restaurant getRestaurant() {return restaurant;}

    public void setDeliveryBeginTime(long time) {deliveryBeginTime = time;}

    public long getDeliveryBeginTime() {return deliveryBeginTime;}

    public void addFood(Food food) {
        if (foods.containsKey(food))
            foods.put(food, foods.get(food) + 1);
        else
            foods.put(food, 1);
    }

    public void removeFood(Food food, int count) throws FoodNotFoundExp, NotEnoughFoodToDelete {
        if (foods.containsKey(food)) {
            if (count > foods.get(food))
                throw new NotEnoughFoodToDelete();
            else if (foods.get(food) == count)
                foods.remove(food);
            else
                foods.put(food, foods.get(food) - count);
        }
        else
            throw new FoodNotFoundExp();
    }

    public void removePartyFood(PartyFood partyFood, int count) throws FoodNotFoundExp, NotEnoughFoodToDelete {
        if (partyFoods.containsKey(partyFood)) {
            if (count > partyFoods.get(partyFood))
                throw new NotEnoughFoodToDelete();
            else if (partyFoods.get(partyFood) == count)
                partyFoods.remove(partyFood);
            else
                partyFoods.put(partyFood, partyFoods.get(partyFood) - count);
        }
        else
            throw new FoodNotFoundExp();
    }

    public void addPartyFood(PartyFood partyFood, int count) {
        if (partyFoods.containsKey(partyFood))
            partyFoods.put(partyFood, partyFoods.get(partyFood) + count);
        else
            partyFoods.put(partyFood, count);
    }

    public int overallPrice() {
        int price = 0;
        for (Map.Entry<Food, Integer> entry: foods.entrySet()) {
            price += entry.getKey().getPrice() * entry.getValue();
        }
        for (Map.Entry<PartyFood, Integer> entry: partyFoods.entrySet()) {
            price += entry.getKey().getNewPrice() * entry.getValue();
        }
        return price;
    }

    public void clearPartyFoods() {
        partyFoods.clear();
    }
}