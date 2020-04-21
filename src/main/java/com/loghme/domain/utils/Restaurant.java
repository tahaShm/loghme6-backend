package com.loghme.domain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.domain.utils.exceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Restaurant {
    private String id;
    private String name;
    private Location location;
    private String logo;
    private ArrayList<Food> menu = new ArrayList<>();
    private ArrayList<PartyFood> partyFoods = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Food> getMenu() {
        return menu;
    }

    public void setMenu (ArrayList<Food> menu) {
        this.menu = menu;
    }

    public ArrayList<PartyFood> getPartyFoods() { return partyFoods; }

    public void setPartyFoods(ArrayList<PartyFood> partyFoods) { this.partyFoods = partyFoods; }

    public int foodIdx(String foodName) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.get(i).getName().equals(foodName))
                return i;
        }
        return -1;
    }

    public void addFood(Food newFood) throws FoodAlreadyExistsExp {
        if (foodIdx(newFood.getName()) == -1)
            menu.add(newFood);
        else
            throw new FoodAlreadyExistsExp();
    }

    public String sendJsonInfo() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public String sendJsonFoodInfo(String foodName) throws IOException, FoodNotFoundExp {
        int index = -1;
        for (int i = 0; i < menu.size(); i++) {
            if (foodName.equals(menu.get(i).getName())) {
                index = i;
                break;
            }
        }
        if (index >= 0)
            return menu.get(index).sendInfo();
        else
            throw new FoodNotFoundExp();
    }

    public boolean isFoodValid(String foodName) {
        int index = -1;
        boolean isValid = false;
        for (int i = 0; i < menu.size(); i++) {
            if (foodName.equals(menu.get(i).getName())) {
                index = i;
                break;
            }
        }
        if (index >= 0)
            isValid = true;
        return isValid;
    }

    public float sendPopularity() {
        float average = sendAverageFoodPopulations();
        float distance = location.sendDistance();
        if (distance == 0)
            return Float.MAX_VALUE;
        return average / distance;
    }

    private float sendAverageFoodPopulations() {
        float total = 0;
        for (Food food : menu) {
            total += food.getPopularity();
        }
        return (total / menu.size());
    }

    public int sendFoodPriceByName(String foodName) throws FoodNotFoundExp {
        for (Food food: menu)
            if (foodName.equals(food.getName()))
                return food.getPrice();

        throw new FoodNotFoundExp();
    }

    public void addPartyFoods(ArrayList<PartyFood> newPartyFoods) {
        partyFoods.addAll(newPartyFoods);
    }

    public void updateMenu() {
        for (PartyFood partyFood: partyFoods) {
            if (foodIdx(partyFood.getName()) == -1) {
                Food newFood = new Food();
                newFood.setName(partyFood.getName());
                newFood.setDescription(partyFood.getDescription());
                newFood.setImage(partyFood.getImage());
                newFood.setPopularity(partyFood.getPopularity());
                newFood.setPrice(partyFood.getPrice());
                menu.add(newFood);
            }
        }
    }

    public void deletePartyFoods() {
        partyFoods.clear();
    }

    public void restorePreviousPartyCounts(Order currentOrder) {
        for (Map.Entry<PartyFood, Integer> entry: currentOrder.getPartyFoods().entrySet()) {
            PartyFood currentPartyFood = sendPartyFoodByName(entry.getKey().getName());
            if (currentPartyFood != null)
                currentPartyFood.setCount(currentPartyFood.getCount() + entry.getValue()); //restore previous number of current party food
        }
    }

    private PartyFood sendPartyFoodByName(String currentPartyFoodName) {
        for (PartyFood partyFood: partyFoods) {
            if (partyFood.getName().equals(currentPartyFoodName)) {
                return partyFood;
            }
        }
        return null;
    }

    public void reducePartyFoodAmounts(Order currentOrder) throws ExtraFoodPartyExp {
        for (Map.Entry<PartyFood, Integer> entry: currentOrder.getPartyFoods().entrySet()) {
            PartyFood currentPartyFood = sendPartyFoodByName(entry.getKey().getName());
            if (currentPartyFood != null)
                if (currentPartyFood.getCount() < entry.getValue())
                    throw new ExtraFoodPartyExp();
//                currentPartyFood.setCount(currentPartyFood.getCount() + entry.getValue()); //restore previous number of current party food
        }
        for (Map.Entry<PartyFood, Integer> entry: currentOrder.getPartyFoods().entrySet()) {
            PartyFood currentPartyFood = sendPartyFoodByName(entry.getKey().getName());
            if (currentPartyFood != null) {
                if (currentPartyFood.getCount() - entry.getValue() == 0) {
                    partyFoods.remove(currentPartyFood);
                    return;
                }
                currentPartyFood.setCount(currentPartyFood.getCount() - entry.getValue());
            }
        }
    }
}
