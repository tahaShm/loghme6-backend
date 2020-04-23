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
        dataSource.setPassword("Sph153153");

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
        } catch (SQLException e) {
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

}
