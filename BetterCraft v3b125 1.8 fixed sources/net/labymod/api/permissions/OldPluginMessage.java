/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.permissions;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import net.labymod.api.permissions.Permissions;
import net.labymod.api.protocol.liquid.FixedLiquidBucketProtocol;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.minecraft.network.PacketBuffer;

public class OldPluginMessage {
    @Deprecated
    public static void handlePluginMessage(String channel, PacketBuffer data) {
        try {
            if (data != null && channel != null && channel.equals("LABYMOD")) {
                int length = data.capacity();
                byte[] array = new byte[length];
                int i2 = 0;
                while (i2 < length) {
                    array[i2] = data.readByte();
                    ++i2;
                }
                ByteArrayInputStream byteIn = new ByteArrayInputStream(array);
                ObjectInputStream in2 = new ObjectInputStream(byteIn);
                Map list = (Map)in2.readObject();
                for (Map.Entry entry : list.entrySet()) {
                    try {
                        Permissions.Permission permission = Permissions.Permission.valueOf(((String)entry.getKey()).toUpperCase());
                        LabyMod.getInstance().getServerManager().getPermissionMap().put(permission, (Boolean)entry.getValue());
                        if (permission != Permissions.Permission.IMPROVED_LAVA) continue;
                        FixedLiquidBucketProtocol.onPermissionUpdate((Boolean)entry.getValue());
                    }
                    catch (Exception error2) {
                        Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[OldVersionSupport] Permission " + (String)entry.getKey() + " is not supported!");
                    }
                }
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
}

