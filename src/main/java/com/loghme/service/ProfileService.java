package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.UserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/profile", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUser() {
        return DTOHandler.getUser();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public String authenticate(HttpEntity<String> httpEntity) {
        return httpEntity.getBody();
    }
}
