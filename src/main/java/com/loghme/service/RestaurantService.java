package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.Restaurant;
import com.loghme.domain.utils.exceptions.BadRequestException;
import com.loghme.repository.LoghmeRepository;
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
