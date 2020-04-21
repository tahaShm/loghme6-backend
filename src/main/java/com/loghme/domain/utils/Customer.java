package com.loghme.domain.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.domain.utils.exceptions.*;

import java.io.IOException;
import java.util.ArrayList;

public class Customer {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private int credit;
    ArrayList<Order> orders = new ArrayList<>();
    Order currentOrder = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public ArrayList<Order> getOrders() { return orders; }

    public void setOrders(ArrayList<Order> orders) { this.orders = orders; }

    public Order getCurrentOrder() { return currentOrder; }

    public void setCurrentOrder(Order order) { this.currentOrder = order; }

    public void addOrder(Order order) { orders.add(order); }

    public void addFoodToCurrentOrder(Food food) {
        currentOrder.addFood(food);
    }

    public void addFoodToCurrentOrder(Food food, Integer count) {
        for (int i = 0; i < count; i++) {
            currentOrder.addFood(food);
        }
    }

    public void removeFoodFromCurrentOrder(Food food, int count) throws FoodNotFoundExp, NotEnoughFoodToDelete {
        currentOrder.removeFood(food, count);
        if (currentOrder.getFoods().isEmpty() && currentOrder.getPartyFoods().isEmpty())
            currentOrder = null;
    }

    public void removePartyFoodFromCurrentOrder(PartyFood food, int count) throws FoodNotFoundExp, NotEnoughFoodToDelete {
        currentOrder.removePartyFood(food, count);
        if (currentOrder.getFoods().isEmpty() && currentOrder.getPartyFoods().isEmpty())
            currentOrder = null;
    }

    public void addPartyFoodToCurrentOrder(PartyFood partyFood, int count) throws ExtraFoodPartyExp {
        if (currentOrder.getPartyFoods().containsKey(partyFood))
            if (currentOrder.getPartyFoods().get(partyFood) + count > partyFood.getCount())
                throw new ExtraFoodPartyExp();
        else if (count > partyFood.getCount())
            throw new ExtraFoodPartyExp();
        currentOrder.addPartyFood(partyFood, count);
    }

    public void emptyCurrentOrder() {currentOrder = null;}

    public String getCartJson() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapperObj = new ObjectMapper();
        String foodCartJson = mapperObj.writeValueAsString(currentOrder);
        return foodCartJson;
    }

    public void addCredit(int toAddCredit) {
        credit += toAddCredit;
    }

    public Order getOrderById(int id) throws OrderNotFound {
        for (Order order: orders) {
            if (order.getId() == id)
                return order;
        }
        throw new OrderNotFound();
    }

    public int cartOverallPrice() {
        return currentOrder.overallPrice();
    }

    public void clearCurrentPartyFoods() {
        if (currentOrder != null) {
            currentOrder.clearPartyFoods();
            if (currentOrder.getFoods().size() == 0)
                currentOrder = null;
        }
    }

    public ArrayList<OrderDTO> getOrdersLists() {
        ArrayList<OrderDTO> toReturn = new ArrayList<>();
        for (Order order: orders) {
            OrderDTO orderDTO = new OrderDTO(order.getStatus(), order.getRestaurant().getName(), order.getFoodsInOrder());
            toReturn.add(orderDTO);
        }
        return toReturn;
    }
}