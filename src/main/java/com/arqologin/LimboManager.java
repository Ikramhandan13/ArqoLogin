package com.arqologin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LimboManager {
    private final ArqoLoginPlugin plugin;
    private World limboWorld;
    private final Map<UUID, LimboSession> sessions = new HashMap<>();

    public LimboManager(ArqoLoginPlugin plugin) { this.plugin = plugin; ensureLimboWorld(); }

    private void ensureLimboWorld() {
        limboWorld = Bukkit.getWorld("limb");
        if (limboWorld == null) {
            WorldCreator c = new WorldCreator("limb");
            c.environment(World.Environment.NORMAL); c.type(WorldType.FLAT); c.generateStructures(false);
            c.generator(new org.bukkit.generator.ChunkGenerator() {
                @Override public org.bukkit.generator.ChunkGenerator.ChunkData generateChunkData(World w, java.util.Random r, int x, int z, BiomeGrid b) { return createChunkData(w); }
            });
            limboWorld = c.createWorld();
        }
        if (limboWorld != null) {
            limboWorld.setAutoSave(false); limboWorld.setTime(6000);
            limboWorld.setGameRuleValue("doDaylightCycle", "false"); limboWorld.setGameRuleValue("doMobSpawning", "false");
        }
    }

    public void enterLimbo(Player p) {
        sessions.put(p.getUniqueId(), new LimboSession(p.getLocation().clone()));
        p.teleport(limboWorld.getSpawnLocation());
        p.setInvulnerable(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        for (Player o : Bukkit.getOnlinePlayers()) if (!o.equals(p)) p.hidePlayer(plugin, o);
    }

    public void exitLimbo(Player p) {
        LimboSession s = sessions.remove(p.getUniqueId());
        if (s != null) {
            p.teleport(s.getOriginal()); p.setInvulnerable(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            for (Player o : Bukkit.getOnlinePlayers()) if (!o.equals(p)) p.showPlayer(plugin, o);
        }
    }

    public boolean isInLimbo(Player p) { return sessions.containsKey(p.getUniqueId()); }
    public World getLimboWorld() { return limboWorld; }
}
