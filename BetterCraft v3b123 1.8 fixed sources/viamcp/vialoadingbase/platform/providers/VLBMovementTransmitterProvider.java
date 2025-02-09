// 
// Decompiled by Procyon v0.6.0
// 

package viamcp.vialoadingbase.platform.providers;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class VLBMovementTransmitterProvider extends MovementTransmitterProvider
{
    @Override
    public Object getFlyingPacket() {
        return null;
    }
    
    @Override
    public Object getGroundPacket() {
        return null;
    }
    
    @Override
    public void sendPlayer(final UserConnection userConnection) {
        if (userConnection.getProtocolInfo().getState() != State.PLAY) {
            return;
        }
        if (userConnection.getEntityTracker(Protocol1_9To1_8.class).clientEntityId() == -1) {
            return;
        }
        final MovementTracker movementTracker = userConnection.get(MovementTracker.class);
        movementTracker.incrementIdlePacket();
        try {
            final PacketWrapper c03 = PacketWrapper.create(ServerboundPackets1_8.PLAYER_MOVEMENT, userConnection);
            c03.write(Type.BOOLEAN, movementTracker.isGround());
            c03.scheduleSendToServer(Protocol1_9To1_8.class);
        }
        catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
