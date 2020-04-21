package com.loghme.domain.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.domain.schedulers.CouriersScheduler;
import com.loghme.domain.utils.exceptions.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Loghme
{
    private ArrayList<Restaurant> restaurants;
    private User user;
    private static Loghme singleApp = null;
    private int lastOrderId = 0;

    private Loghme() {
        restaurants = new ArrayList<>();
        user = new User();
    }

    public static Loghme getInstance() {
        if (singleApp == null)
            singleApp = new Loghme();

        return singleApp;
    }

    public void setRestaurants(ArrayList<Restaurant> inRestaurants) { restaurants.addAll(inRestaurants); }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public User getUser() {
        return user;
    }

    public HashMap<String, Float> sortByValue(HashMap<String, Float> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float> > list =
                new LinkedList<Map.Entry<String, Float> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Float> >() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private Map<String, Float> getBestRestaurants(int numOfBests) {
        HashMap<String, Float> allRestaurants = new HashMap<String, Float>();
        Map<String, Float> sortedRestaurants;

        for (Restaurant restaurant : restaurants) {
            String currentRestaurantName = restaurant.getName();
            float currentPopularity = restaurant.sendPopularity();
            allRestaurants.put(currentRestaurantName, currentPopularity);
        }

        sortedRestaurants = sortByValue(allRestaurants);
        allRestaurants.clear();
        int counter = 0;
        for (Map.Entry<String, Float> entry : sortedRestaurants.entrySet()) {
            if (counter >= numOfBests)
                break;
            allRestaurants.put(entry.getKey(), entry.getValue());
            counter++;
        }

        return allRestaurants;
    }

    public ArrayList<Food> getRestaurantFoods(String id) throws NotFound404Exp {
        Restaurant restaurant = getRestaurantById(id);
        return restaurant.getMenu();
    }

    public void addToCart(Restaurant restaurant, String foodName, int count, boolean isPartyFood) throws FoodFromOtherRestaurantInCartExp, ExtraFoodPartyExp, FoodNotFoundExp {
        boolean allowToAdd = false;
        if (user.getCurrentOrder() == null) {
            Order newOrder = new Order(lastOrderId, restaurant);
            user.setCurrentOrder(newOrder);
            allowToAdd = true;
            lastOrderId++;
        }
        else {
            if (restaurant.getId().equals(user.currentOrder.getRestaurant().getId()))
                allowToAdd = true;
        }
        if (allowToAdd) {
            if (isPartyFood) {
                PartyFood partyFood = getPartyFoodByName(foodName, restaurant);
                user.addPartyFoodToCurrentOrder(partyFood, count);
            }
            else {
                Food food = getFoodByName(foodName, restaurant);
                user.addFoodToCurrentOrder(food, count);
            }
        }
        else
            throw new FoodFromOtherRestaurantInCartExp();
    }

    public void removeFromCart(Restaurant restaurant, String foodName, int count, boolean isPartyFood) throws FoodFromOtherRestaurantInCartExp, NotEnoughFoodToDelete, FoodNotFoundExp {
        if (!restaurant.getId().equals(user.getCurrentOrder().getRestaurant().getId()))
            throw new FoodFromOtherRestaurantInCartExp();
        if (isPartyFood) {
            PartyFood partyFood = getPartyFoodByName(foodName, restaurant);
            user.removePartyFoodFromCurrentOrder(partyFood, count);
        }
        else {
            Food food = getFoodByName(foodName, restaurant);
            user.removeFoodFromCurrentOrder(food, count);
        }
    }

    public void finalizeOrder() throws NotEnoughCreditExp, ExtraFoodPartyExp {
        int cartPrice = user.cartOverallPrice();
        Restaurant currentRestaurant = user.getCurrentOrder().getRestaurant();
        if (cartPrice > user.getCredit()) {
            user.emptyCurrentOrder();
            throw new NotEnoughCreditExp();
        }
        try {
            currentRestaurant.reducePartyFoodAmounts(user.getCurrentOrder());
        }
        catch (ExtraFoodPartyExp e) {
            user.emptyCurrentOrder();
            throw new ExtraFoodPartyExp();
        }
        user.getCurrentOrder().setStatus("finding delivery");

        Timer timer = new Timer();
        TimerTask task = new CouriersScheduler(user.getCurrentOrder());
        timer.schedule(task, 0, 3000);

        user.addCredit(-1 * cartPrice);
        user.addOrder(user.getCurrentOrder());
        user.emptyCurrentOrder();
    }

    public ArrayList<Restaurant> getCloseRestaurants(float distance){
        ArrayList<Restaurant> closeRestaurants = new ArrayList<>();
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getLocation().sendDistance() <= distance) {
                closeRestaurants.add(restaurants.get(i));
            }
        }
        return closeRestaurants;
    }

    public Restaurant getRestaurantById(String inId) throws NotFound404Exp {
        for (Restaurant restaurant: restaurants)
            if (restaurant.getId().equals(inId))
                return restaurant;
        throw new NotFound404Exp();
    }

    public void addCredit(int credit) {
        user.addCredit(credit);
    }

    public void addCredit(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        user.addCredit(obj.getInt("credit"));
    }

    public Food getFoodByName(String foodName, Restaurant restaurant) throws FoodNotFoundExp {
        for (Food food: restaurant.getMenu()) {
            if (food.getName().equals(foodName))
                return food;
        }
        throw new FoodNotFoundExp();
    }

    public PartyFood getPartyFoodByName(String foodName, Restaurant restaurant) throws FoodNotFoundExp {
        for (PartyFood partyFood: restaurant.getPartyFoods()) {
            if (partyFood.getName().equals(foodName))
                return partyFood;
        }
        throw new FoodNotFoundExp();
    }

    public void deletePreviousPartyFoods(){
        for (Restaurant restaurant: restaurants) {
            if (restaurant.getPartyFoods().size() > 0) {
                restaurant.deletePartyFoods();
            }
        }
    }

    private void deletePreviousUserPartyFoods() {
        user.clearCurrentPartyFoods();
    }

    public void addPartyRestaurants(ArrayList<Restaurant> partyRestaurants) {
        deletePreviousPartyFoods();
        deletePreviousUserPartyFoods();
        for (Restaurant restaurant: partyRestaurants) {
            Restaurant currentRestaurant = null;
            try {
                currentRestaurant = getRestaurantById(restaurant.getId());
                currentRestaurant.addPartyFoods(restaurant.getPartyFoods());
            } catch (NotFound404Exp notFound404Exp) {
                currentRestaurant = restaurant;
                restaurants.add(restaurant);
            }
            currentRestaurant.updateMenu();
        }
    }

    public ArrayList<PartyFoodDTO> getPartyFoodsDTO() {
        ArrayList<PartyFoodDTO> foods = new ArrayList<>();
        for (Restaurant restaurant: restaurants) {
            for (PartyFood partyFood: restaurant.getPartyFoods())
                foods.add(new PartyFoodDTO(partyFood, restaurant.getName(), restaurant.getId()));
        }
        return foods;
    }
}