/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.permissions;

import java.util.HashMap;
import java.util.Map;
import net.labymod.api.permissions.Permissions;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class PermissionNotifyRenderer {
    private Map<Permissions.Permission, Boolean> updateValues = new HashMap<Permissions.Permission, Boolean>();
    private Map<Permissions.Permission, Boolean> lastRenderedPermissions = new HashMap<Permissions.Permission, Boolean>();
    private long lastCheckCalled;

    public void checkChangedPermissions() {
        boolean equals;
        if (!LabyMod.getSettings().notifyPermissionChanges) {
            return;
        }
        HashMap<Permissions.Permission, Boolean> updateValues = new HashMap<Permissions.Permission, Boolean>();
        Permissions.Permission[] permissionArray = Permissions.Permission.values();
        int n2 = permissionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            boolean serverValue;
            Permissions.Permission permission = permissionArray[n3];
            boolean defaultValue = permission.isDefaultEnabled();
            if (defaultValue != (serverValue = Permissions.isAllowed(permission))) {
                updateValues.put(permission, serverValue);
            }
            ++n3;
        }
        this.updateValues = updateValues;
        boolean bl2 = equals = this.updateValues.size() == this.lastRenderedPermissions.size();
        if (!this.lastRenderedPermissions.isEmpty()) {
            for (Map.Entry<Permissions.Permission, Boolean> set : this.updateValues.entrySet()) {
                Boolean value = this.lastRenderedPermissions.get((Object)set.getKey());
                if (value != null && value == set.getValue()) continue;
                equals = false;
                break;
            }
        }
        this.lastRenderedPermissions.putAll(this.updateValues);
        if (!this.updateValues.isEmpty() && !equals) {
            this.lastCheckCalled = System.currentTimeMillis();
        }
    }

    public void quit() {
        if (!this.lastRenderedPermissions.isEmpty() && LabyMod.getSettings().notifyPermissionChanges) {
            this.lastRenderedPermissions.clear();
        }
    }

    public void render(int screenWidth) {
        int maxDisplayTime = 5000;
        if (this.lastCheckCalled + 5000L > System.currentTimeMillis() && !Minecraft.getMinecraft().isSingleplayer()) {
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, 0.0f, 10.0f);
            int y2 = 5;
            int fadeInAnimation = (int)(System.currentTimeMillis() - (this.lastCheckCalled + 1000L));
            int fadeOutAnimation = (int)(System.currentTimeMillis() - (this.lastCheckCalled + 5000L - 1000L));
            if ((fadeInAnimation /= 5) > 0) {
                fadeInAnimation = 0;
            }
            for (Map.Entry<Permissions.Permission, Boolean> permissionEntry : this.updateValues.entrySet()) {
                boolean value = permissionEntry.getValue();
                String displayName = String.valueOf(value ? ModColor.cl("a") : ModColor.cl("4")) + permissionEntry.getKey().getDisplayName() + " " + (value ? "\u2714" : "\u2716");
                int width = draw.getStringWidth(displayName);
                fadeOutAnimation -= y2 * 2;
                if ((fadeOutAnimation /= 1) < 0) {
                    fadeOutAnimation = 0;
                }
                draw.drawRectangle(screenWidth - 5 - width - 2 + fadeOutAnimation, y2 + fadeInAnimation, screenWidth - 5 + fadeOutAnimation, y2 + 12 + fadeInAnimation, Integer.MIN_VALUE);
                draw.drawRectBorder(screenWidth - 5 - width - 2 + fadeOutAnimation, y2 + fadeInAnimation, screenWidth - 5 + fadeOutAnimation, y2 + 12 + fadeInAnimation, Integer.MIN_VALUE, 1.0);
                draw.drawRightString(displayName, screenWidth - 5 - 1 + fadeOutAnimation, y2 + 2 + fadeInAnimation);
                y2 += 13;
            }
            GlStateManager.translate(0.0f, 0.0f, -10.0f);
            GlStateManager.popMatrix();
        }
    }
}

