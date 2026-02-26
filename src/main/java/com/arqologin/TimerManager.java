package com.arqologin;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerManager {
    private static final Map<UUID, BossBar> bars = new HashMap<>();

    public static void start(Player p) {
        BossBar bar = org.bukkit.Bukkit.createBossBar(" ", BarColor.valueOf(ArqoLoginPlugin.getInstance().getConfig().getString("bossbar.color")), BarStyle.valueOf(ArqoLoginPlugin.getInstance().getConfig().getString("bossbar.style")));
        bars.put(p.getUniqueId(), bar);
        bar.addPlayer(p);
        runTick(p, AuthConfig.AUTH_TIMEOUT);
    }

    private static void runTick(Player p, int remaining) {
        if (!bars.containsKey(p.getUniqueId())) return;
        if (remaining <= 0) {
            p.kickPlayer(MessageUtil.get("timeout-kick"));
            bars.remove(p.getUniqueId());
            return;
        }
        BossBar bar = bars.get(p.getUniqueId());
        bar.setProgress(Math.max(0.0, Math.min(1.0, (double) remaining / AuthConfig.AUTH_TIMEOUT)));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MessageUtil.color("&cTimeout: " + remaining + " detik")));
        ArqoLoginPlugin.getInstance().getSchedulerUtil().runSyncDelayed(() -> runTick(p, remaining - 1), 20);
    }

    public static void stop(Player p) {
        BossBar bar = bars.remove(p.getUniqueId());
        if (bar != null) bar.removePlayer(p);
    }
}
