// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketChat implements Packet<INetHandlerPlayClient>
{
    private ITextComponent chatComponent;
    private ChatType type;
    
    public SPacketChat() {
    }
    
    public SPacketChat(final ITextComponent componentIn) {
        this(componentIn, ChatType.SYSTEM);
    }
    
    public SPacketChat(final ITextComponent p_i47428_1_, final ChatType p_i47428_2_) {
        this.chatComponent = p_i47428_1_;
        this.type = p_i47428_2_;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.chatComponent = buf.readTextComponent();
        this.type = ChatType.func_192582_a(buf.readByte());
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeTextComponent(this.chatComponent);
        buf.writeByte(this.type.func_192583_a());
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleChat(this);
    }
    
    public ITextComponent getChatComponent() {
        return this.chatComponent;
    }
    
    public boolean isSystem() {
        return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
    }
    
    public ChatType func_192590_c() {
        return this.type;
    }
}
