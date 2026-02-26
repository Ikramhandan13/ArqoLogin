package com.arqologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class PacketEventListener implements Listener {
    private final AuthManager auth;

    public PacketEventListener(ArqoLoginPlugin plugin) {
        this.auth = plugin.getAuthManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (auth.isInLimbo(e.getPlayer()).join()) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setTo(e.getFrom());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (auth.isInLimbo(e.getPlayer()).join()) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (auth.isInLimbo(e.getPlayer()).join()) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player && auth.isInLimbo((Player) e.getWhoClicked()).join()) {
            e.setCancelled(true);
        }
    }
}
