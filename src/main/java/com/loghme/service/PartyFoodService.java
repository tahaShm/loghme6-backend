package com.loghme.service;

import com.loghme.domain.utils.FoodDTO;
import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.Restaurant;
import com.loghme.domain.utils.PartyFoodDTO;
import com.loghme.domain.utils.exceptions.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class PartyFoodService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/partyFood/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> addFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            Restaurant restaurant = loghme.getRestaurantById(id);
            loghme.addToCart(restaurant, foodName, count, true);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getUser().getCurrentOrder().getFoodsInOrder();
    }

    @RequestMapping(value = "/partyFood/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> deleteFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            Restaurant restaurant = loghme.getRestaurantById(id);
            loghme.removeFromCart(restaurant, foodName, count, true);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getUser().getCurrentOrder().getFoodsInOrder();
    }

    @RequestMapping(value = "/partyFood", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<PartyFoodDTO> getFoods() {
        try {
            return loghme.getPartyFoodsDTO();
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
