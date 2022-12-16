package com.p2p.dao;

import com.p2p.entity.Card;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;
import com.p2p.util.Currency;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDao implements Dao<Long, Card> {

    private static final CardDao INSTANCE = new CardDao();
    private static final UserDao USER_DAO = UserDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM card
            WHERE id_number = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO card (id_number, owner, validity, balance, currency)
            VALUES (?, ?, ?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE card
            SET validity = ?,
                balance = ?,
                currency = ?,
                owner = ?
                
            WHERE id_number = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id_number,
                owner,
                validity,
                balance,
                currency
            FROM card
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE card.id_number=?
            """;

    private CardDao() {
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Card save(Card card) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, card.getIdNumber());
            preparedStatement.setInt(2, card.getOwner().getId());
            preparedStatement.setDate(3, Date.valueOf(card.getValidity()));
            preparedStatement.setBigDecimal(2, card.getBalance());
            preparedStatement.setObject(4, card.getCurrency().name());

            preparedStatement.executeUpdate();

            return card;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(Card card) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setDate(1, Date.valueOf(card.getValidity()));
            preparedStatement.setBigDecimal(2, card.getBalance());
            preparedStatement.setObject(3, card.getCurrency().name());
            preparedStatement.setInt(4, card.getOwner().getId());
            preparedStatement.setLong(5, card.getIdNumber());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<Card> findById(Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Card> findById(Long id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Card card = null;
            if (resultSet.next()) {
                card = createCard(resultSet);
            }
            return Optional.ofNullable(card);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<Card> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Card> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createCard(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private Card createCard(ResultSet resultSet) throws SQLException {
        return new Card(
                resultSet.getLong("id_number"),
                USER_DAO.findById(resultSet.getInt("owner"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getDate("validity").toLocalDate(),
                resultSet.getBigDecimal("balance"),
                Currency.getCurrency(resultSet.getString("currency")).orElse(null)
        );
    }

    public static CardDao getInstance() {
        return INSTANCE;
    }
}
