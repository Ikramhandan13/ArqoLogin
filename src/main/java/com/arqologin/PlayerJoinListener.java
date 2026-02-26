package com.arqologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerJoinListener implements Listener {
    private final ArqoLoginPlugin plugin;
    public PlayerJoinListener(ArqoLoginPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (plugin.getAuthManager().isInLimbo(e.getPlayer()).join()) {
            e.setCancelled(true); e.getPlayer().sendMessage(MessageUtil.get("chat-prompt"));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (plugin.getAuthManager().isInLimbo(e.getPlayer()).join()) {
            String m = e.getMessage().toLowerCase();
            if (m.startsWith("/login ") || m.startsWith("/register ")) {
                e.setCancelled(true);
                String[] args = e.getMessage().split(" ");
                if (args.length >= 2) {
                    plugin.getGuiManager().handleInput(e.getPlayer(), args[1]);
                }
            } else if (!m.startsWith("/login") && !m.startsWith("/register")) {
                e.setCancelled(true); e.getPlayer().sendMessage(MessageUtil.get("chat-prompt"));
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getSchedulerUtil().runAsync(() -> {
            boolean reg = plugin.getAuthManager().isRegistered(e.getPlayer().getUniqueId()).join();
            plugin.getSchedulerUtil().runSyncDelayed(() -> {
                plugin.getLimboManager().enterLimbo(e.getPlayer());
                plugin.getGuiManager().showLoginForm(e.getPlayer());
                e.getPlayer().sendTitle(reg ? MessageUtil.get("welcome-back") : MessageUtil.get("welcome-new"), "", 10, 70, 20);
                TimerManager.start(e.getPlayer());
            }, 1);
        });
    }
}
