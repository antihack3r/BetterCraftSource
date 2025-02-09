/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RemoteServer;
import java.util.Arrays;
import java.util.UUID;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.NativeUnixSocket;

public final class AFUNIXSocketCredentials {
    public static final AFUNIXSocketCredentials SAME_PROCESS = new AFUNIXSocketCredentials();
    private long pid = -1L;
    private long uid = -1L;
    private long[] gids = null;
    private UUID uuid = null;

    AFUNIXSocketCredentials() {
    }

    public long getPid() {
        return this.pid;
    }

    public long getUid() {
        return this.uid;
    }

    public long getGid() {
        return this.gids == null ? -1L : (this.gids.length == 0 ? -1L : this.gids[0]);
    }

    public long[] getGids() {
        return this.gids == null ? null : (long[])this.gids.clone();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    void setUUID(String uuidStr) {
        this.uuid = UUID.fromString(uuidStr);
    }

    void setGids(long[] gids) {
        this.gids = (long[])gids.clone();
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(super.toString());
        sb2.append('[');
        if (this == SAME_PROCESS) {
            sb2.append("(same process)]");
            return sb2.toString();
        }
        if (this.pid != -1L) {
            sb2.append("pid=" + this.pid + ";");
        }
        if (this.uid != -1L) {
            sb2.append("uid=" + this.uid + ";");
        }
        if (this.gids != null) {
            sb2.append("gids=" + Arrays.toString(this.gids) + ";");
        }
        if (this.uuid != null) {
            sb2.append("uuid=" + this.uuid + ";");
        }
        if (sb2.charAt(sb2.length() - 1) == ';') {
            sb2.setLength(sb2.length() - 1);
        }
        sb2.append(']');
        return sb2.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.gids);
        result = 31 * result + (int)(this.pid ^ this.pid >>> 32);
        result = 31 * result + (int)(this.uid ^ this.uid >>> 32);
        result = 31 * result + (this.uuid == null ? 0 : this.uuid.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AFUNIXSocketCredentials other = (AFUNIXSocketCredentials)obj;
        if (!Arrays.equals(this.gids, other.gids)) {
            return false;
        }
        if (this.pid != other.pid) {
            return false;
        }
        if (this.uid != other.uid) {
            return false;
        }
        return !(this.uuid == null ? other.uuid != null : !this.uuid.equals(other.uuid));
    }

    public static AFUNIXSocketCredentials remotePeerCredentials() {
        try {
            RemoteServer.getClientHost();
        }
        catch (Exception e2) {
            return null;
        }
        Socket sock = NativeUnixSocket.currentRMISocket();
        if (!(sock instanceof AFUNIXSocket)) {
            return null;
        }
        AFUNIXSocket socket = (AFUNIXSocket)sock;
        try {
            return socket.getPeerCredentials();
        }
        catch (IOException e3) {
            return null;
        }
    }
}

