package com.loghme.loghme5b.service;

import com.loghme.loghme5b.BadRequestException;
import com.loghme.loghme5b.repo.utils.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class FoodService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/food/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodInOrder> addFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            Restaurant restaurant = loghme.getRestaurantById(id);
            loghme.addToCart(restaurant, foodName, count, false);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getCustomer().getCurrentOrder().getFoodsInOrder();
    }

    @RequestMapping(value = "/food/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodInOrder> deleteFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            Restaurant restaurant = loghme.getRestaurantById(id);
            loghme.removeFromCart(restaurant, foodName, count, false);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        if (loghme.getCustomer().getCurrentOrder() == null)
            return null;
        return loghme.getCustomer().getCurrentOrder().getFoodsInOrder();
    }

    @RequestMapping(value = "/food", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<Food> getFoods(
            @RequestParam(value = "id") String id) {
        ArrayList<Food> foods = null;
        try {
            foods = loghme.getRestaurantFoods(id);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return foods;
    }
}