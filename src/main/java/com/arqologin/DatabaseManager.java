package com.arqologin;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final ConfigHandler config;
    private final SchedulerUtil scheduler;
    private Connection connection;

    private DatabaseManager(ConfigHandler config, SchedulerUtil scheduler) {
        this.config = config;
        this.scheduler = scheduler;
        init();
    }

    public static void init(ConfigHandler config, SchedulerUtil scheduler) {
        if (instance == null) { instance = new DatabaseManager(config, scheduler); }
    }

    public static DatabaseManager getInstance() { return instance; }

    public static DatabaseManager initAndGet(ArqoLoginPlugin plugin) {
        ConfigHandler cfg = ConfigHandler.getInstance();
        SchedulerUtil sched = new SchedulerUtil(plugin);
        init(cfg, sched);
        return getInstance();
    }

    private void init() {
        String typeStr = config.getString("database.type");
        try {
            if ("mysql".equalsIgnoreCase(typeStr)) { openMySQL(); } 
            else { openSQLite(); }
            createTables();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Gagal membuka database", e);
        }
    }

    private void openSQLite() throws SQLException {
        String file = config.getString("database.sqlite-file");
        java.io.File dbFile = new java.io.File(ArqoLoginPlugin.getInstance().getDataFolder(), file);
        if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
    }

    private void openMySQL() throws SQLException {
        String host = config.getString("database.mysql.host");
        int port = config.getInt("database.mysql.port");
        String db = config.getString("database.mysql.database");
        String user = config.getString("database.mysql.username");
        String pass = config.getString("database.mysql.password");
        String url = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", host, port, db);
        connection = DriverManager.getConnection(url, user, pass);
    }

    private void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (uuid VARCHAR(36) PRIMARY KEY, username VARCHAR(16), password VARCHAR(60), ip VARCHAR(45), last_login BIGINT);");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ip_registers (ip VARCHAR(45) PRIMARY KEY, count INT);");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS lockouts (ip VARCHAR(45) PRIMARY KEY, until BIGINT);");
        }
    }

    public void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException e) { Bukkit.getLogger().log(Level.WARNING, "Error closing database", e); }
        }
    }

    public Connection getConnection() { return connection; }

    public CompletableFuture<Void> saveUser(UserRecord user) {
        return scheduler.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("REPLACE INTO users (uuid, username, password, ip, last_login) VALUES (?,?,?,?,?)")) {
                ps.setString(1, user.getUuid().toString());
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getIp());
                ps.setLong(5, user.getLastLogin());
                ps.executeUpdate();
            } catch (SQLException e) { Bukkit.getLogger().log(Level.SEVERE, "[DB] saveUser error", e); }
            return null;
        });
    }

    public CompletableFuture<UserRecord> fetchUserByUUID(String uuid) {
        return scheduler.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?")) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { return UserRecord.fromResultSet(rs); } }
            } catch (SQLException e) { Bukkit.getLogger().log(Level.SEVERE, "[DB] fetchUser error", e); }
            return null;
        });
    }

    public CompletableFuture<java.util.List<String>> fetchUsersByIp(String ip) {
        return scheduler.supplyAsync(() -> {
            java.util.List<String> list = new java.util.ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE ip = ?")) {
                ps.setString(1, ip);
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { list.add(rs.getString("username")); } }
            } catch (SQLException e) { Bukkit.getLogger().log(Level.SEVERE, "[DB] fetchUsersByIp error", e); }
            return list;
        });
    }

    public CompletableFuture<java.util.Map<String, java.util.List<String>>> findDuplicateIps() {
        return scheduler.supplyAsync(() -> {
            java.util.Map<String, java.util.List<String>> map = new java.util.HashMap<>();
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery("SELECT ip, GROUP_CONCAT(username) AS users, COUNT(*) AS cnt FROM users GROUP BY ip HAVING cnt > 1")) {
                while (rs.next()) {
                    String ip = rs.getString("ip");
                    String users = rs.getString("users");
                    map.put(ip, java.util.Arrays.asList(users.split(",")));
                }
            } catch (SQLException e) { Bukkit.getLogger().log(Level.SEVERE, "[DB] findDuplicateIps error", e); }
            return map;
        });
    }
}
