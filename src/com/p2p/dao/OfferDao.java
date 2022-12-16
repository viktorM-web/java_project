package com.p2p.dao;

import com.p2p.dto.OfferFilter;
import com.p2p.entity.Offer;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;
import com.p2p.util.Currency;
import com.p2p.util.Operation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OfferDao implements Dao<Integer, Offer>{

    private static final OfferDao INSTANCE = new OfferDao();
    private static final UserDao USER_DAO = UserDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM offer
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO offer (supplier, sum, currency, price, expected_currency, publication,operation)
            VALUES (?, ?, ?, ?, ?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE offer
            SET supplier = ?,
                sum = ?,
                currency = ?,
                price = ?,
                expected_currency = ?,
                publication = ?,
                operation = ?
                
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id,
                supplier,
                sum,
                currency,
                price,
                expected_currency,
                publication,
                operation
            FROM offer
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE offer.id=?
            """;

    private OfferDao() {
    }

    public List<Offer> findAll(OfferFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.supplier() != null) {
            whereSql.add("supplier = ?");
            parameters.add(filter.supplier().getId());
        }
        if (filter.sumWithCondition() != null) {
            whereSql.add("sum " + filter.sumWithCondition().first() + " ?");
            parameters.add(filter.sumWithCondition().second());
        }
        if (filter.currency() != null) {
            whereSql.add("currency = ?");
            parameters.add(filter.currency().name());
        }
        if (filter.priceWithCondition() != null) {
            whereSql.add("price " + filter.priceWithCondition().first() + " ?");
            parameters.add(filter.priceWithCondition().second());
        }
        if (filter.expectedCurrency() != null) {
            whereSql.add("expected_currency = ?");
            parameters.add(filter.expectedCurrency().name());
        }
        if (filter.publicationWithCondition() != null) {
            whereSql.add("publication " + filter.publicationWithCondition().first() + " ?");
            parameters.add(filter.publicationWithCondition().second());
        }
        if (filter.operation() != null) {
            whereSql.add("operation = ?");
            parameters.add(filter.operation().name());
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
            List<Offer> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createOffer(resultSet));
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
    public Offer save(Offer offer) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, offer.getSupplier().getId());
            preparedStatement.setBigDecimal(2, offer.getSum());
            preparedStatement.setString(3, offer.getCurrency().name());
            preparedStatement.setBigDecimal(4, offer.getPrice());
            preparedStatement.setString(5, offer.getExpectedCurrency().name());
            preparedStatement.setDate(6, Date.valueOf(offer.getPublication()));
            preparedStatement.setString(7, offer.getOperation().name());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                offer.setId(generatedKeys.getInt("id"));
            }
            return offer;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(Offer offer) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, offer.getSupplier().getId());
            preparedStatement.setBigDecimal(2, offer.getSum());
            preparedStatement.setString(3, offer.getCurrency().name());
            preparedStatement.setBigDecimal(4, offer.getPrice());
            preparedStatement.setString(5, offer.getExpectedCurrency().name());
            preparedStatement.setDate(6, Date.valueOf(offer.getPublication()));
            preparedStatement.setString(7, offer.getOperation().name());
            preparedStatement.setInt(8, offer.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<Offer> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Offer> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Offer offer = null;
            if (resultSet.next()) {
                offer = createOffer(resultSet);
            }
            return Optional.ofNullable(offer);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<Offer> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Offer> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createOffer(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private Offer createOffer(ResultSet resultSet) throws SQLException {
        return new Offer(
                resultSet.getInt("id"),
                USER_DAO.findById(resultSet.getInt("supplier"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getBigDecimal("sum"),
                Currency.getCurrency(resultSet.getString("currency")).orElse(null),
                resultSet.getBigDecimal("price"),
                Currency.getCurrency(resultSet.getString("Expected_currency")).orElse(null),
                resultSet.getDate("publication").toLocalDate(),
                Operation.getOperation(resultSet.getString("operation")).orElse(null)

        );
    }

    public static OfferDao getInstance(){
        return INSTANCE;
    }
}
