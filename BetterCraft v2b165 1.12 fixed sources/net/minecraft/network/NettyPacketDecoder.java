// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

import java.io.IOException;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyPacketDecoder extends ByteToMessageDecoder
{
    private static final Logger LOGGER;
    private static final Marker RECEIVED_PACKET_MARKER;
    private final EnumPacketDirection direction;
    
    static {
        LOGGER = LogManager.getLogger();
        RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.NETWORK_PACKETS_MARKER);
    }
    
    public NettyPacketDecoder(final EnumPacketDirection direction) {
        this.direction = direction;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext p_decode_1_, final ByteBuf p_decode_2_, final List<Object> p_decode_3_) throws IOException, InstantiationException, IllegalAccessException, Exception {
        if (p_decode_2_.readableBytes() != 0) {
            final PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            final int i = packetbuffer.readVarIntFromBuffer();
            final Packet<?> packet = p_decode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getPacket(this.direction, i);
            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            }
            packet.readPacketData(packetbuffer);
            if (packetbuffer.readableBytes() > 0) {
                throw new IOException("Packet " + p_decode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
            }
            p_decode_3_.add(packet);
            if (NettyPacketDecoder.LOGGER.isDebugEnabled()) {
                NettyPacketDecoder.LOGGER.debug(NettyPacketDecoder.RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", p_decode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get(), i, packet.getClass().getName());
            }
        }
    }
}
