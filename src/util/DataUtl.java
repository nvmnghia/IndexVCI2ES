package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import config.Config;

import java.sql.*;

public class DataUtl {
    private static Connection connection = null;

    static {
        try {
            connection = createNewConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stm = connection.createStatement()) {
            stm.execute("SET GLOBAL group_concat_max_len=10240");
            System.out.println("SET GLOBAL group_concat_max_len=10240");
        } catch (SQLException e) {
            System.err.println("CANNOT SET group_concat_max_len=10240");
            e.printStackTrace();
        }
    }

    public static Connection getDBConnection() throws SQLException {
        if (connection == null) {
            connection = createNewConnection();
        }

        return connection;
    }

    public static Statement getDBStatement() throws SQLException {
        if (statement == null) {
            statement = getDBConnection().createStatement();
        }

        return statement;
    }

    private static Statement statement = null;

    public static ResultSet queryDB(String dbName, String query) throws SQLException {
        Statement stm = getDBStatement();

        stm.executeQuery("USE " + dbName);
        return stm.executeQuery(query);
    }

    public static Connection createNewConnection() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setUseUnicode(true);
        dataSource.setEncoding("UTF-8");
        dataSource.setServerName(Config.DB.HOST);
        dataSource.setPort(Config.DB.PORT);
        dataSource.setUser(Config.DB.USERNAME);
        dataSource.setPassword(Config.DB.PASSWORD);

        return dataSource.getConnection();
    }

    public static ResultSet queryDB(Statement statement, String dbName, String query) throws SQLException {
        statement.executeQuery("USE " + dbName);
        return statement.executeQuery(query);
    }
}
