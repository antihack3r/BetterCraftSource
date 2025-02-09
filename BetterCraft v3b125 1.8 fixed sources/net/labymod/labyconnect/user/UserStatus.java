/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.user;

import net.labymod.main.lang.LanguageManager;

public enum UserStatus {
    ONLINE(0, "a"),
    AWAY(1, "b"),
    BUSY(2, "5"),
    OFFLINE(-1, "c");

    private byte id;
    private String chatColor;
    private String name;

    private UserStatus(byte id2, String chatColor) {
        this.id = id2;
        this.chatColor = chatColor;
        this.name = LanguageManager.translate("user_status_" + this.name().toLowerCase());
    }

    public static UserStatus getById(int id2) {
        UserStatus[] userStatusArray = UserStatus.values();
        int n2 = userStatusArray.length;
        int n3 = 0;
        while (n3 < n2) {
            UserStatus status = userStatusArray[n3];
            if (status.id == id2) {
                return status;
            }
            ++n3;
        }
        return OFFLINE;
    }

    public byte getId() {
        return this.id;
    }

    public String getChatColor() {
        return this.chatColor;
    }

    public String getName() {
        return this.name;
    }
}

