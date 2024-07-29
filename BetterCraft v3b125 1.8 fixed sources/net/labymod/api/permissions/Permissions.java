/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.permissions;

import net.labymod.api.permissions.PermissionNotifyRenderer;
import net.labymod.main.LabyMod;

public class Permissions {
    private static PermissionNotifyRenderer permissionNotifyRenderer = new PermissionNotifyRenderer();

    public static boolean isAllowed(Permission permission) {
        if (LabyMod.getInstance() == null || LabyMod.getInstance().getServerManager() == null) {
            return permission.isDefaultEnabled();
        }
        return LabyMod.getInstance().getServerManager().isAllowed(permission);
    }

    public static PermissionNotifyRenderer getPermissionNotifyRenderer() {
        return permissionNotifyRenderer;
    }

    public static enum Permission {
        IMPROVED_LAVA("Improved Lava", false),
        CROSSHAIR_SYNC("Crosshair sync", false),
        REFILL_FIX("Refill fix", false),
        GUI_ALL("LabyMod GUI", true),
        GUI_POTION_EFFECTS("Potion Effects", true),
        GUI_ARMOR_HUD("Armor HUD", true),
        GUI_ITEM_HUD("Item HUD", true),
        BLOCKBUILD("Blockbuild", true),
        TAGS("Tags", true),
        CHAT("Chat features", true),
        ANIMATIONS("Animations", true),
        SATURATION_BAR("Saturation bar", true);

        private String displayName;
        private boolean defaultEnabled;

        public static Permission getPermissionByName(String name) {
            Permission[] permissionArray = Permission.values();
            int n2 = permissionArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Permission permission = permissionArray[n3];
                if (permission.name().equals(name)) {
                    return permission;
                }
                ++n3;
            }
            return null;
        }

        private Permission(String displayName, boolean defaultEnabled) {
            this.displayName = displayName;
            this.defaultEnabled = defaultEnabled;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isDefaultEnabled() {
            return this.defaultEnabled;
        }
    }
}

