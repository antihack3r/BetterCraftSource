/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSessionStorage
extends StoredObject {
    public int gameMode;
    public boolean sprinting;
    public boolean allowFly;
    public boolean flying;
    public boolean invincible;
    public boolean creative;
    public float flySpeed;
    public float walkSpeed;
    private double posX;
    private double posY;
    private double posZ;
    public double receivedPosY;
    public float yaw;
    public float pitch;
    public boolean onGround;
    private final Map<UUID, Item[]> playerEquipment = new HashMap<UUID, Item[]>();

    public PlayerSessionStorage(UserConnection user) {
        super(user);
    }

    public byte combineAbilities() {
        byte flags = 0;
        if (this.invincible) {
            flags = (byte)(flags | 8);
        }
        if (this.allowFly) {
            flags = (byte)(flags | 4);
        }
        if (this.flying) {
            flags = (byte)(flags | 2);
        }
        if (this.creative) {
            flags = (byte)(flags | 1);
        }
        return flags;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public void setPos(double x2, double y2, double z2) {
        this.posX = x2;
        this.posY = y2;
        this.posZ = z2;
    }

    public Item getPlayerEquipment(UUID uuid, int slot) {
        Item[] items = this.playerEquipment.get(uuid);
        if (items == null || slot < 0 || slot >= items.length) {
            return null;
        }
        return items[slot];
    }

    public void setPlayerEquipment(UUID uuid, Item equipment, int slot) {
        Item[] items = this.playerEquipment.computeIfAbsent(uuid, it2 -> new Item[5]);
        if (slot < 0 || slot >= items.length) {
            return;
        }
        items[slot] = equipment;
    }

    public boolean isSpectator() {
        return this.gameMode == 3;
    }

    public Map<UUID, Item[]> getPlayerEquipment() {
        return this.playerEquipment;
    }
}

