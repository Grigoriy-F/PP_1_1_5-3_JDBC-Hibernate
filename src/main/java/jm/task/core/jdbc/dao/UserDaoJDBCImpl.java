package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserDaoJDBCImpl implements UserDao {
    private static final Connection connection = Util.MySql.getConnection();
    private static final Logger logger = Logger.getLogger(UserDaoJDBCImpl.class.getName());


    @Override
    public void createUsersTable() {
        // команда создания таблицы
        String createTableSQL = "CREATE TABLE IF NOT EXISTS User ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "lastName VARCHAR(255),"
                + "age TINYINT"
                + ")";

        try (Connection connection = Util.MySql.getConnection();
             Statement statement = connection.createStatement()) {
            // создание таблицы
            statement.execute(createTableSQL);
            logger.info("Таблица пользователей создана успешно");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Не удалось создать таблицу \n", e);
        }
    }


    @Override
    public void dropUsersTable() {
        String createTableSQL = "DROP TABLE IF EXISTS USER";

        try (Connection connection = Util.MySql.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Не удалось удалить таблицу 2 \n", e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        String insertSQL = "INSERT INTO User (name, lastName, age) VALUES (?, ?, ?)";

        try (Connection connection = Util.MySql.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.executeUpdate();
            System.out.println("User с именем " + name + " добавлен в базу данных");
        } catch (SQLException e) {
            String message = String.format("User с именем — %s не добавлен в базу данных", name);
            logger.log(Level.SEVERE, message, e);
        }
    }

    @Override
    public void removeUserById(long id) {
        String deleteSQL = "DELETE FROM User WHERE id = ?";

        try (Connection connection = Util.MySql.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Не удалось удалить User'a с id - %d", id);
            logger.log(Level.SEVERE, message, e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // получаем все поля с таблицы USER
        String selectSQL = "SELECT * FROM User";

        try (Connection connection = Util.MySql.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectSQL);

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setAge(resultSet.getByte("age"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastname"));
                users.add(user);
            }
        } catch (SQLException e) {
            logger.info("Неудалось получить всех User'ов \n");
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        String truncateSQL = "TRUNCATE TABLE User";

        try (Connection connection = Util.MySql.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(truncateSQL);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при очистке таблицы", e);
        }
    }
}
