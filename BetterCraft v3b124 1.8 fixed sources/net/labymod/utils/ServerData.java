/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

public class ServerData {
    private String ip;
    private int port = 0;
    private int index = 0;
    private boolean partner;

    public ServerData(String ip2, int port) {
        this(ip2, port, false);
    }

    public ServerData(String ip2, int port, boolean partner) {
        this.ip = ip2;
        this.port = port;
        this.partner = partner;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isPartner() {
        return this.partner;
    }

    public void setIp(String ip2) {
        this.ip = ip2;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPartner(boolean partner) {
        this.partner = partner;
    }
}

