/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.impl;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.labymod.core_implementation.mc18.serverpinger.ServerPinger;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewServerPinger {
    private static final AtomicInteger threadNumber = new AtomicInteger(0);
    public static final Logger logger = LogManager.getLogger();
    public ServerData serverData;
    private boolean done = false;
    private boolean failed = false;

    public void ping(String ip2) {
        this.pinger(ip2, 25565);
    }

    public void pinger(final String ip2, final int port) {
        this.serverData = new ServerData("", String.valueOf(String.valueOf(String.valueOf(ip2))) + ":" + port, false);
        final ServerAddress serveradress = ServerAddress.resolveAddress(this.serverData.serverIP);
        new Thread("Server Connector #" + threadNumber.incrementAndGet()){

            @Override
            public void run() {
                ServerPinger pinger = new ServerPinger();
                try {
                    logger.info("Pinging " + ip2 + ":" + port + "...");
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(serveradress.getIP(), serveradress.getPort()), 500);
                    socket.close();
                    logger.info("Ping successful: " + ip2 + ":" + port);
                }
                catch (UnknownHostException e2) {
                    logger.info("Unknown host: " + ip2 + ":" + port);
                    NewServerPinger.failed(NewServerPinger.this, true);
                }
                catch (Exception e2) {
                    logger.info("Ping failed: " + ip2 + ":" + port);
                    NewServerPinger.failed(NewServerPinger.this, true);
                }
                pinger.clearPendingNetworks();
                NewServerPinger.done(NewServerPinger.this, true);
            }
        }.start();
    }

    public boolean isStillPinging() {
        return !this.done;
    }

    public boolean isWorking() {
        return !this.failed;
    }

    public boolean isOtherVersion() {
        return this.serverData.version != 47;
    }

    static void failed(NewServerPinger wurstServerPinger, boolean failed) {
        wurstServerPinger.failed = failed;
    }

    static void done(NewServerPinger wurstServerPinger, boolean done) {
        wurstServerPinger.done = done;
    }
}

