/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.liquid;

import io.netty.buffer.Unpooled;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.minecraft.network.PacketBuffer;

public class FixedLiquidBucketProtocol {
    public static void handleBucketAction(Action bucketAction, int x2, int y2, int z2) {
        if (!Permissions.isAllowed(Permissions.Permission.IMPROVED_LAVA)) {
            Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "Can't handle LAVA_UPDATE with action " + bucketAction.name() + " - not allowed!");
            return;
        }
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeInt(bucketAction.ordinal());
        if (bucketAction == Action.FILL_BUCKET || bucketAction == Action.EMPTY_BUCKET) {
            packetBuffer.writeInt(x2);
            packetBuffer.writeInt(y2);
            packetBuffer.writeInt(z2);
            Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "LAVA_UPDATE with action " + bucketAction.name() + " at " + x2 + " " + y2 + " " + z2);
        } else {
            Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "LAVA_UPDATE with action " + bucketAction.name());
        }
        LabyModCore.getMinecraft().sendPluginMessage("LAVA_UPDATE", packetBuffer);
    }

    public static void onPermissionUpdate(boolean allowed) {
        if (!allowed) {
            return;
        }
        if (LabyMod.getSettings().improvedLavaFixedGhostBlocks) {
            FixedLiquidBucketProtocol.handleBucketAction(Action.ENABLE, 0, 0, 0);
        }
    }

    public static enum Action {
        FILL_BUCKET,
        EMPTY_BUCKET,
        ENABLE,
        DISABLE;

    }
}

