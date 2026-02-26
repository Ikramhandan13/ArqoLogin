package com.arqologin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import java.util.concurrent.CompletableFuture;

public class SchedulerUtil {
    private final Plugin plugin;
    public SchedulerUtil(Plugin plugin) { this.plugin = plugin; }
    public void runAsync(Runnable r) { Bukkit.getScheduler().runTaskAsynchronously(plugin, r); }
    public void runSyncDelayed(Runnable r, long t) { Bukkit.getScheduler().runTaskLater(plugin, r, t); }
    public <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> s) {
        CompletableFuture<T> f = new CompletableFuture<>();
        runAsync(() -> { try { f.complete(s.get()); } catch (Throwable t) { f.completeExceptionally(t); } });
        return f;
    }
}
