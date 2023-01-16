package me.skmanux.joinmessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public final class JoinMessage extends Plugin implements Listener {

    public File file;
    public Configuration cg;

    @Override
    public void onEnable() {
        // Plugin startup logic
        createFiles();
        registerConfig();
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void createFiles() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                InputStream in = getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void registerConfig() {
        try {
            cg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(ServerConnectedEvent e) {
        ServerInfo curInfo = e.getServer().getInfo();
        ProxiedPlayer p = e.getPlayer();

        for (String srv : cg.getStringList("lobbies")) {
            ServerInfo srvInfo = getProxy().getServerInfo(srv);

            if (curInfo == srvInfo) {
                ComponentBuilder joinMsgComponent = new ComponentBuilder("");

                for (String ln : cg.getStringList("msg")) {
                    joinMsgComponent.append(ChatColor.translateAlternateColorCodes('&', ln).replaceAll("%player%", p.getName()) + "\n");
                }

                p.sendMessage(joinMsgComponent.create());

                return;
            }
        }
    }
}
