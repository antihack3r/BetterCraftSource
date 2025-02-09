/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.labymod.api.EventManager;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.tabs.GuiChatNameHistory;
import net.labymod.ingamechat.tools.playermenu.PlayerMenu;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.gui.GuiFriendsAddFriend;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.main.listeners.CapeReportCommand;
import net.labymod.support.util.Debug;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.util.UserActionEntry;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class UserActionGui
extends Gui
implements ServerMessageEvent {
    private static Field fieldLeftClickCounter;
    private List<UserActionEntry> defaultEntries = new ArrayList<UserActionEntry>();
    private List<UserActionEntry> serverEntries = new ArrayList<UserActionEntry>();
    private EntityPlayer selectedPlayer = null;
    private User selectedUser = null;
    private NetworkPlayerInfo networkPlayerInfo;
    private List<UserActionEntry> actionEntries;
    private float lockedYaw = 0.0f;
    private float lockedPitch = 0.0f;
    private boolean prevCrosshairState;
    private boolean middleMousePressed = false;
    private UserActionEntry actionToExecute = null;
    private long selectionStarted;

    public UserActionGui(UserManager userManager) {
        this.defaultEntries.add(new UserActionEntry(LanguageManager.translate("playermenu_entry_copyname"), UserActionEntry.EnumActionType.CLIPBOARD, "{name}", null));
        this.defaultEntries.add(new UserActionEntry(LanguageManager.translate("playermenu_entry_addfriend"), UserActionEntry.EnumActionType.NONE, null, new UserActionEntry.ActionExecutor(){

            @Override
            public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                LabyConnect labyConnect = LabyMod.getInstance().getLabyConnect();
                if (!labyConnect.isOnline()) {
                    return false;
                }
                ArrayList<ChatUser> list = new ArrayList<ChatUser>(LabyMod.getInstance().getLabyConnect().getFriends());
                for (ChatUser p2 : list) {
                    if (!p2.getGameProfile().getId().equals(user.getUuid())) continue;
                    return false;
                }
                return true;
            }

            @Override
            public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsAddFriend(null, entityPlayer.getName()));
            }
        }));
        this.defaultEntries.add(new UserActionEntry(LanguageManager.translate("ingame_chat_tab_namehistory"), UserActionEntry.EnumActionType.NONE, null, new UserActionEntry.ActionExecutor(){

            @Override
            public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                return true;
            }

            @Override
            public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                GuiChatCustom.activeTab = 5;
                Minecraft.getMinecraft().displayGuiScreen(new GuiChatNameHistory("", entityPlayer.getName()));
            }
        }));
        this.defaultEntries.add(new UserActionEntry(LanguageManager.translate("playermenu_entry_capereport"), UserActionEntry.EnumActionType.NONE, null, new UserActionEntry.ActionExecutor(){

            @Override
            public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                return true;
            }

            @Override
            public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
                if (entityPlayer != null && entityPlayer.getName() != null) {
                    CapeReportCommand.report(entityPlayer.getName());
                }
            }
        }));
        EventManager eventManager = LabyMod.getInstance().getEventManager();
        eventManager.register(this);
    }

    @Override
    public void onServerMessage(String messageKey, JsonElement serverMessage) {
        if (!messageKey.equals("user_menu_actions")) {
            return;
        }
        ArrayList<UserActionEntry> serverEntries = new ArrayList<UserActionEntry>();
        JsonArray array = serverMessage.getAsJsonArray();
        int i2 = 0;
        while (i2 < array.size()) {
            try {
                JsonObject obj = array.get(i2).getAsJsonObject();
                String displayName = obj.get("displayName").getAsString();
                UserActionEntry.EnumActionType type = UserActionEntry.EnumActionType.valueOf(obj.get("type").getAsString());
                String value = obj.get("value").getAsString();
                UserActionEntry entry = new UserActionEntry(displayName, type, value, null);
                serverEntries.add(entry);
            }
            catch (Exception error) {
                Debug.log(Debug.EnumDebugMode.API, "Could not parse user menu action entry: " + error.getMessage());
            }
            ++i2;
        }
        this.serverEntries = serverEntries;
    }

    public void open(EntityPlayer entityPlayer) {
        if (this.selectedPlayer != null || entityPlayer == null) {
            return;
        }
        NetworkPlayerInfo networkPlayerInfo = LabyModCore.getMinecraft().getConnection().getPlayerInfo(entityPlayer.getUniqueID());
        if (networkPlayerInfo == null) {
            return;
        }
        if (fieldLeftClickCounter == null) {
            try {
                fieldLeftClickCounter = ReflectionHelper.findField(Minecraft.class, LabyModCore.getMappingAdapter().getLeftClickCounterMappings());
                fieldLeftClickCounter.setAccessible(true);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.networkPlayerInfo = networkPlayerInfo;
        this.selectedPlayer = entityPlayer;
        this.selectedUser = LabyMod.getInstance().getUserManager().getUser(entityPlayer.getUniqueID());
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        ArrayList<UserActionEntry> entries = new ArrayList<UserActionEntry>();
        entries.addAll(this.defaultEntries);
        for (PlayerMenu.PlayerMenuEntry entry : LabyMod.getInstance().getChatToolManager().getPlayerMenu()) {
            entries.add(new UserActionEntry(entry.getDisplayName(), entry.isSendInstantly() ? UserActionEntry.EnumActionType.RUN_COMMAND : UserActionEntry.EnumActionType.SUGGEST_COMMAND, entry.getCommand(), null));
        }
        entries.addAll(this.serverEntries);
        LabyMod.getInstance().getEventManager().callCreateUserMenuActions(this.selectedUser, entityPlayer, networkPlayerInfo, entries);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            UserActionEntry next = (UserActionEntry)iterator.next();
            if (next.getExecutor() == null || next.getExecutor().canAppear(this.selectedUser, player, this.networkPlayerInfo)) continue;
            iterator.remove();
        }
        this.actionEntries = entries;
        this.lockedYaw = player.rotationYaw;
        this.lockedPitch = player.rotationPitch;
        this.selectionStarted = System.currentTimeMillis();
        this.prevCrosshairState = LabyMod.getInstance().getLabyModAPI().isCrosshairHidden();
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(true);
    }

    public void close() {
        if (this.selectedPlayer == null) {
            return;
        }
        this.selectedPlayer = null;
        this.selectedUser = null;
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(this.prevCrosshairState);
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        player.rotationYaw = this.lockedYaw;
        player.rotationPitch = this.lockedPitch;
    }

    public void render() {
        if (this.selectedPlayer == null) {
            return;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null || player.getUniqueID() == null || player.hurtTime != 0) {
            this.close();
            return;
        }
        try {
            if (fieldLeftClickCounter != null) {
                fieldLeftClickCounter.setInt(Minecraft.getMinecraft(), 2);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        double radiusMouseBorder = (double)LabyMod.getInstance().getDrawUtils().getHeight() / 4.0 / 3.0;
        double midX = (double)draw.getWidth() / 2.0;
        double midY = (double)draw.getHeight() / 2.0;
        double lockedX = this.lockedYaw;
        double lockedY = this.lockedPitch;
        if (lockedY + radiusMouseBorder > 90.0) {
            lockedY = (float)(90.0 - radiusMouseBorder);
        }
        if (lockedY - radiusMouseBorder < -90.0) {
            lockedY = (float)(-90.0 + radiusMouseBorder);
        }
        double radius = (double)draw.getHeight() / 4.0;
        double offsetX = lockedX - (double)player.rotationYaw;
        double offsetY = lockedY - (double)player.rotationPitch;
        double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        double cursorX = midX - offsetX * 1.5;
        double cursorY = midY - offsetY * 1.5;
        String playerDisplayName = this.networkPlayerInfo == null || this.networkPlayerInfo.getDisplayName() == null ? this.selectedPlayer.getName() : this.networkPlayerInfo.getDisplayName().getFormattedText();
        draw.drawCenteredString(playerDisplayName, midX += offsetX, (midY += offsetY) - radius / 2.5, radius / 70.0);
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        long timePassed = System.currentTimeMillis() - this.selectionStarted;
        float animation = (float)((float)timePassed > 270.0f ? 1.0 : Math.sin((float)timePassed * 10.0f / 2000.0f));
        if (!LabyMod.getSettings().playerMenuAnimation) {
            animation = 1.0f;
        }
        float skullScale = (20.0f + (float)radius / 2.0f) * animation;
        GlStateManager.translate((float)midX, (float)midY, 0.0f);
        GlStateManager.scale(skullScale, skullScale, skullScale);
        GlStateManager.rotate((float)(cursorY - midY), -1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((float)(cursorX - midX), 0.0f, 1.0f, 0.0f);
        GlStateManager.disableLighting();
        draw.renderSkull(this.selectedPlayer.getGameProfile());
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        int index = 0;
        int amount = this.actionEntries.size();
        this.actionToExecute = null;
        if (amount != 0) {
            for (UserActionEntry entry : this.actionEntries) {
                this.drawUnit(entry, midX, midY, radius, amount, index, cursorX, cursorY, distance);
                ++index;
            }
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (offsetX == 0.0 && offsetY == 0.0) {
            cursorX = (int)cursorX;
            cursorY = (int)cursorY;
        }
        DrawUtils.drawRect(cursorX, cursorY - 4.0, cursorX + 1.0, cursorY + 5.0, Integer.MAX_VALUE);
        DrawUtils.drawRect(cursorX - 4.0, cursorY, cursorX + 5.0, cursorY + 1.0, Integer.MAX_VALUE);
    }

    private void drawUnit(UserActionEntry entry, double midX, double midY, double radius, int amount, int index, double cursorX, double cursorY, double distance) {
        boolean insideOfTwo;
        long timePassed = System.currentTimeMillis() - (this.selectionStarted - 1072L);
        float animation = (float)(timePassed < 1572L ? Math.sin((float)timePassed / 1000.0f) : 1.0);
        if (!LabyMod.getSettings().playerMenuAnimation) {
            animation = 1.0f;
        }
        double tau = Math.PI * 2;
        double destinationShift = 3.0707963267948966;
        double shift = 3.0707963267948966 / (double)(animation * animation);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        double xNext = midX + radius * 2.0 * Math.cos((double)(index + 1) * (Math.PI * 2) / (double)amount + shift);
        double yNext = midY + radius * 2.0 * Math.sin((double)(index + 1) * (Math.PI * 2) / (double)amount + shift);
        double xAfterNext = midX + radius * 2.0 * Math.cos((double)(index + 2) * (Math.PI * 2) / (double)amount + shift);
        double yAfterNext = midY + radius * 2.0 * Math.sin((double)(index + 2) * (Math.PI * 2) / (double)amount + shift);
        double x2 = midX + radius * Math.cos(((double)index + 1.5) * (Math.PI * 2) / (double)amount + shift);
        double y2 = midY + radius * Math.sin(((double)index + 1.5) * (Math.PI * 2) / (double)amount + shift);
        double finalDestX = midX + radius * Math.cos(((double)index + 1.5) * (Math.PI * 2) / (double)amount + 3.0707963267948966);
        double finalDestY = midY + radius * Math.sin(((double)index + 1.5) * (Math.PI * 2) / (double)amount + 3.0707963267948966);
        boolean bl2 = insideOfTwo = cursorY > midY && index == 0 || cursorY < midY && index != 0;
        boolean inside = amount > 2 ? this.isInside(cursorX, cursorY, xAfterNext, yAfterNext, midX, midY, xNext, yNext) : amount == 1 || insideOfTwo;
        boolean hover = distance > 10.0 && inside && timePassed > 1500L;
        double buttonHeight = 256.0;
        double buttonWidth = 256.0;
        double textureWidth = 9.0;
        double textureHeight = 9.0;
        double size = radius / 70.0;
        if (size < 3.0) {
            size = 1.0;
        }
        textureWidth *= size;
        textureHeight *= size;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_MENU_POINT);
        if (hover) {
            GL11.glColor4f(0.2f, 1.0f, 0.2f, 1.0f);
        } else {
            GL11.glColor4f(0.23f, 0.7f, 1.0f, 1.0f);
        }
        if (finalDestX - midX > -10.0 && finalDestX - midX < 10.0) {
            if (finalDestY > midY) {
                draw.drawTexture(x2 - (double)((int)(textureWidth / 2.0)), y2 - 13.0, buttonWidth, buttonHeight, textureWidth, textureHeight);
            } else {
                draw.drawTexture(x2 - (double)((int)(textureWidth / 2.0)), y2 + 13.0, buttonWidth, buttonHeight, textureWidth, textureHeight);
            }
            this.drawOptionTag(entry, x2, y2, hover, radius, 0);
        } else if (finalDestX > midX) {
            if (finalDestY > midY) {
                draw.drawTexture(x2 - 13.0 * size, y2, buttonWidth, buttonHeight, textureWidth, textureHeight);
            } else {
                draw.drawTexture(x2 - 13.0 * size, y2, buttonWidth, buttonHeight, textureWidth, textureHeight);
            }
            this.drawOptionTag(entry, x2, y2, hover, radius, 1);
        } else {
            if (finalDestY > midY) {
                draw.drawTexture(x2 + 4.0, y2, buttonWidth, buttonHeight, textureWidth, textureHeight);
            } else {
                draw.drawTexture(x2 + 4.0, y2, buttonWidth, buttonHeight, textureWidth, textureHeight);
            }
            this.drawOptionTag(entry, x2, y2, hover, radius, -1);
        }
        GL11.glLineWidth(2.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.VOID);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
    }

    private void drawOptionTag(UserActionEntry entry, double x2, double y2, boolean hover, double radius, int alignment) {
        String displayName = entry.getDisplayName();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int stringWidth = draw.getStringWidth(displayName);
        int tagPadding = hover ? 3 : 2;
        int tagHeight = 9;
        int backgroundColor = ModColor.toRGB(20, 20, 20, 100);
        int outlineColor = hover ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        double size = radius / 70.0;
        if (size < 3.0) {
            size = 1.0;
        }
        stringWidth *= (int)size;
        double tagX = x2;
        double tagY = y2 + (double)(tagHeight *= (int)size);
        if (alignment < 0) {
            tagX = x2 - (double)stringWidth;
        } else if (alignment == 0) {
            tagX = x2 - (double)stringWidth / 2.0;
        }
        DrawUtils.drawRect(tagX - (double)tagPadding, tagY - (double)tagHeight - (double)tagPadding, tagX + (double)stringWidth + (double)tagPadding, tagY + (double)tagPadding, backgroundColor);
        draw.drawRectBorder(tagX - (double)tagPadding - 1.0, tagY - (double)tagHeight - (double)tagPadding - 1.0, tagX + (double)stringWidth + (double)tagPadding + 1.0, tagY + (double)tagPadding + 1.0, outlineColor, 1.0);
        switch (alignment) {
            case -1: {
                draw.drawRightString(displayName, x2, y2, size);
                break;
            }
            case 0: {
                draw.drawCenteredString(displayName, x2, y2, size);
                break;
            }
            case 1: {
                draw.drawString(displayName, x2, y2, size);
            }
        }
        if (hover) {
            this.actionToExecute = entry;
        }
    }

    private double sign(double px1, double py1, double px2, double py2, double px3, double py3) {
        return (px1 - px3) * (py2 - py3) - (px2 - px3) * (py1 - py3);
    }

    private boolean isInside(double pointX, double pointY, double px1, double py1, double px2, double py2, double px3, double py3) {
        boolean b3;
        boolean b1 = this.sign(pointX, pointY, px1, py1, px2, py2) < 0.0;
        boolean b2 = this.sign(pointX, pointY, px2, py2, px3, py3) < 0.0;
        boolean bl2 = b3 = this.sign(pointX, pointY, px3, py3, px1, py1) < 0.0;
        return b1 == b2 && b2 == b3;
    }

    public void tick() {
        boolean pressed;
        if (!LabyMod.getSettings().playerMenu) {
            return;
        }
        boolean inGame = LabyMod.getInstance().isInGame();
        if (!inGame) {
            if (this.serverEntries.size() != 0) {
                this.serverEntries.clear();
            }
            return;
        }
        boolean alternativeKeyPressed = LabyMod.getSettings().keyPlayerMenu != -1 && Keyboard.isKeyDown(LabyMod.getSettings().keyPlayerMenu);
        boolean middleMouseButtonpressed = LabyMod.getSettings().keyPlayerMenu == -1 && Mouse.isButtonDown(2);
        boolean bl2 = pressed = (middleMouseButtonpressed || alternativeKeyPressed) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (pressed && Minecraft.getMinecraft().currentScreen == null) {
            if (!this.middleMousePressed) {
                this.middleMousePressed = true;
                Entity entity = LabyModCore.getMinecraft().getEntityMouseOver();
                if (entity != null && entity instanceof EntityPlayer && LabyMod.getInstance().getServerManager().isAllowed(Permissions.Permission.CHAT)) {
                    this.open((EntityPlayer)entity);
                }
            }
        } else if (this.middleMousePressed) {
            this.middleMousePressed = false;
            if (this.actionToExecute != null && this.selectedUser != null && this.selectedPlayer != null && this.networkPlayerInfo != null) {
                this.actionToExecute.execute(this.selectedUser, this.selectedPlayer, this.networkPlayerInfo);
            }
            this.close();
        }
    }

    public void lockMouseMovementInCircle() {
        float newY;
        double distanceY;
        float newX;
        double distanceX;
        double distance;
        if (this.selectedPlayer == null) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        double radius = (double)LabyMod.getInstance().getDrawUtils().getHeight() / 4.0 / 3.0;
        float centerX = this.lockedYaw;
        float centerY = this.lockedPitch;
        if ((double)centerY + radius > 90.0) {
            centerY = (float)(90.0 - radius);
        }
        if ((double)centerY - radius < -90.0) {
            centerY = (float)(-90.0 + radius);
        }
        if ((distance = Math.sqrt((distanceX = (double)(centerX - (newX = player.rotationYaw))) * distanceX + (distanceY = (double)(centerY - (newY = player.rotationPitch))) * distanceY)) > radius) {
            double fromOriginToObjectX = newX - centerX;
            double fromOriginToObjectY = newY - centerY;
            double multiplier = radius / distance;
            player.rotationYaw = centerX += (float)(fromOriginToObjectX *= multiplier);
            player.prevRotationYaw = centerX;
            player.rotationPitch = centerY += (float)(fromOriginToObjectY *= multiplier);
            player.prevRotationPitch = centerY;
        }
    }

    public boolean isOpen() {
        return this.selectedPlayer != null;
    }
}

