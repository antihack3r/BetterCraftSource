/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.events;

import java.util.List;
import net.labymod.user.User;
import net.labymod.user.util.UserActionEntry;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public interface UserMenuActionEvent {
    public void createActions(User var1, EntityPlayer var2, NetworkPlayerInfo var3, List<UserActionEntry> var4);
}

