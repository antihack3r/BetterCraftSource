/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class UserActionEntry {
    private String displayName;
    private EnumActionType type;
    private String value;
    private ActionExecutor executor;

    public UserActionEntry(String displayName, EnumActionType type, String value, ActionExecutor executor) {
        this.displayName = ModColor.createColors(displayName);
        this.type = type;
        this.value = value;
        this.executor = executor;
    }

    public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
        if (this.value != null && entityPlayer != null && networkPlayerInfo != null) {
            String string = this.value.replace("{name}", entityPlayer.getName()).replace("{uuid}", entityPlayer.getUniqueID().toString());
            switch (this.type) {
                case CLIPBOARD: {
                    StringSelection stringSelection = new StringSelection(string);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    break;
                }
                case RUN_COMMAND: {
                    if (!string.startsWith("/")) {
                        string = "/" + string;
                    }
                    if (Permissions.isAllowed(Permissions.Permission.CHAT)) {
                        LabyModCore.getMinecraft().getPlayer().sendChatMessage(string);
                        break;
                    }
                    LabyMod.getInstance().displayMessageInChat(String.valueOf(ModColor.cl('c')) + "This feature is not allowed on this server!");
                    Minecraft.getMinecraft().displayGuiScreen(new GuiChatCustom(string));
                    break;
                }
                case SUGGEST_COMMAND: {
                    if (!string.startsWith("/")) {
                        string = "/" + string;
                    }
                    Minecraft.getMinecraft().displayGuiScreen(new GuiChatCustom(string));
                    break;
                }
                case OPEN_BROWSER: {
                    LabyMod.getInstance().openWebpage(string, true);
                }
            }
        }
        if (this.executor != null) {
            this.executor.execute(user, entityPlayer, networkPlayerInfo);
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public EnumActionType getType() {
        return this.type;
    }

    public void setType(EnumActionType type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ActionExecutor getExecutor() {
        return this.executor;
    }

    public void setExecutor(ActionExecutor executor) {
        this.executor = executor;
    }

    public static interface ActionExecutor {
        public void execute(User var1, EntityPlayer var2, NetworkPlayerInfo var3);

        public boolean canAppear(User var1, EntityPlayer var2, NetworkPlayerInfo var3);
    }

    public static enum EnumActionType {
        NONE,
        CLIPBOARD,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_BROWSER;

    }
}

