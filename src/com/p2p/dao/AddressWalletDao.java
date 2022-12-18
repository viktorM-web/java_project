package com.p2p.dao;

import com.p2p.entity.AddressWallet;
import com.p2p.exeption.DaoException;
import com.p2p.util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressWalletDao implements Dao<Integer, AddressWallet> {
    private static final AddressWalletDao INSTANCE = new AddressWalletDao();
    private static final WalletContentDao WALLET_CONTENT_DAO = WalletContentDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE 
            FROM address_wallet
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO address_wallet (id_wallet_content, net, address)
            VALUES (?, ?, ?)           
            """;
    private static final String UPDATE_SQL = """
            UPDATE address_wallet
            SET 
                id_wallet_content = ?,
                net = ?,
                address = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
                id,
                id_wallet_content,
                net,
                address
            FROM address_wallet
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE address_wallet.id = ?
            """;

    private AddressWalletDao() {
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
    public AddressWallet save(AddressWallet addressWallet) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, addressWallet.getIdWalletContent().getId());
            preparedStatement.setString(2, addressWallet.getNet());
            preparedStatement.setString(3, addressWallet.getAddress());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                addressWallet.setId(generatedKeys.getInt("id"));
            }

            return addressWallet;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public void update(AddressWallet addressWallet) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setInt(1, addressWallet.getIdWalletContent().getId());
            preparedStatement.setString(2, addressWallet.getNet());
            preparedStatement.setString(3, addressWallet.getAddress());
            preparedStatement.setInt(4, addressWallet.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<AddressWallet> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<AddressWallet> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            AddressWallet addressWallet = null;
            if (resultSet.next()) {
                addressWallet = createAddressWallet(resultSet);
            }
            return Optional.ofNullable(addressWallet);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<AddressWallet> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<AddressWallet> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(createAddressWallet(resultSet));
            }
            return result;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private AddressWallet createAddressWallet(ResultSet resultSet) throws SQLException {
        return new AddressWallet(
                resultSet.getInt("id"),
                WALLET_CONTENT_DAO.findById(resultSet.getInt("id_wallet_content"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getString("net"),
                resultSet.getString("address")
        );
    }

    public static AddressWalletDao getInstance() {
        return INSTANCE;
    }
}
