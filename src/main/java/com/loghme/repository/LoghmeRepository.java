package com.loghme.repository;

import com.loghme.repository.DAO.RestaurantDAO;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoghmeRepository {
    private static LoghmeRepository instance;
    public static final int MYSQL_DUPLICATE_PK = 1062;

    ComboPooledDataSource dataSource;

    private LoghmeRepository() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/loghme6?useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("Taha1378");

        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(20);
        dataSource.setMaxStatements(100);
    }

    public static LoghmeRepository getInstance() {
        if (instance == null)
            instance = new LoghmeRepository();
        return instance;
    }
    public void loginUser(String username, String password) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Users where username = \"" + username + "\" and password = \"" + password +"\"");
            if (result.next()) {
                String phoneNumber = result.getString("phoneNumber");
                //same way for other attributes
                System.out.println(phoneNumber);
                statement.close();
                connection.close();
            }
            //exception not handled
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RestaurantDAO> getRestaurants() {
        ArrayList<RestaurantDAO> restaurants = new ArrayList<RestaurantDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Restaurants");
            while (result.next()) {
                RestaurantDAO restaurantDao = new RestaurantDAO();
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
                restaurants.add(restaurantDao);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    public void addRestaurant(String id, String name, String logo, float x, float y) throws SQLException {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into Restaurants (id, name, logoUrl, x, y) values (?, ?, ?, ?, ?)");
            pStatement.setString(1, id);
            pStatement.setString(2, name);
            pStatement.setString(3, logo);
            pStatement.setFloat(4, x);
            pStatement.setFloat(5, y);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            if(e.getErrorCode() == MYSQL_DUPLICATE_PK ) {
                connection = dataSource.getConnection();
                PreparedStatement pStatement = connection.prepareStatement(
                        "UPDATE Restaurants " +
                                "SET name=?, logoUrl=?, x=?, y=? " +
                                "WHERE id = ?;");
                pStatement.setString(1, name);
                pStatement.setString(2, logo);
                pStatement.setFloat(3, x);
                pStatement.setFloat(4, y);
                pStatement.setString(5, id);
                pStatement.executeUpdate();
                pStatement.close();
                connection.close();
            }
        }
    }

    public int addFood(String restaurantId, String name, String description, float popularity, String imageUrl, int price, int count) {
        Connection connection;
        int foodId = 0;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select F.id from Foods F, Menu M where M.restaurantId = \"" + restaurantId + "\" and F.name = \"" + name + "\" and M.foodId = F.id");
            if (result.next()) { //food already exits -> update Foods
                foodId = result.getInt("id");
//                System.out.println("food update here!  " + foodId);
                PreparedStatement pStatement = connection.prepareStatement(
                        "update Foods set description = ?, popularity = ?, imageUrl = ?, price = ?, count = ? where id = ?");
                pStatement.setString(1, description);
                pStatement.setFloat(2, popularity);
                pStatement.setString(3, imageUrl);
                pStatement.setInt(4, price);
                pStatement.setInt(5, count);
                pStatement.setInt(6, foodId);
                pStatement.executeUpdate();
                pStatement.close();
            }
            else { //new food -> insert into Foods and Menu
                PreparedStatement pStatement = connection.prepareStatement(
                        "insert into Foods (name, description, popularity, imageUrl, price, count) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pStatement.setString(1, name);
                pStatement.setString(2, description);
                pStatement.setFloat(3, popularity);
                pStatement.setString(4, imageUrl);
                pStatement.setInt(5, price);
                pStatement.setInt(6, count);
                pStatement.executeUpdate();
                ResultSet rs = pStatement.getGeneratedKeys();

                if(rs.next())
                    foodId = rs.getInt(1);
//                System.out.println("food insert here!" + foodId);
                rs.close();
                pStatement.close();

                PreparedStatement pStatementMenu = connection.prepareStatement(
                        "insert into Menu (restaurantId, foodId) values (?, ?)");
                pStatementMenu.setString(1, restaurantId);
                pStatementMenu.setInt(2, foodId);
                pStatementMenu.executeUpdate();
                pStatementMenu.close();
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return foodId;
    }

    public void invalidPrevPartyFoods() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            String invalid = "0";
            String valid = "1";
            PreparedStatement pStatement = connection.prepareStatement(
                    "update PartyFoods set valid = ? where valid = ?");
            pStatement.setString(1, invalid);
            pStatement.setString(2, valid);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPartyFood(String restaurantId, int foodId, int newPrice, int count){
        Connection connection;
        int partyFoodId = 0;
        String valid = "1";
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select PF.id from PartyFoods PF, PartyMenu PM where PM.restaurantId = \"" + restaurantId + "\" and PF.foodId = \"" + foodId + "\" and PM.partyFoodId = PF.id");
            if (result.next()) { //partyFood already exits -> update PartyFoods
                partyFoodId = result.getInt("id");
                System.out.println("partyfood update here!  " + partyFoodId);
                PreparedStatement pStatement = connection.prepareStatement(
                        "update PartyFoods set newPrice = ?, count = ?, valid = ? where id = ?");

                pStatement.setInt(1, newPrice);
                pStatement.setInt(2, count);
                pStatement.setString(3, valid);
                pStatement.setInt(4, partyFoodId);
                pStatement.executeUpdate();
                pStatement.close();
            } else { //new food -> insert into PartyFoods and Menu

                PreparedStatement pStatement = connection.prepareStatement(
                        "insert into PartyFoods (foodId, newPrice, count, valid) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pStatement.setInt(1, foodId);
                pStatement.setInt(2, newPrice);
                pStatement.setInt(3, count);
                pStatement.setString(4, valid);
                pStatement.executeUpdate();
                ResultSet rs = pStatement.getGeneratedKeys();

                if (rs.next())
                    partyFoodId = rs.getInt(1);
                System.out.println("partyfood insert here!" + partyFoodId);
                rs.close();
                pStatement.close();

                PreparedStatement pStatementMenu = connection.prepareStatement(
                        "insert into PartyMenu (restaurantId, partyFoodId) values (?, ?)");
                pStatementMenu.setString(1, restaurantId);
                pStatementMenu.setInt(2, partyFoodId);
                pStatementMenu.executeUpdate();
                pStatementMenu.close();
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
