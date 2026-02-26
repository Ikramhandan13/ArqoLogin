package com.arqologin;

import org.bukkit.plugin.java.JavaPlugin;

public class ArqoLoginPlugin extends JavaPlugin {
    private static ArqoLoginPlugin instance;
    private SchedulerUtil scheduler;
    private GUIManager guiManager;
    private LimboManager limboManager;
    private AuthManager authManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        ConfigHandler.init(this);
        scheduler = new SchedulerUtil(this);
        databaseManager = DatabaseManager.initAndGet(this);
        AuthManager.init(this, databaseManager, scheduler);
        authManager = AuthManager.getInstance();
        limboManager = new LimboManager(this);
        guiManager = new GUIManager(this);

        new PacketEventListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        
        getCommand("arqologin").setExecutor(new AdminCommandExecutor(this));

        loadAuthConfig();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void loadAuthConfig() {
        AuthConfig.AUTH_TIMEOUT = getConfig().getInt("auth.timeout");
        AuthConfig.MAX_PER_IP = getConfig().getInt("auth.max-per-ip");
        AuthConfig.IP_LOCKOUT_MINUTES = getConfig().getInt("auth.ip-lockout-minutes");
        AuthConfig.PASSWORD_MIN_LENGTH = getConfig().getInt("auth.password.min-length");
        AuthConfig.DISALLOW_COMMON = getConfig().getBoolean("auth.password.disallow-common");
        AuthConfig.USERNAME_PATTERN = getConfig().getString("auth.username.pattern");
    }

    public SchedulerUtil getSchedulerUtil() { return scheduler; }
    public GUIManager getGuiManager() { return guiManager; }
    public LimboManager getLimboManager() { return limboManager; }
    public AuthManager getAuthManager() { return authManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public static ArqoLoginPlugin getInstance() { return instance; }
}
