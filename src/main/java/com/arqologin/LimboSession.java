package com.arqologin;

import org.bukkit.Location;

public class LimboSession {
    private final Location original;
    public LimboSession(Location original) { this.original = original; }
    public Location getOriginal() { return original; }
}
