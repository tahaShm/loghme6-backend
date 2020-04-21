package com.loghme.service;

import com.loghme.domain.utils.User;
import com.loghme.domain.utils.Loghme;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/profile", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser() {
        return loghme.getUser();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public String authenticate(HttpEntity<String> httpEntity) {
        return httpEntity.getBody();
    }
}
