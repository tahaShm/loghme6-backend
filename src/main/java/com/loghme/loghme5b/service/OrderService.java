package com.loghme.loghme5b.service;

import com.loghme.loghme5b.BadRequestException;
import com.loghme.loghme5b.repo.utils.Loghme;
import com.loghme.loghme5b.repo.utils.OrderList;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class OrderService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/order", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<OrderList> getOrders() {
        return loghme.getCustomer().getOrdersLists();
    }

    @RequestMapping(value = "/order", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void finalizeOrder() {
        try {
            loghme.finalizeOrder();
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
