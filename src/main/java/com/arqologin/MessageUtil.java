package com.arqologin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtil {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(Player p, String path) {
        String msg = ConfigHandler.getInstance().getMessage(path);
        if (msg != null && !msg.isEmpty()) {
            p.sendMessage(color(msg));
        }
    }

    public static String get(String path) {
        String msg = ConfigHandler.getInstance().getMessage(path);
        return msg == null ? "" : color(msg);
    }
}
