/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.partner;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PartnerServerList {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<ServerData> servers = Lists.newArrayList();

    public PartnerServerList(Minecraft mcIn) {
        this.mc = mcIn;
        this.loadServerList();
    }

    public void loadServerList() {
        try {
            this.servers.clear();
            this.addServerData(new ServerData("MarvinMC", "marvinmc.net", false));
        }
        catch (Exception e2) {
            logger.error("Couldn't load partner server list", (Throwable)e2);
        }
    }

    public ServerData getServerData(int p_78850_1_) {
        return this.servers.get(p_78850_1_);
    }

    public void addServerData(ServerData server) {
        this.servers.add(server);
    }

    public int countServers() {
        return this.servers.size();
    }
}

