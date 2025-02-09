// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.bukkit.listeners;

import com.viaversion.viaversion.api.connection.UserConnection;
import org.bukkit.entity.Player;
import com.viaversion.viaversion.api.protocol.Protocol;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import com.viaversion.viaversion.ViaListener;

public class ViaBukkitListener extends ViaListener implements Listener
{
    private final Plugin plugin;
    
    public ViaBukkitListener(final Plugin plugin, final Class<? extends Protocol> requiredPipeline) {
        super(requiredPipeline);
        this.plugin = plugin;
    }
    
    protected UserConnection getUserConnection(final Player player) {
        return this.getUserConnection(player.getUniqueId());
    }
    
    protected boolean isOnPipe(final Player player) {
        return this.isOnPipe(player.getUniqueId());
    }
    
    @Override
    public void register() {
        if (this.isRegistered()) {
            return;
        }
        this.setRegistered(true);
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, this.plugin);
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
}
