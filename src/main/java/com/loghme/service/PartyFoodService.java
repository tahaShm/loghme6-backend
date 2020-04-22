package com.loghme.service;

import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.Restaurant;
import com.loghme.service.DTO.PartyFoodDTO;
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
        return DTOHandler.getCurrentOrder();
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
        return DTOHandler.getCurrentOrder();
    }

    @RequestMapping(value = "/partyFood", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<PartyFoodDTO> getFoods() {
        try {
            return DTOHandler.getPartyFoods();
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
