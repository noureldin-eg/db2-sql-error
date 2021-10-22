package com.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.db2.jcc.DB2PreparedStatement;

public class App {
    private static final String USER = "db2inst1";
    private static final String PASSWORD = "password";

    private static final String SERVER = "db2"; // "localhost";
    private static final String PORT = "50000";
    private static final String DATABASE = "bludb";
    private static final String URL = "jdbc:db2://" + SERVER + ":" + PORT + "/" + DATABASE + ":"
    // + "sslConnection=true;"
            + "enableNamedParameterMarkers=1;" + "traceFile=db2.log;";

    public static void main(String[] args) {
        // @formatter:off
        String SQL = "SELECT *" 
                + " FROM ( VALUES (10,'A'),(20,'B'),(30,'C'),(40,'D') ) AS T(COL1,COL2)"
                + " WHERE T.COL2 = CAST (:PARAM AS VARCHAR(3)) OR :PARAM = 'ALL'";
        // @formatter:on
        String PARAM = "ALL";

        // SQL = SQL.replaceAll(":PARAM", "'" + PARAM + "'"); // Uncomment me

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                DB2PreparedStatement statement = (DB2PreparedStatement) connection.prepareStatement(SQL)) {

            print(connection.getMetaData());
            print(statement.getParameterMetaData());

            statement.setJccStringAtName("PARAM", PARAM); // Comment me
            try (ResultSet resultSet = statement.executeQuery()) {
                print(resultSet);
            }
        } catch (SQLException exception) {
            print(exception);
        }
    }

    private static void print(DatabaseMetaData metaData) throws SQLException {
        String productName = metaData.getDatabaseProductName();
        System.out.println("Database Product Name: " + productName);
        String productVersion = metaData.getDatabaseProductVersion();
        System.out.println("Database Product Version: " + productVersion);
        int databaseMajorVersion = metaData.getDatabaseMajorVersion();
        int databaseMinorVersion = metaData.getDatabaseMinorVersion();
        System.out.println("Database Version: " + databaseMajorVersion + "." + databaseMinorVersion);
        String driverName = metaData.getDriverName();
        System.out.println("Driver Name: " + driverName);
        String driverVersion = metaData.getDriverVersion();
        System.out.println("Driver Version: " + driverVersion);
        int jdbcMajorVersion = metaData.getJDBCMajorVersion();
        int jdbcMinorVersion = metaData.getJDBCMinorVersion();
        System.out.println("JDBC Version: " + jdbcMajorVersion + "." + jdbcMinorVersion);
    }

    private static void print(ParameterMetaData parameterMetaData) throws SQLException {
        int parameterCount = parameterMetaData.getParameterCount();
        System.out.println("Number of statement parameters: " + parameterCount);
        for (int i = 1; i <= parameterCount; i++) {
            String sqlType = parameterMetaData.getParameterTypeName(i);
            int precision = parameterMetaData.getPrecision(i);
            System.out.printf("SQL type of parameter %d is %s(%d)%n", i, sqlType, precision);
        }
    }

    private static void print(ResultSet resultSet) throws SQLException {
        System.out.println("Results:");
        System.out.println("COL1  COL2");
        System.out.println("----  ----");
        while (resultSet.next()) {
            int col1 = resultSet.getInt("COL1");
            String col2 = resultSet.getString("COL2");
            System.out.printf("%4d  %-4s%n", col1, col2);
        }
    }

    private static void print(SQLException exception) {
        System.err.println("SQLException information:");
        while (exception != null) {
            System.err.println("Error msg: " + exception.getMessage());
            System.err.println("SQLSTATE: " + exception.getSQLState());
            System.err.println("Error code: " + exception.getErrorCode());
            exception.printStackTrace();
            // For drivers that support chained exceptions
            exception = exception.getNextException();
        }
    }
}
