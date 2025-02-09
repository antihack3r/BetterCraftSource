// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import java.util.Locale;
import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketTitle implements Packet<INetHandlerPlayClient>
{
    private Type type;
    private ITextComponent message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;
    
    public SPacketTitle() {
    }
    
    public SPacketTitle(final Type typeIn, final ITextComponent messageIn) {
        this(typeIn, messageIn, -1, -1, -1);
    }
    
    public SPacketTitle(final int fadeInTimeIn, final int displayTimeIn, final int fadeOutTimeIn) {
        this(Type.TIMES, null, fadeInTimeIn, displayTimeIn, fadeOutTimeIn);
    }
    
    public SPacketTitle(final Type typeIn, @Nullable final ITextComponent messageIn, final int fadeInTimeIn, final int displayTimeIn, final int fadeOutTimeIn) {
        this.type = typeIn;
        this.message = messageIn;
        this.fadeInTime = fadeInTimeIn;
        this.displayTime = displayTimeIn;
        this.fadeOutTime = fadeOutTimeIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.type = buf.readEnumValue(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            this.message = buf.readTextComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = buf.readInt();
            this.displayTime = buf.readInt();
            this.fadeOutTime = buf.readInt();
        }
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            buf.writeTextComponent(this.message);
        }
        if (this.type == Type.TIMES) {
            buf.writeInt(this.fadeInTime);
            buf.writeInt(this.displayTime);
            buf.writeInt(this.fadeOutTime);
        }
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleTitle(this);
    }
    
    public Type getType() {
        return this.type;
    }
    
    public ITextComponent getMessage() {
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
    
    public enum Type
    {
        TITLE("TITLE", 0), 
        SUBTITLE("SUBTITLE", 1), 
        ACTIONBAR("ACTIONBAR", 2), 
        TIMES("TIMES", 3), 
        CLEAR("CLEAR", 4), 
        RESET("RESET", 5);
        
        private Type(final String s, final int n) {
        }
        
        public static Type byName(final String name) {
            Type[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final Type spackettitle$type = values[i];
                if (spackettitle$type.name().equalsIgnoreCase(name)) {
                    return spackettitle$type;
                }
            }
            return Type.TITLE;
        }
        
        public static String[] getNames() {
            final String[] astring = new String[values().length];
            int i = 0;
            Type[] values;
            for (int length = (values = values()).length, j = 0; j < length; ++j) {
                final Type spackettitle$type = values[j];
                astring[i++] = spackettitle$type.name().toLowerCase(Locale.ROOT);
            }
            return astring;
        }
    }
}
