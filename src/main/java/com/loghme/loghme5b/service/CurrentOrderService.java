package com.loghme.loghme5b.service;

import com.loghme.loghme5b.repo.utils.FoodInOrder;
import com.loghme.loghme5b.repo.utils.Loghme;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class CurrentOrderService {
    private Loghme loghme = Loghme.getInstance();
    @RequestMapping(value = "/currentOrder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodInOrder> getCurrentOrder() {
        if (loghme.getCustomer().getCurrentOrder() != null)
            return loghme.getCustomer().getCurrentOrder().getFoodsInOrder();
        else
            return null;
    }
}