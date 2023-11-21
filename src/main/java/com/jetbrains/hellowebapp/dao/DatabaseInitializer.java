package com.jetbrains.hellowebapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String URL = "jdbc:postgresql://localhost:5432/jerseydb2";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            createCompanyTable(connection);
//            createDepartmentTable(connection);
//            createEmployeeTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void createCompanyTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS company (id SERIAL not null, name VARCHAR(255) not null, PRIMARY KEY (id))");
        }
    }

    private static void createDepartmentTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS department (\n" +
                    "    id SERIAL not null,\n" +
                    "    name VARCHAR(255) not null,\n" +
                    "    company_id INTEGER not null,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    FOREIGN KEY (company_id) REFERENCES company(id),\n" +
                    "    UNIQUE (name)\n" +
                    ")");
        }
    }


    private static void createEmployeeTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS employee (\n" +
                    "    id SERIAL not null,\n" +
                    "    name VARCHAR(255) not null unique,\n" +
                    "    department_id INTEGER not null,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    FOREIGN KEY (department_id) REFERENCES department(id),\n" +
                    "    UNIQUE (name)\n" +
                    ")");
        }
    }
}
