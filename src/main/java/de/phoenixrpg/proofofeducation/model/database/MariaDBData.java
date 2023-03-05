package de.phoenixrpg.proofofeducation.model.database;

import de.phoenixrpg.proofofeducation.controller.proof.DailyProof;
import de.phoenixrpg.proofofeducation.model.DataManager;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.*;
import java.util.LinkedHashMap;

public class MariaDBData implements DataManager {

    private static MariaDBData database;

    private String host;
    private int port;
    private String databaseName;
    private String user;
    private String password;


    private Connection connection;
    private PreparedStatement preparedStatement;

    private MariaDBData(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
    }

    public static MariaDBData getDatabase(String host, int port, String databaseName, String user, String password) {
        if (database == null) database = new MariaDBData(host, port, databaseName, user, password);

        return database;
    }

    @Override
    public LinkedHashMap<String, DailyProof> getWeek(long discordId, LocalDate date) {
        connect();

        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM Proof WHERE User_ID = (SELECT User_ID FROM User WHERE Discord_ID = ?) AND  Date BETWEEN ? AND ? ORDER BY Date ASC;");

            preparedStatement.setLong(1, discordId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(weekStart));
            preparedStatement.setDate(3, java.sql.Date.valueOf(weekEnd));

            ResultSet resultSet = preparedStatement.executeQuery();

            LinkedHashMap<String, DailyProof> dailyProofs = new LinkedHashMap<>();
            while(resultSet.next()) {
                LocalDate localDate = Instant.ofEpochMilli(resultSet.getDate("Date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

                dailyProofs.put(localDate.toString(), new DailyProof(resultSet.getString("Task"), resultSet.getString("Location"), localDate));
            }

            close();
            return dailyProofs;
        }  catch (SQLException e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    @Override
    public String getNameById(long discordId) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("SELECT Name FROM User WHERE Discord_ID = ?");
            preparedStatement.setLong(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();

            String name = null;
            if (resultSet.next()) {
                name = resultSet.getString("Name");
            }

            close();
            return name;
        } catch (SQLException e) {
            close();
            return null;
        }
    }

    @Override
    public LocalDate getYearById(long discordId) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("SELECT Year FROM User WHERE Discord_ID = ?");
            preparedStatement.setLong(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();

            LocalDate localDate = null;
            if (resultSet.next()) {
                java.sql.Date date = resultSet.getDate("Year");
                localDate = LocalDate.of((date.getYear()+1900), (date.getMonth()+1), date.getDate());
            }

            close();
            return localDate;
        } catch (SQLException e) {
            close();
            return null;
        }
    }

    @Override
    public boolean insertProof(long discordId, String location, String task, LocalDate date) {
        if(!existUser(discordId)) return false;
        if(existProof(discordId, date)) return updateProof(discordId, date, task, location);

        connect();
            try {

                preparedStatement = connection.prepareStatement("INSERT INTO Proof(User_ID, Date, Task, Location) VALUES((SELECT User_ID FROM User WHERE Discord_ID = ?), ?, ?, ?);");
                preparedStatement.setLong(1, discordId);
                preparedStatement.setDate(2, new java.sql.Date(date.getYear()-1900, date.getMonthValue()-1, date.getDayOfMonth()));
                preparedStatement.setString(3, task);
                preparedStatement.setString(4, location);

                preparedStatement.execute();
                close();
                return true;
            } catch (SQLException e) {
                close();
                return false;
            }
    }
    @Override
    public boolean updateProof(long discordId, LocalDate date, String task, String location) {
            connect();
            try {
                preparedStatement = connection.prepareStatement("UPDATE Proof SET Task = ?, Location = ? WHERE User_ID = (SELECT User_ID FROM User WHERE Discord_ID = ?) AND Date = ?;");

                preparedStatement.setString(1, task);
                preparedStatement.setString(2, location);
                preparedStatement.setLong(3, discordId);
                preparedStatement.setDate(4, new java.sql.Date(date.getYear()-1900, date.getMonthValue()-1, date.getDayOfMonth()));

                preparedStatement.execute();
                close();
                return true;
            } catch (SQLException e) {
                close();
                return false;
            }
    }

    @Override
    public boolean deleteProof(long discordId, LocalDate date) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM Proof WHERE User_ID = (SELECT User_ID FROM User WHERE Discord_ID = ?) AND Date = ?");
            preparedStatement.setLong(1, discordId);
            preparedStatement.setDate(2, new java.sql.Date(date.getYear()-1900, date.getMonthValue()-1, date.getDayOfMonth()));
            preparedStatement.execute();
            close();
            return false;
        } catch (SQLException e) {
            close();
            return false;
        }
    }
    @Override
    public boolean existProof(long discordId, LocalDate date) {

        connect();
        try {
            preparedStatement = connection.prepareStatement("SELECT Proof_ID FROM Proof WHERE User_ID = (SELECT User_ID FROM User WHERE Discord_ID = ?) AND Date = ?");
            preparedStatement.setLong(1, discordId);

            java.sql.Date sqlDate = new java.sql.Date(date.getYear()-1900, date.getMonthValue()-1, date.getDayOfMonth());
            preparedStatement.setDate(2, sqlDate);

            ResultSet resultSet = preparedStatement.executeQuery();

           boolean exists = resultSet.next();
            close();
            return exists;
        } catch (SQLException e) {
            close();
            return false;
        }
    }

    @Override
    public DailyProof getProofByDate(long discordId, LocalDate date) {
        connect();

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM Proof WHERE User_ID = (SELECT User_ID FROM User WHERE Discord_ID = ?) AND Date = ?;");
            preparedStatement.setLong(1, discordId);
            preparedStatement.setDate(2, new java.sql.Date(date.getYear()-1900, date.getMonthValue()-1, date.getDayOfMonth()));

            ResultSet resultSet = preparedStatement.executeQuery();

            DailyProof dailyProof = null;
            while(resultSet.next()) {
                LocalDate localDate = Instant.ofEpochMilli(resultSet.getDate("Date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                dailyProof = new DailyProof(resultSet.getString("Task"), resultSet.getString("Location"), localDate);
            }

            close();
            return dailyProof;
        }  catch (SQLException e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    @Override
    public boolean insertUser(long discordId, String name, LocalDate year) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO User(Discord_ID, Name, Year) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE Discord_ID = Discord_ID, Name = ?, Year = ?;");

            preparedStatement.setLong(1, discordId);
            preparedStatement.setString(2, name);
            preparedStatement.setDate(3, new java.sql.Date(year.getYear()-1900, year.getMonthValue()-1, year.getDayOfMonth()));
            preparedStatement.setString(4, name);
            preparedStatement.setDate(5, new java.sql.Date(year.getYear()-1900, year.getMonthValue()-1, year.getDayOfMonth()));

            preparedStatement.execute();
            close();
            return true;
        } catch (SQLException e) {
            close();
            return false;
        }
    }

    @Override
    public boolean existUser(long discordId) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("SELECT Discord_ID FROM User WHERE Discord_ID = ?");
            preparedStatement.setLong(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                close();
                return true;
            }

            close();
            return false;
        } catch (SQLException e) {
            close();
            return false;
        }
    }

    @Override
    public int getUserByDiscordID(long discordId) {
        connect();
        try {
            preparedStatement = connection.prepareStatement("SELECT User_ID FROM User WHERE Discord_ID = ?");
            preparedStatement.setLong(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("User_ID");
            }

            close();
            return -1;
        } catch (SQLException e) {
            close();
            return -1;
        }
    }

    public boolean createDataholder() {
        connect();
        try {
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS User(User_ID int NOT NULL AUTO_INCREMENT PRIMARY KEY, Discord_ID bigint NOT NULL UNIQUE, Name varchar(128) NOT NULL, Year tinyint(2) NOT NULL); " +
            "CREATE TABLE IF NOT EXISTS Proof(Proof_ID int NOT NULL AUTO_INCREMENT PRIMARY KEY, User_ID int NOT NULL, Date date NOT NULL, Task varchar(256) NOT NULL, Location varchar(128) NOT NULL, FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE);");
            preparedStatement.execute();
            close();
            return true;
        } catch (SQLException e) {
            close();
            return false;
        }
    }


    public void connect() {
        try {

            if (connection != null && !connection.isClosed()) return;
            connection = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + databaseName + "?allowMultiQueries=true", user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: cant connect to the database");
        }
    }


    public void close() {
        try {
            connection.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: cant close connection");
        }
    }
}
