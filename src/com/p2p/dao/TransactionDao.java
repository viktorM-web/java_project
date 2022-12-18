package com.p2p.dao;

import com.p2p.dto.TransactionFilter;
import com.p2p.entity.Transaction;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;
import com.p2p.util.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionDao implements Dao<Integer, Transaction> {
    private static final TransactionDao INSTANCE = new TransactionDao();
    private static final UserDao USER_DAO = UserDao.getInstance();
    private static final OfferDao OFFER_DAO = OfferDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM transaction
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO transaction (offer, consumer, status)
            VALUES (?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE transaction
            SET offer = ?,
                consumer = ?,
                status = ?
                
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id,
                offer,
                consumer,
                status
                
            FROM transaction
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE transaction.id=?
            """;

    private TransactionDao() {
    }

    public List<Transaction> findAll(TransactionFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.offer() != null) {
            whereSql.add("offer = ?");
            parameters.add(filter.offer().getId());
        }
        if (filter.consumer() != null) {
            whereSql.add("consumer = ?");
            parameters.add(filter.consumer().getId());
        }
        if (filter.status() != null) {
            whereSql.add("status = ?");
            parameters.add(filter.status().name());
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
            List<Transaction> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createTransaction(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
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
    public Transaction save(Transaction transaction) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, transaction.getOffer().getId());
            preparedStatement.setInt(2, transaction.getConsumer().getId());
            preparedStatement.setString(3, transaction.getStatus().name());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                transaction.setId(generatedKeys.getInt("id"));
            }
            return transaction;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(Transaction transaction) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, transaction.getOffer().getId());
            preparedStatement.setInt(2, transaction.getConsumer().getId());
            preparedStatement.setString(3, transaction.getStatus().name());
            preparedStatement.setInt(4, transaction.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<Transaction> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private Optional<Transaction> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Transaction transaction = null;
            if (resultSet.next()) {
                transaction = createTransaction(resultSet);
            }
            return Optional.ofNullable(transaction);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<Transaction> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Transaction> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createTransaction(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private Transaction createTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(
                resultSet.getInt("id"),
                OFFER_DAO.findById(resultSet.getInt("offer"),
                        resultSet.getStatement().getConnection()).orElse(null),
                USER_DAO.findById(resultSet.getInt("consumer"),
                        resultSet.getStatement().getConnection()).orElse(null),
                Status.getStatus(resultSet.getString("status")).orElse(null)
        );
    }

    public static TransactionDao getInstance() {
        return INSTANCE;
    }
}
