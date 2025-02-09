/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.api.rewriter.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.util.ChatColorUtil;

public class Replacement {
    private final int id;
    private final int data;
    private final String name;
    private String resetName;
    private String bracketName;

    public Replacement(int id2) {
        this(id2, -1);
    }

    public Replacement(int id2, int data) {
        this(id2, data, null);
    }

    public Replacement(int id2, String name) {
        this(id2, -1, name);
    }

    public Replacement(int id2, int data, String name) {
        this.id = id2;
        this.data = data;
        this.name = name;
    }

    public void buildNames(String protocolVersion) {
        if (this.name != null) {
            this.resetName = ChatColorUtil.translateAlternateColorCodes("&r" + protocolVersion + " " + this.name);
            this.bracketName = ChatColorUtil.translateAlternateColorCodes(" &r&7(" + protocolVersion + " " + this.name + "&r&7)");
        }
    }

    public int getId() {
        return this.id;
    }

    public int getData() {
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public Item replace(Item item) {
        item.setIdentifier(this.id);
        if (this.data != -1) {
            item.setData((short)this.data);
        }
        if (this.name != null) {
            CompoundTag display;
            CompoundTag rootTag;
            CompoundTag compoundTag = rootTag = item.tag() == null ? new CompoundTag() : item.tag();
            if (!rootTag.contains("display")) {
                rootTag.put("display", new CompoundTag());
            }
            if ((display = (CompoundTag)rootTag.get("display")).contains("Name")) {
                StringTag name = (StringTag)display.get("Name");
                if (!name.getValue().equals(this.resetName) && !name.getValue().endsWith(this.bracketName)) {
                    name.setValue(name.getValue() + this.bracketName);
                }
            } else {
                display.put("Name", new StringTag(this.resetName));
            }
            item.setTag(rootTag);
        }
        return item;
    }

    public int replaceData(int data) {
        return this.data == -1 ? data : this.data;
    }
}

