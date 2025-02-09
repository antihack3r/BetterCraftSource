/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S45PacketTitle
implements Packet<INetHandlerPlayClient> {
    private Type type;
    private IChatComponent message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;

    public S45PacketTitle() {
    }

    public S45PacketTitle(Type type, IChatComponent message) {
        this(type, message, -1, -1, -1);
    }

    public S45PacketTitle(int fadeInTime, int displayTime, int fadeOutTime) {
        this(Type.TIMES, null, fadeInTime, displayTime, fadeOutTime);
    }

    public S45PacketTitle(Type type, IChatComponent message, int fadeInTime, int displayTime, int fadeOutTime) {
        this.type = type;
        this.message = message;
        this.fadeInTime = fadeInTime;
        this.displayTime = displayTime;
        this.fadeOutTime = fadeOutTime;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.type = buf.readEnumValue(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE) {
            this.message = buf.readChatComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = buf.readInt();
            this.displayTime = buf.readInt();
            this.fadeOutTime = buf.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE) {
            buf.writeChatComponent(this.message);
        }
        if (this.type == Type.TIMES) {
            buf.writeInt(this.fadeInTime);
            buf.writeInt(this.displayTime);
            buf.writeInt(this.fadeOutTime);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTitle(this);
    }

    public Type getType() {
        return this.type;
    }

    public IChatComponent getMessage() {
        return this.message;
    }

    public int getFadeInTime() {
        return this.fadeInTime;
    }

    public int getDisplayTime() {
        return this.displayTime;
    }

    public int getFadeOutTime() {
        return this.fadeOutTime;
    }

    public static enum Type {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET;


        public static Type byName(String name) {
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type s45packettitle$type = typeArray[n3];
                if (s45packettitle$type.name().equalsIgnoreCase(name)) {
                    return s45packettitle$type;
                }
                ++n3;
            }
            return TITLE;
        }

        public static String[] getNames() {
            String[] astring = new String[Type.values().length];
            int i2 = 0;
            Type[] typeArray = Type.values();
            int n2 = typeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Type s45packettitle$type = typeArray[n3];
                astring[i2++] = s45packettitle$type.name().toLowerCase();
                ++n3;
            }
            return astring;
        }
    }
}

