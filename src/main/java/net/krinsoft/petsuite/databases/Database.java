package net.krinsoft.petsuite.databases;

import net.krinsoft.petsuite.PetCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @author krinsdeath
 */
public class Database {

    private enum Type {
        MySQL("com.mysql.jdbc.Driver"),
        SQLite("org.sqlite.JDBC");
        private String driver;

        Type(String driver) {
            this.driver = driver;
        }

        public String getDriver() {
            return this.driver;
        }

        public static Type forName(String name) throws TypeNotPresentException {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            throw new TypeNotPresentException(name, new Throwable("Available types: " + Arrays.toString(values())));
        }
    }

    private PetCore plugin;
    private Type type;

    private Connection connection;

    public Database(PetCore instance) {
        this.plugin     = instance;

        try {
            type = Type.forName(plugin.getConfig().getString("database.type"));
        } catch (TypeNotPresentException e) {
            e.printStackTrace();
            plugin.debug("Defaulting to SQLite...");
            type = Type.SQLite;
        }
        if (connect()) {
            buildDatabase();
        } else {
            plugin.debug("An error occurred while trying to connect to the database. Check your config.yml.");
        }
    }

    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
            Class.forName(type.getDriver());
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            String connURL = "jdbc:" + type.name().toLowerCase() + ":" + getDatabasePath();
            Connection connection = DriverManager.getConnection(connURL);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getDatabasePath() {
        String dbpath = "";
        switch (type) {
            case SQLite:
                dbpath += plugin.getDataFolder().toString();
                dbpath += "/petsuite.db";
                break;
            case MySQL:
                dbpath += "//" + plugin.getConfig().getString("database.hostname", "localhost");
                dbpath += ":" + plugin.getConfig().getInt("database.port", 3306);
                dbpath += "/" + plugin.getConfig().getString("database.name", "petsuite");
                break;
            default:
                dbpath = plugin.getDataFolder().toString();
                dbpath += "/petsuite.db";
        }
        return dbpath;
    }

    private void buildDatabase() {
        try {
            String createBase = "CREATE TABLE IF NOT EXISTS petsuite_base (" +
                    "id INTEGER UNIQUE AUTO_INCREMENT, " +
                    "pet_uuid TEXT UNIQUE NOT NULL, " +
                    "owner TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "level INTEGER" +
                    ");";
            String createSkill = "CREATE TABLE IF NOT EXISTS petsuite_skills (" +
                    "pet_id INTEGER, " +
                    "skill INTEGER, " +
                    "level INTEGER, " +
                    "FOREIGN KEY (pet_id) REFERENCES petsuite_base(id)" +
                    ");";
            Statement state = connection.createStatement();
            state.executeUpdate(createBase);
            state.executeUpdate(createSkill);
            state.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
