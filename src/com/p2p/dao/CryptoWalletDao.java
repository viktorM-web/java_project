package com.p2p.dao;

import com.p2p.dto.CryptoWalletFilter;
import com.p2p.entity.CryptoWallet;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CryptoWalletDao implements Dao<String, CryptoWallet> {
    private static final CryptoWalletDao INSTANCE = new CryptoWalletDao();
    private static final UserDao USER_DAO = UserDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM crypto_wallet
            WHERE id_number = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO crypto_wallet (id_number, owner)
            VALUES (?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE crypto_wallet
            SET 
                owner = ?
                
            WHERE id_number = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id_number,
                owner
            FROM crypto_wallet
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE crypto_wallet.id_number=?
            """;

    private CryptoWalletDao() {
    }

    public List<CryptoWallet> findAll(CryptoWalletFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.owner() != null) {
            whereSql.add("owner = ?");
            parameters.add(filter.owner().getId());
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
            List<CryptoWallet> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createCryptoWallet(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public boolean delete(String id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setString(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public CryptoWallet save(CryptoWallet cryptoWallet) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setString(1, cryptoWallet.getIdNumber());
            preparedStatement.setInt(2, cryptoWallet.getOwner().getId());

            preparedStatement.executeUpdate();

            return cryptoWallet;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(CryptoWallet cryptoWallet) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, cryptoWallet.getOwner().getId());
            preparedStatement.setString(2, cryptoWallet.getIdNumber());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<CryptoWallet> findById(String id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<CryptoWallet> findById(String id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setString(1, id);
            var resultSet = preparedStatement.executeQuery();
            CryptoWallet cryptoWallet = null;
            if (resultSet.next()) {
                cryptoWallet = createCryptoWallet(resultSet);
            }
            return Optional.ofNullable(cryptoWallet);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<CryptoWallet> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<CryptoWallet> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createCryptoWallet(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private CryptoWallet createCryptoWallet(ResultSet resultSet) throws SQLException {
        return new CryptoWallet(
                resultSet.getString("id_number"),
                USER_DAO.findById(resultSet.getInt("owner"),
                        resultSet.getStatement().getConnection()).orElse(null)
        );
    }

    public static CryptoWalletDao getInstance() {
        return INSTANCE;
    }
}
