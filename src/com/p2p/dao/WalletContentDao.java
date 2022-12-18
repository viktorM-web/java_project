package com.p2p.dao;

import com.p2p.dto.WalletContentFilter;
import com.p2p.entity.WalletContent;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;
import com.p2p.util.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WalletContentDao implements Dao<Integer, WalletContent> {
    private static final WalletContentDao INSTANCE = new WalletContentDao();
    private static final CryptoWalletDao CRYPTO_WALLET_DAO = CryptoWalletDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM wallet_content
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO wallet_content (id_number, cryptocurrency, amount)
            VALUES (?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE wallet_content
            SET 
                id_number = ?,
                cryptocurrency = ?,
                amount = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id,
                id_number,
                cryptocurrency,
                amount
            FROM wallet_content
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE wallet_content.id = ?
            """;

    private WalletContentDao() {
    }

    public List<WalletContent> findAll(WalletContentFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.idNumber() != null) {
            whereSql.add("id_number = ?");
            parameters.add(filter.idNumber());
        }
        if (filter.cryptocurrency() != null) {
            whereSql.add("currency = ?");
            parameters.add(filter.cryptocurrency().name());
        }
        if (filter.amountWithCondition() != null) {
            whereSql.add("amount " + filter.amountWithCondition().first() + " ?");
            parameters.add(filter.amountWithCondition().second());
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
            List<WalletContent> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createWalletContent(resultSet));
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
    public WalletContent save(WalletContent walletContent) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, walletContent.getIdNumber().getIdNumber());
            preparedStatement.setString(2, walletContent.getCryptocurrency().name());
            preparedStatement.setBigDecimal(3, walletContent.getAmount());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                walletContent.setId(generatedKeys.getInt("id"));
            }

            return walletContent;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(WalletContent walletContent) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, walletContent.getIdNumber().getIdNumber());
            preparedStatement.setString(2, walletContent.getCryptocurrency().name());
            preparedStatement.setBigDecimal(3, walletContent.getAmount());
            preparedStatement.setInt(4, walletContent.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<WalletContent> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<WalletContent> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            WalletContent walletContent = null;
            if (resultSet.next()) {
                walletContent = createWalletContent(resultSet);
            }
            return Optional.ofNullable(walletContent);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<WalletContent> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<WalletContent> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createWalletContent(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private WalletContent createWalletContent(ResultSet resultSet) throws SQLException {
        return new WalletContent(
                resultSet.getInt("id"),
                CRYPTO_WALLET_DAO.findById(resultSet.getString("id_number"),
                        resultSet.getStatement().getConnection()).orElse(null),
                Currency.getCurrency(resultSet.getString("cryptocurrency")).orElse(null),
                resultSet.getBigDecimal("amount")
        );
    }

    public static WalletContentDao getInstance() {
        return INSTANCE;
    }
}
