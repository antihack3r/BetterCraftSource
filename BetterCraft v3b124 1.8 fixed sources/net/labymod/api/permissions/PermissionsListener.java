/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.permissions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.api.protocol.liquid.FixedLiquidBucketProtocol;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;

public class PermissionsListener
implements ServerMessageEvent {
    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (!messageKey.equals("PERMISSIONS") || !serverMessage.isJsonObject()) {
            return;
        }
        JsonObject permissionsObject = serverMessage.getAsJsonObject();
        for (Map.Entry<String, JsonElement> jsonEntry : permissionsObject.entrySet()) {
            JsonPrimitive primitive;
            Permissions.Permission permission = Permissions.Permission.getPermissionByName(jsonEntry.getKey());
            JsonPrimitive jsonPrimitive = primitive = jsonEntry.getValue().isJsonPrimitive() ? jsonEntry.getValue().getAsJsonPrimitive() : null;
            if (permission == null || primitive == null || !primitive.isBoolean()) {
                Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "Permission " + jsonEntry.getKey() + " is not supported!");
                continue;
            }
            LabyMod.getInstance().getServerManager().getPermissionMap().put(permission, primitive.getAsBoolean());
            if (permission != Permissions.Permission.IMPROVED_LAVA) continue;
            FixedLiquidBucketProtocol.onPermissionUpdate(primitive.getAsBoolean());
        }
    }
}

