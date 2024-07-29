/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.PacketBuf;

public abstract class Packet {
    public abstract void read(PacketBuf var1);

    public abstract void write(PacketBuf var1);

    public abstract void handle(PacketHandler var1);
}

