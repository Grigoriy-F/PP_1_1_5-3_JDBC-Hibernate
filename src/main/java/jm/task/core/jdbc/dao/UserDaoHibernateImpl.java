package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoHibernateImpl implements UserDao {
    private static final SessionFactory sessionFactory = Util.Hibernate.getSessionFactory();
    private static final Logger logger = Logger.getLogger(UserDaoHibernateImpl.class.getName());

    // rollback() вызывается только в случае, если произошла ошибка при выполнении DML операций (изменяющих данные)
    // — добавлении пользователя, удалении пользователя и очистке таблицы.
    // saveUser(String name, String lastName, byte age) - добавление нового пользователя.
    // removeUserById(long id) - удаление пользователя по идентификатору.
    // cleanUsersTable() - очистка таблицы пользователей (удаление всех записей из таблицы).


    // убрал rollback() и соответственно вложенный блок try-catch больше не нужен,
    @Override
    public void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS User ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "lastName VARCHAR(255),"
                + "age TINYINT"
                + ")";

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createTableSQL).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Не удалось создать таблицу", e);
        }
    }

    // вызов rollback() удалён
    @Override
    public void dropUsersTable() {
        String dropTableSQL = "DROP TABLE User";

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(dropTableSQL).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Не удалось удалить таблицу", e);
        }
    }


    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User user = new User(name, lastName, age);
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            String message = String.format("Не удалось добавить пользователя с именем %s", name);
            logger.log(Level.SEVERE, message, e);
        }
    }

    // добавил проверку метода get, что он возвращает объект пользователя, перед его удалением и коммит транзакции
    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
            } else {
                logger.log(Level.SEVERE, String.format("Пользователь с id - %d не найден", id));
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, String.format("Не удалось удалить пользователя с id - %d", id), e);
        }
    }

    // rollback() удалён из метода getAllUsers()
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            users = session.createQuery("SELECT u from User u", User.class).getResultList();
            transaction.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Не удалось получить всех пользователей", e);
        }
        return users;
    }

    // блок try-with-resources автоматически закрывает объект Session
    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.createQuery("DELETE FROM User").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, "Не удалось очистить таблицу", e);
        }
    }
}