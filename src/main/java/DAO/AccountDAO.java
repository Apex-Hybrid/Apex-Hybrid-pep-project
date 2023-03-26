package DAO;

import Model.Account;
import Util.ConnectionUtil;
import exception.ResourceNotFoundException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {

    public List<Account> getAllAccounts() {
        Connection connection = ConnectionUtil.getConnection();
        List<Account> accounts = new ArrayList<>();
        try {
            //Write SQL logic here
            String sql = "SELECT * FROM Account";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Account account = new Account(rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return null;
    }

    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
//
            String sql = "INSERT INTO Account (username, password) VALUES (?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //write preparedStatement's setString method here.
            preparedStatement.setString(1, account.username);
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int account_id = (int) pkeyResultSet.getLong(1);
                account = new Account(account_id, account.getUsername(), account.getPassword());
            }
            return account;
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return null;
    }

    public Account login(String username, String password) {
        try {
            Connection connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM account WHERE username = ? and password = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResourceNotFoundException("Account was not found!");
            }
            return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserName(String username) {
        try {
            Connection connection = ConnectionUtil.getConnection();
            String sql = "SELECT username FROM account WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResourceNotFoundException("Account was not found!");
            }

            return rs.getString("username");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Account getAccountById(int id) {
        try {
            Connection connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResourceNotFoundException("Account was not found!");
            }

            return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
