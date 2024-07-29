/*
 * Decompiled with CFR 0.152.
 */
package de.florianmichael.vialoadingbase.command;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.UUID;

public class UserCommandSender
implements ViaCommandSender {
    private final UserConnection user;

    public UserCommandSender(UserConnection user) {
        this.user = user;
    }

    @Override
    public boolean hasPermission(String s2) {
        return false;
    }

    @Override
    public void sendMessage(String s2) {
        Via.getPlatform().sendMessage(this.getUUID(), s2);
    }

    @Override
    public UUID getUUID() {
        return this.user.getProtocolInfo().getUuid();
    }

    @Override
    public String getName() {
        return this.user.getProtocolInfo().getUsername();
    }
}

