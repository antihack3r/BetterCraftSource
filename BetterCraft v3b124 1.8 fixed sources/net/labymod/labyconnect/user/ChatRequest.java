/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.user;

import com.mojang.authlib.GameProfile;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.UserStatus;

public class ChatRequest
extends ChatUser {
    public ChatRequest(GameProfile gameProfile) {
        super(gameProfile, UserStatus.OFFLINE, "", null, 0, System.currentTimeMillis(), 0L, "", 0L, 0L, 0, false);
    }
}

