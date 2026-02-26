package com.arqologin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthManager {
    private static AuthManager instance;
    private final DatabaseManager db;
    private final SchedulerUtil scheduler;
    private final ArqoLoginPlugin plugin;

    private final Map<UUID, LimboSession> limboSessions = new HashMap<>();
    private final Map<String, AtomicInteger> failedAttempts = new HashMap<>();
    private final Map<String, Long> lockoutUntil = new HashMap<>();

    private AuthManager(ArqoLoginPlugin plugin, DatabaseManager db, SchedulerUtil scheduler) {
        this.plugin = plugin;
        this.db = db;
        this.scheduler = scheduler;
    }

    public static void init(ArqoLoginPlugin plugin, DatabaseManager db, SchedulerUtil scheduler) {
        if (instance == null) { instance = new AuthManager(plugin, db, scheduler); }
    }

    public static AuthManager getInstance() { return instance; }

    public CompletableFuture<Boolean> register(Player p, String password) {
        String ip = p.getAddress().getAddress().getHostAddress();
        return db.fetchUsersByIp(ip).thenCompose(users -> {
            if (users.size() >= AuthConfig.MAX_PER_IP) {
                p.sendMessage(MessageUtil.color("&cTerlalu banyak akun terdaftar dari IP ini!"));
                return CompletableFuture.completedFuture(false);
            }
            if (password.length() < AuthConfig.PASSWORD_MIN_LENGTH) {
                p.sendMessage(MessageUtil.color("&cPassword terlalu pendek!"));
                return CompletableFuture.completedFuture(false);
            }
            if (AuthConfig.DISALLOW_COMMON && AuthConfig.COMMON_PASSWORDS.contains(password.toLowerCase())) {
                p.sendMessage(MessageUtil.color("&cPassword terlalu pasaran!"));
                return CompletableFuture.completedFuture(false);
            }
            if (password.equalsIgnoreCase(p.getName())) {
                p.sendMessage(MessageUtil.color("&cPassword tidak boleh sama dengan username!"));
                return CompletableFuture.completedFuture(false);
            }
            UUID uuid = p.getUniqueId();
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            UserRecord user = new UserRecord(uuid, p.getName(), hash, ip, System.currentTimeMillis());
            return db.saveUser(user).thenApply(v -> true);
        });
    }

    public CompletableFuture<Boolean> login(Player p, String password) {
        if (isLockedOut(p)) {
            p.sendMessage(MessageUtil.get("ip-lockout"));
            return CompletableFuture.completedFuture(false);
        }
        return isSessionValid(p).thenCompose(valid -> {
            if (isPremium(p) || valid) return CompletableFuture.completedFuture(true);
            return db.fetchUserByUUID(p.getUniqueId().toString()).thenApply(user -> {
                if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                    db.saveUser(new UserRecord(p.getUniqueId(), p.getName(), user.getPassword(), p.getAddress().getAddress().getHostAddress(), System.currentTimeMillis()));
                    resetFailedAttempts(p.getAddress().getAddress().getHostAddress());
                    return true;
                }
                recordFailedAttempt(p.getAddress().getAddress().getHostAddress());
                if (getFailedAttempts(p.getAddress().getAddress().getHostAddress()) >= 3) {
                    lockoutIp(p.getAddress().getAddress().getHostAddress());
                    p.kickPlayer(MessageUtil.get("ip-lockout"));
                }
                return false;
            });
        });
    }

    public CompletableFuture<Boolean> isRegistered(UUID uuid) {
        return db.fetchUserByUUID(uuid.toString()).thenApply(u -> u != null);
    }

    public CompletableFuture<Boolean> isInLimbo(Player p) {
        return CompletableFuture.completedFuture(plugin.getLimboManager().isInLimbo(p));
    }

    public void exitLimbo(Player p) {
        plugin.getLimboManager().exitLimbo(p);
        TimerManager.stop(p);
    }

    public boolean isPremium(Player p) {
        if (org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) return true;
        return Bukkit.getOnlineMode();
    }

    public CompletableFuture<Boolean> isSessionValid(Player p) {
        String ip = p.getAddress().getAddress().getHostAddress();
        return db.fetchUserByUUID(p.getUniqueId().toString()).thenApply(u -> u != null && u.getIp().equals(ip) && (System.currentTimeMillis() - u.getLastLogin() < 24 * 3600 * 1000L));
    }

    public void recordFailedAttempt(String ip) { failedAttempts.computeIfAbsent(ip, k -> new AtomicInteger()).incrementAndGet(); }
    public int getFailedAttempts(String ip) { return failedAttempts.getOrDefault(ip, new AtomicInteger(0)).get(); }
    public void resetFailedAttempts(String ip) { failedAttempts.remove(ip); }
    private void lockoutIp(String ip) { lockoutUntil.put(ip, System.currentTimeMillis() + AuthConfig.IP_LOCKOUT_MINUTES * 60_000L); }
    public boolean isLockedOut(Player p) {
        String ip = p.getAddress().getAddress().getHostAddress();
        Long until = lockoutUntil.get(ip);
        if (until == null) return false;
        if (System.currentTimeMillis() > until) { lockoutUntil.remove(ip); return false; }
        return true;
    }

    public void forceLogin(Player p) { exitLimbo(p); }

    public void unregister(String name) {
        scheduler.runAsync(() -> {
            try (java.sql.PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM users WHERE username = ?")) {
                ps.setString(1, name); ps.executeUpdate();
            } catch (Exception e) { Bukkit.getLogger().severe("Unregister error: " + e.getMessage()); }
        });
    }

    public void deleteAccount(String name) { unregister(name); }
}
