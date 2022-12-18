package com.p2p.dao;

import com.p2p.dto.UserFilter;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;
import com.p2p.entity.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDao implements Dao<Integer, User> {

    private static final UserDao INSTANCE = new UserDao();

    private static final String DELETE_SQL = """
            DELETE 
            FROM user_p2p
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO user_p2p (first_name, last_name, number_passport, email, number_telephone, admin)
            VALUES (?, ?, ?, ?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE user_p2p
            SET first_name = ?,
                last_name = ?,
                number_passport = ?,
                email = ?,
                number_telephone = ?,
                admin = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id,
                first_name,
                last_name,
                number_passport,
                email,
                number_telephone,
                admin
            FROM user_p2p
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE user_p2p.id=?
            """;

    private UserDao() {
    }


    @Override
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public User save(User user) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getNumberPassport());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getNumberTelephone());
            preparedStatement.setBoolean(6, user.getAdmin());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt("id"));
            }
            return user;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(User user) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getNumberPassport());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getNumberTelephone());
            preparedStatement.setBoolean(6, user.getAdmin());
            preparedStatement.setInt(7, user.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public List<User> findAll(UserFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.firstName() != null) {
            whereSql.add("first_name LIKE ?");
            parameters.add("%" + filter.firstName() + "%");
        }
        if (filter.lastName() != null) {
            whereSql.add("last_name LIKE ?");
            parameters.add("%" + filter.lastName() + "%");
        }
        if (filter.email() != null) {
            whereSql.add("email LIKE ?");
            parameters.add("%" + filter.email() + "%");
        }
        if (filter.numberTelephone() != null) {
            whereSql.add("number_telephone LIKE ?");
            parameters.add("%" + filter.numberTelephone() + "%");
        }
        if (filter.admin() != null) {
            whereSql.add("admin = ?");
            parameters.add(filter.admin());
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var where = whereSql.stream()
                .collect(Collectors.joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));
        if (whereSql.isEmpty()) {
            where = where.replaceAll("WHERE", "");
        }
        var sql = FIND_ALL_SQL + where;
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = preparedStatement.executeQuery();
            List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createUser(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<User> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createUser(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<User> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = createUser(resultSet);
            }
            return Optional.ofNullable(user);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private User createUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("number_passport"),
                resultSet.getString("email"),
                resultSet.getString("number_telephone"),
                resultSet.getBoolean("admin")
        );
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
