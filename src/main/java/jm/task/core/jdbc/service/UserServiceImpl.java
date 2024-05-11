package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.model.User;

import java.util.List;

import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

public class UserServiceImpl implements UserService {
    // поправил инкапсуляцию и сделал его private
    // использовал интерфейс UserDao, вместо конкретной реализации UserDaoHidernateImpl
    private final UserDao dao = new UserDaoHibernateImpl();

    @Override
    public void createUsersTable() {
        dao.createUsersTable();
    }

    @Override
    public void dropUsersTable() {
        dao.dropUsersTable();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        dao.saveUser(name, lastName, age);
        Logger logger = getLogger(User.class.getName());
        String message = String.format("User с именем — %s добавлен в базу данных", name);
        logger.info(message);
    }

    @Override
    public void removeUserById(long id) {
        dao.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    @Override
    public void cleanUsersTable() {
        dao.dropUsersTable();
    }
}