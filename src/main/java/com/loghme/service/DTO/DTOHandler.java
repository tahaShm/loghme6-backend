package com.loghme.service.DTO;

import com.loghme.domain.utils.*;
import com.loghme.domain.utils.exceptions.RestaurantNotFoundExp;
import com.loghme.repository.DAO.FoodDAO;
import com.loghme.repository.DAO.PartyFoodDAO;
import com.loghme.repository.DAO.RestaurantDAO;
import com.loghme.repository.LoghmeRepository;

import java.util.ArrayList;
import java.util.Map;

public class DTOHandler {
    private static Loghme loghme = Loghme.getInstance();

    public static ArrayList<PartyFoodDTO> getPartyFoods() {
//        ArrayList<PartyFoodDTO> foods = new ArrayList<>();
//        for (Restaurant restaurant: loghme.getRestaurants()) {
//            for (PartyFood partyFood: restaurant.getPartyFoods())
//                foods.add(new PartyFoodDTO(partyFood, restaurant.getName(), restaurant.getId()));
//        }
//        return foods;
        ArrayList<PartyFoodDTO> retPartyFoods = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<PartyFoodDAO> partyFoods = loghmeRepo.getValidPartyFoods();
        for (PartyFoodDAO partyFood: partyFoods) {
            RestaurantDAO currentRestaurant = loghmeRepo.getRestaurantByPartyFoodId(partyFood.getId());
            retPartyFoods.add(new PartyFoodDTO(partyFood, currentRestaurant.getName(), currentRestaurant.getId()));
        }
        return retPartyFoods;
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
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<FoodDAO> foods = loghmeRepo.getRestaurantFoods(id);
        for (FoodDAO food: foods) {
            System.out.println(food.getImageUrl());
            toReturn.add(new FoodDTO(food.getName(), food.getPrice(), food.getDescription(), food.getPopularity(), food.getImageUrl()));
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
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<RestaurantDAO> restaurants = loghmeRepo.getRestaurants();
        for (RestaurantDAO restaurant: restaurants) {
            System.out.println(restaurant.getId());
            toReturn.add(new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId())));
        }
        return toReturn;
    }

    public static RestaurantDTO getRestaurantById(String id) throws RestaurantNotFoundExp {
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        RestaurantDAO restaurant = loghmeRepo.getRestaurantById(id);
        if (restaurant == null)
            throw new RestaurantNotFoundExp();
        return new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId()));
    }

    public static ArrayList<RestaurantDTO> getSearchedRestaurants(String restaurantName, String foodName){
        ArrayList<RestaurantDTO> toReturn = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<RestaurantDAO> restaurants = loghmeRepo.getSearchedRestaurants(restaurantName, foodName);
        for (RestaurantDAO restaurant: restaurants) {
            System.out.println(restaurant.getId());
            toReturn.add(new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId())));
        }
        return toReturn;
    }
}
