package studio.asmussen.bank.sql;

import studio.asmussen.bank.core.Security;

import java.sql.*;

public class MySQL {

    public static final String HOST = "localhost";
    public static final String USER = "root";
    public static final String PASSWORD = "toor";
    public static final String DATABASE = "Bank";
    public static final int PORT = 3306;

    public Connection connection;

    public String firstName;
    public String lastName;
    public String email;

    public double balance;

    public void connect() {

        try {

            final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=true&requireSSL=true&useUnicode=true&characterEncoding=UTF-8";

            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
        }
    }

    public void disconnect() {

        try {

            connection.close();
            connection = null;

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public boolean isConnected() {

        return connection != null;
    }

    public boolean signUp(String firstName, String lastName, String email, String password, double balance) {

        if (!isConnected())

            return false;

        try {

            String hashedPassword = new Security().generateHash(password);

            PreparedStatement createAccount = connection.prepareStatement("INSERT INTO Bank.Accounts (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");

            createAccount.setString(1, firstName);
            createAccount.setString(2, lastName);
            createAccount.setString(3, email);
            createAccount.setString(4, hashedPassword);

            createAccount.execute();

            PreparedStatement createBalance = connection.prepareStatement("INSERT INTO Bank.Balances (account_id, balance) VALUES ((SELECT id FROM Bank.Accounts WHERE email = ?), ?)");

            createBalance.setString(1, email);
            createBalance.setDouble(2, balance);

            createBalance.execute();

            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;

            this.balance = balance;

            return true;

        } catch (SQLException e) {

            return false;
        }
    }

    public boolean logIn(String email, String password) {

        if (!isConnected())

            return false;

        Security security = new Security();

        try {

            PreparedStatement getAccount = connection.prepareStatement("SELECT first_name, last_name, email, password, balance FROM Bank.Accounts JOIN Balances b ON Accounts.id = b.account_id WHERE email = ?");

            getAccount.setString(1, email);

            getAccount.execute();

            ResultSet accountResultSet = getAccount.getResultSet();

            while (accountResultSet.next()) {

                String hashedPassword = accountResultSet.getString("password");

                if (security.compareHash(password, hashedPassword)) {

                    this.firstName = accountResultSet.getString("first_name");
                    this.lastName = accountResultSet.getString("last_name");
                    this.email = accountResultSet.getString("email");

                    this.balance = accountResultSet.getDouble("balance");

                    return true;
                }
            }

            return false;

        } catch (SQLException e) {

            return false;
        }
    }

    public boolean logOut() {

        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.balance = 0;

        return true;
    }

    public boolean updateBalance(String email, double amount) {

        if (!isConnected())

            return false;

        try {

            PreparedStatement updateBalance = connection.prepareStatement("UPDATE Bank.Balances SET balance = ? WHERE account_id = (SELECT id FROM Bank.Accounts WHERE email = ?)");

            updateBalance.setDouble(1, balance + amount);
            updateBalance.setString(2, email);

            updateBalance.execute();

            this.balance += amount;

            return true;

        } catch (SQLException e) {

            return false;
        }
    }
}
