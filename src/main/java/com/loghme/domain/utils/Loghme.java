package com.loghme.domain.utils;

import com.loghme.domain.schedulers.CouriersScheduler;
import com.loghme.domain.utils.exceptions.*;
import com.loghme.repository.LoghmeRepository;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.service.DTO.UserDTO;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class Loghme
{
    private ArrayList<Restaurant> restaurants;
    private User user;
    private static Loghme singleApp = null;
    private int lastOrderId = 0;
    private LoghmeRepository loghmeRepository = LoghmeRepository.getInstance();

    private Loghme() {
        restaurants = new ArrayList<>();
        user = new User();
    }

    public static Loghme getInstance() {
        if (singleApp == null)
            singleApp = new Loghme();

        return singleApp;
    }

    public void setRestaurants(ArrayList<Restaurant> inRestaurants) { restaurants.addAll(inRestaurants); }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public User getUser()  {
        return user;
    }

    public HashMap<String, Float> sortByValue(HashMap<String, Float> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float> > list =
                new LinkedList<Map.Entry<String, Float> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Float> >() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private Map<String, Float> getBestRestaurants(int numOfBests) {
        HashMap<String, Float> allRestaurants = new HashMap<String, Float>();
        Map<String, Float> sortedRestaurants;

        for (Restaurant restaurant : restaurants) {
            String currentRestaurantName = restaurant.getName();
            float currentPopularity = restaurant.sendPopularity();
            allRestaurants.put(currentRestaurantName, currentPopularity);
        }

        sortedRestaurants = sortByValue(allRestaurants);
        allRestaurants.clear();
        int counter = 0;
        for (Map.Entry<String, Float> entry : sortedRestaurants.entrySet()) {
            if (counter >= numOfBests)
                break;
            allRestaurants.put(entry.getKey(), entry.getValue());
            counter++;
        }

        return allRestaurants;
    }

    public void changeCart(String username, String restaurantId, String foodName, int count, boolean isPartyFood) throws FoodFromOtherRestaurantInCartExp, ExtraFoodPartyExp, NotEnoughFoodToDelete {
        String currentOrderRestaurantId = loghmeRepository.getCurrentOrderRestaurantId(user.getId());
        if (currentOrderRestaurantId == null || currentOrderRestaurantId.equals(restaurantId)) {
            if (isPartyFood)
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "party");
            else
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "normal");
        }
        else
            throw new FoodFromOtherRestaurantInCartExp();
    }

    public void finalizeOrder(String username) throws NotEnoughCreditExp, RestaurantNotFoundExp {
//        int cartPrice = user.cartOverallPrice();
//        Restaurant currentRestaurant = user.getCurrentOrder().getRestaurant();
//        if (cartPrice > user.getCredit()) {
//            user.emptyCurrentOrder();
//            throw new NotEnoughCreditExp();
//        }
//        try {
//            currentRestaurant.reducePartyFoodAmounts(user.getCurrentOrder());
//        }
//        catch (ExtraFoodPartyExp e) {
//            user.emptyCurrentOrder();
//            throw new ExtraFoodPartyExp();
//        }
//        user.getCurrentOrder().setStatus("searching");

//        user.addCredit(-1 * cartPrice);
//        user.addOrder(user.getCurrentOrder());

//        Order currentOrder = getUser().getCurrentOrder();
//        int orderId = loghmeRepository.addOrder(getUser().getId(), currentOrder.getRestaurant().getId(), "searching", currentOrder.getFoods(), currentOrder.getPartyFoods());

        int orderId = loghmeRepository.finalizeOrder(username);
        Location location = loghmeRepository.getOrderRestaurantLocation(orderId);

        Timer timer = new Timer();
        TimerTask task = new CouriersScheduler(location, orderId);
        timer.schedule(task, 0, 3000);

//        user.emptyCurrentOrder();
    }

    public ArrayList<Restaurant> getCloseRestaurants(float distance){
        ArrayList<Restaurant> closeRestaurants = new ArrayList<>();
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getLocation().sendDistance() <= distance) {
                closeRestaurants.add(restaurants.get(i));
            }
        }
        return closeRestaurants;
    }

    public Restaurant getRestaurantById(String inId) throws RestaurantNotFoundExp {
        for (Restaurant restaurant: restaurants)
            if (restaurant.getId().equals(inId))
                return restaurant;
        throw new RestaurantNotFoundExp();
    }

    public void addCredit(String username, String json) throws JSONException, NotEnoughCreditExp {
        JSONObject obj = new JSONObject(json);
        loghmeRepository.changeCredit(username, obj.getInt("credit"));
    }

    public int getUserCredit(String username) {
        return loghmeRepository.getCredit(username);
    }

    public UserDTO getUserDTO(String username) {
        return loghmeRepository.getUserDTO(username);
    }

    public Food getFoodByName(String foodName, Restaurant restaurant) throws FoodNotFoundExp {
        for (Food food: restaurant.getMenu()) {
            if (food.getName().equals(foodName))
                return food;
        }
        throw new FoodNotFoundExp();
    }

    public PartyFood getPartyFoodByName(String foodName, Restaurant restaurant) throws FoodNotFoundExp {
        for (PartyFood partyFood: restaurant.getPartyFoods()) {
            if (partyFood.getName().equals(foodName))
                return partyFood;
        }
        throw new FoodNotFoundExp();
    }

    public void deletePreviousPartyFoods(){
        for (Restaurant restaurant: restaurants) {
            if (restaurant.getPartyFoods().size() > 0) {
                restaurant.deletePartyFoods();
            }
        }
    }

    private void deletePreviousUserPartyFoods() {
        user.clearCurrentPartyFoods();
    }

    public void addPartyRestaurants(ArrayList<Restaurant> partyRestaurants) throws SQLException {
        loghmeRepository.invalidPrevPartyFoods();
        for (Restaurant restaurant: partyRestaurants) {
            loghmeRepository.addRestaurant(restaurant.getId(), restaurant.getName(), restaurant.getLogo(), restaurant.getLocation().getX(), restaurant.getLocation().getY());
            for (PartyFood partyFood: restaurant.getPartyFoods()) {
                int foodId = loghmeRepository.addFood(restaurant.getId(), partyFood.getName(), partyFood.getDescription(), partyFood.getPopularity(), partyFood.getImage(), partyFood.getPrice(), partyFood.getCount());
                loghmeRepository.addPartyFood(restaurant.getId(), foodId, partyFood.getNewPrice(), partyFood.getCount());

            }
        }
        deletePreviousPartyFoods();
        deletePreviousUserPartyFoods();
        for (Restaurant restaurant: partyRestaurants) {
            Restaurant currentRestaurant = null;
            try {
                currentRestaurant = getRestaurantById(restaurant.getId());
                currentRestaurant.addPartyFoods(restaurant.getPartyFoods());
            } catch (RestaurantNotFoundExp e) {
                currentRestaurant = restaurant;
                restaurants.add(restaurant);
            }
            currentRestaurant.updateMenu();
        }
    }

    public ArrayList<OrderDTO> getUserOrders(String username) {
        return loghmeRepository.getOrders(username);
    }

    public ArrayList<FoodDTO> getCurrentOrderFoods(String username) {
        return loghmeRepository.getCurrentOrderFoods(username);
    }
}