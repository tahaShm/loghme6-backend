package com.loghme.service;

import com.loghme.domain.utils.Customer;
import com.loghme.domain.utils.Loghme;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/profile", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Customer getCustomer() {
        return loghme.getCustomer();
    }
}
