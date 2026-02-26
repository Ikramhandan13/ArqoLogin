package com.arqologin;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.cumulus.form.CustomForm;

public class GUIManager {
    private final ArqoLoginPlugin plugin;

    public GUIManager(ArqoLoginPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleInput(Player p, String input) {
        if (input == null || input.isEmpty()) return;
        plugin.getAuthManager().isRegistered(p.getUniqueId()).thenAccept(registered -> {
            if (registered) {
                plugin.getAuthManager().login(p, input).thenAccept(success -> {
                    if (success) { plugin.getAuthManager().exitLimbo(p); p.sendMessage(MessageUtil.color("&aLogin berhasil!")); }
                    else { p.sendMessage(MessageUtil.color("&cPassword salah.")); }
                });
            } else {
                plugin.getAuthManager().register(p, input).thenAccept(success -> {
                    if (success) { plugin.getAuthManager().exitLimbo(p); p.sendMessage(MessageUtil.color("&aRegistrasi berhasil!")); }
                });
            }
        });
    }

    public void showLoginForm(Player p) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) {
            showBedrockForm(p);
        } else {
            p.sendMessage(MessageUtil.get("chat-prompt"));
        }
    }

    public void markClosed(Player p) {}

    private void showBedrockForm(Player p) {
        CustomForm form = CustomForm.builder().title(MessageUtil.get("welcome-new")).input("Password", "Ketik di sini...")
                .validResultHandler((f, response) -> handleInput(p, (String) response.next()))
                .closedOrInvalidResultHandler(() -> p.sendMessage(MessageUtil.get("chat-prompt")))
                .build();
        FloodgateApi.getInstance().sendForm(p.getUniqueId(), form);
    }
}
