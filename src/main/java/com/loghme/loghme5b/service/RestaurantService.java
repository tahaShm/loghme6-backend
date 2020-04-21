package com.loghme.loghme5b.service;

import com.loghme.loghme5b.BadRequestException;
import com.loghme.loghme5b.repo.utils.Loghme;
import com.loghme.loghme5b.repo.utils.Restaurant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class RestaurantService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/restaurant", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<Restaurant> getRestaurants() {
        return loghme.getRestaurants();
    }

    @RequestMapping(value = "/restaurant/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Restaurant getRestaurantById(
            @PathVariable(value = "id") String id) {
        try {
            return loghme.getRestaurantById(id);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
