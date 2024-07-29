/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.servermanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.nzxtercode.bettercraft.client.events.PlayerTickEvent;
import net.labymod.api.LabyModAddon;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.servermanager.Server;
import net.labymod.servermanager.ServerConfig;
import net.labymod.support.util.Debug;
import net.labymod.utils.manager.ConfigManager;
import net.lenni0451.eventapi.events.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ServerManager {
    private final ConfigManager<ServerConfig> serverConfigManager;
    private final ServerConfig config;
    private final List<Server> servers = new ArrayList<Server>();
    private final Map<Permissions.Permission, Boolean> permissionMap = new HashMap<Permissions.Permission, Boolean>();
    private Server currentServer;
    private ServerData prevServer;
    private long lastSecond = 0L;

    public ServerManager() {
        this.serverConfigManager = new ConfigManager<ServerConfig>(new File("LabyMod/", "servers.json"), ServerConfig.class);
        this.config = this.serverConfigManager.getSettings();
    }

    public void init() {
    }

    public void registerServerSupport(LabyModAddon labyModAddon, Server server) {
        server.bindAddon(labyModAddon);
        this.servers.add(server);
    }

    @EventTarget
    public void handleEvent(PlayerTickEvent event) {
        ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
        if (currentServerData != null && LabyModCore.getMinecraft().getConnection() != null && this.prevServer != currentServerData) {
            this.prevServer = currentServerData;
            this.updateServer(currentServerData.serverIP);
            if (this.currentServer != null) {
                this.currentServer.onJoin(currentServerData);
            }
            LabyMod.getInstance().onJoinServer(currentServerData);
        } else if (currentServerData == null && this.prevServer != null) {
            this.reset();
            this.updateServer(null);
            this.prevServer = null;
            Debug.log(Debug.EnumDebugMode.MINECRAFT, "Disconnected from server");
        }
        if (this.lastSecond < System.currentTimeMillis()) {
            this.lastSecond = System.currentTimeMillis() + 1000L;
            if (this.currentServer != null) {
                this.currentServer.loopSecond();
            }
        }
    }

    public ChatDisplayAction handleChatMessage(String clean, String formatted) {
        try {
            return this.currentServer == null ? ChatDisplayAction.NORMAL : this.currentServer.handleChatMessage(clean, formatted);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return ChatDisplayAction.NORMAL;
        }
    }

    public void updateServer(String ip2) {
        if (ip2 == null) {
            this.currentServer = null;
            return;
        }
        for (Server server : this.servers) {
            String[] stringArray = server.getAddressNames();
            int n2 = stringArray.length;
            int n3 = 0;
            while (n3 < n2) {
                String addresses = stringArray[n3];
                if (ip2.toLowerCase().contains(addresses.toLowerCase())) {
                    this.currentServer = server;
                    return;
                }
                ++n3;
            }
        }
    }

    public void reset() {
        this.permissionMap.clear();
        for (Server server : this.servers) {
            server.reset();
        }
    }

    public void draw() {
        if (this.currentServer == null) {
            return;
        }
        this.currentServer.draw();
    }

    public boolean isServer(Class<? extends Server> serverClass) {
        return this.currentServer != null && this.currentServer.getClass().equals(serverClass);
    }

    public boolean isAllowed(Permissions.Permission permission) {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            return true;
        }
        if (this.currentServer == null) {
            Boolean allowed = this.permissionMap.get((Object)permission);
            if (allowed == null) {
                allowed = permission.isDefaultEnabled();
            }
            return allowed;
        }
        return this.currentServer.isAllowed(permission);
    }

    public ConfigManager<ServerConfig> getServerConfigManager() {
        return this.serverConfigManager;
    }

    public ServerConfig getConfig() {
        return this.config;
    }

    public List<Server> getServers() {
        return this.servers;
    }

    public Map<Permissions.Permission, Boolean> getPermissionMap() {
        return this.permissionMap;
    }

    public Server getCurrentServer() {
        return this.currentServer;
    }

    public void setPrevServer(ServerData prevServer) {
        this.prevServer = prevServer;
    }
}

