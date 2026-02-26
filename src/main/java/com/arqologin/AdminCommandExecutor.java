package com.arqologin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommandExecutor implements CommandExecutor {
    private final ArqoLoginPlugin plugin;
    public AdminCommandExecutor(ArqoLoginPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sender.sendMessage(ChatColor.GREEN + "/arqologin support|setspawn|forcelogin|dupeip|unregister|delete"); return true; }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "support": sender.sendMessage(MessageUtil.get("support-info")); break;
            case "setspawn": if (sender instanceof Player) { plugin.getLimboManager().getLimboWorld().setSpawnLocation(((Player) sender).getLocation()); sender.sendMessage(ChatColor.GREEN + "Spawn limbo disimpan."); } break;
            case "forcelogin": if (args.length >= 2) { Player t = Bukkit.getPlayer(args[1]); if (t != null) { plugin.getAuthManager().forceLogin(t); sender.sendMessage(ChatColor.GREEN + "Forced login " + t.getName()); } } break;
            case "dupeip":
                if (args.length >= 2) {
                    Player pt = Bukkit.getPlayer(args[1]); String ip = (pt != null) ? pt.getAddress().getAddress().getHostAddress() : args[1];
                    plugin.getDatabaseManager().fetchUsersByIp(ip).thenAccept(u -> sender.sendMessage(ChatColor.YELLOW + "Accounts on " + ip + ": " + String.join(", ", u)));
                } else {
                    plugin.getDatabaseManager().findDuplicateIps().thenAccept(m -> m.forEach((ip, u) -> sender.sendMessage(ChatColor.YELLOW + ip + " -> " + String.join(", ", u))));
                }
                break;
            case "unregister": if (args.length >= 2) { plugin.getAuthManager().unregister(args[1]); sender.sendMessage(ChatColor.YELLOW + "Unregistered " + args[1]); } break;
            case "delete": if (args.length >= 2) { sender.sendMessage(ChatColor.RED + "🚨 UUID data may be lost!"); plugin.getAuthManager().deleteAccount(args[1]); sender.sendMessage(ChatColor.RED + "Deleted " + args[1]); } break;
        }
        return true;
    }
}
