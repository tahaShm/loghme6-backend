package com.loghme.loghme5b.repo.schedulers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.loghme5b.repo.utils.Food;
import com.loghme.loghme5b.repo.utils.Loghme;
import com.loghme.loghme5b.repo.utils.PartyFood;
import com.loghme.loghme5b.repo.utils.Restaurant;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class RestaurantsScheduler implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new Callable() {
            public Object call() throws Exception {
                String loghmeBody = "";
                ArrayList<Restaurant> restaurants = null;
                Loghme loghme = Loghme.getInstance();
                ArrayList<Restaurant> convertedRestaurants;
                ObjectMapper nameMapper = new ObjectMapper();
                try {
                    loghmeBody = HTTPHandler.getUrlBody("http://138.197.181.131:8080/restaurants");
                    restaurants = nameMapper.readValue(loghmeBody, ArrayList.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                convertedRestaurants = nameMapper.convertValue(restaurants, new TypeReference<ArrayList<Restaurant>>() { });
                loghme.setRestaurants(convertedRestaurants);
                loghme.getCustomer().setId("1234");
                loghme.getCustomer().setName("Houman Chamani");
                loghme.getCustomer().setPhoneNumber("+989300323231");
                loghme.getCustomer().setEmail("hoomch@gmail.com");
                loghme.getCustomer().setCredit(100000);
                return 0;
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}
