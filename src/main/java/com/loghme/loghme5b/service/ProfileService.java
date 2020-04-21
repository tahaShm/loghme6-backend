package com.loghme.loghme5b.service;

import com.loghme.loghme5b.repo.utils.Customer;
import com.loghme.loghme5b.repo.utils.Loghme;
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
