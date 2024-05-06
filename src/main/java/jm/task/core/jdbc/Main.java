package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {

        UserService userService = new UserServiceImpl();

        userService.createUsersTable();

        userService.saveUser("Grigoriy", "Fedorov", (byte) 28);
        userService.saveUser("Джеймс", "Гослинг", (byte) 69);
        userService.saveUser("Павел", "Дуров", (byte) 39);
        userService.saveUser("Илон", "Маск", (byte) 52);

        userService.getAllUsers();
        userService.cleanUsersTable();
        userService.dropUsersTable();
    }
}