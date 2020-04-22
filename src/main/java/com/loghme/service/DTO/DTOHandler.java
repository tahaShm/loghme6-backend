package com.loghme.service.DTO;

import com.loghme.domain.utils.*;
import com.loghme.domain.utils.exceptions.RestaurantNotFoundExp;

import java.util.ArrayList;
import java.util.Map;

public class DTOHandler {
    private static Loghme loghme = Loghme.getInstance();

    public static ArrayList<PartyFoodDTO> getPartyFoods() {
        ArrayList<PartyFoodDTO> foods = new ArrayList<>();
        for (Restaurant restaurant: loghme.getRestaurants()) {
            for (PartyFood partyFood: restaurant.getPartyFoods())
                foods.add(new PartyFoodDTO(partyFood, restaurant.getName(), restaurant.getId()));
        }
        return foods;
    }

    public static ArrayList<FoodDTO> getCurrentOrder() {
        ArrayList<FoodDTO> toReturn = new ArrayList<>();
        for (Map.Entry<Food, Integer> entry: loghme.getUser().getCurrentOrder().getFoods().entrySet()) {
            Food currFood = entry.getKey();
            toReturn.add(new FoodDTO(currFood.getName(), currFood.getPrice(), entry.getValue()));
        }
        for (Map.Entry<PartyFood, Integer> entry: loghme.getUser().getCurrentOrder().getPartyFoods().entrySet()) {
            PartyFood currFood = entry.getKey();
            toReturn.add(new FoodDTO(currFood.getName(), currFood.getNewPrice(), entry.getValue()));
        }
        return toReturn;
    }

    public static ArrayList<FoodDTO> getOrderFoods(Order order) {
        ArrayList<FoodDTO> toReturn = new ArrayList<>();
        for (Map.Entry<Food, Integer> entry: order.getFoods().entrySet()) {
            Food currFood = entry.getKey();
            toReturn.add(new FoodDTO(currFood.getName(), currFood.getPrice(), entry.getValue()));
        }
        for (Map.Entry<PartyFood, Integer> entry: order.getPartyFoods().entrySet()) {
            PartyFood currFood = entry.getKey();
            toReturn.add(new FoodDTO(currFood.getName(), currFood.getNewPrice(), entry.getValue()));
        }
        return toReturn;
    }

    public static ArrayList<FoodDTO> getRestaurantFoods(String id) throws RestaurantNotFoundExp {
        ArrayList<FoodDTO> toReturn = new ArrayList<>();
        Restaurant restaurant = loghme.getRestaurantById(id);
        for (Food food: restaurant.getMenu()) {
            toReturn.add(new FoodDTO(food.getName(), food.getPrice(), food.getDescription(), food.getPopularity(), food.getImage()));
        }
        return toReturn;
    }

    public static ArrayList<OrderDTO> getOrders() {
        ArrayList<OrderDTO> toReturn = new ArrayList<>();
        for (Order order: loghme.getUser().getOrders()) {
            OrderDTO orderDTO = new OrderDTO(order.getStatus(), order.getRestaurant().getName(), getOrderFoods(order));
            toReturn.add(orderDTO);
        }
        return toReturn;
    }
    public static UserDTO getUser() {
        User user = loghme.getUser();
        return new UserDTO(user.getId(), user.getName(), user.getPhoneNumber(), user.getEmail(), user.getCredit());
    }

    public static ArrayList<FoodDTO> getRestaurantMenu(Restaurant restaurant) {
        ArrayList<FoodDTO> foodDTOS = new ArrayList<>();
        for (Food food: restaurant.getMenu()) {
            foodDTOS.add(new FoodDTO(food.getName(), food.getPrice(), food.getDescription(), food.getPopularity(), food.getImage()));
        }
        return foodDTOS;
    }

    public static ArrayList<RestaurantDTO> getRestaurants() {
        ArrayList<RestaurantDTO> toReturn = new ArrayList<>();
        for (Restaurant restaurant: loghme.getRestaurants()) {
            toReturn.add(new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getLocation(), restaurant.getLogo(), getRestaurantMenu(restaurant)));
        }
        return toReturn;
    }

    public static RestaurantDTO getRestaurantById(String id) throws RestaurantNotFoundExp {
        Restaurant restaurant = loghme.getRestaurantById(id);
        return new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getLocation(), restaurant.getLogo(), getRestaurantMenu(restaurant));
    }
}
