package com.arqologin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRecord {
    private final UUID uuid;
    private final String username;
    private final String password;
    private final String ip;
    private final long lastLogin;

    public UserRecord(UUID uuid, String username, String password, String ip, long lastLogin) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.lastLogin = lastLogin;
    }

    public UUID getUuid() { return uuid; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getIp() { return ip; }
    public long getLastLogin() { return lastLogin; }

    public static UserRecord fromResultSet(ResultSet rs) throws SQLException {
        return new UserRecord(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("ip"),
                rs.getLong("last_login")
        );
    }
}
