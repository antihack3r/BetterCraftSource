// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packetIn, final T processor, final IThreadListener scheduler) throws ThreadQuickExitException {
        if (!scheduler.isCallingFromMinecraftThread()) {
            scheduler.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    packetIn.processPacket(processor);
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
    }
}
