/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.nbt;

import com.mcf.davidee.nbtedit.NBTEdit;
import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

public class SaveStates {
    private File file;
    private SaveState[] tags;

    public SaveStates(File file) {
        this.file = file;
        this.tags = new SaveState[7];
        int i2 = 0;
        while (i2 < 7) {
            this.tags[i2] = new SaveState("Slot " + (i2 + 1));
            ++i2;
        }
    }

    public void read() throws IOException {
        if (this.file.exists() && this.file.canRead()) {
            NBTTagCompound root = CompressedStreamTools.read(this.file);
            int i2 = 0;
            while (i2 < 7) {
                String name = "slot" + (i2 + 1);
                if (root.hasKey(name)) {
                    this.tags[i2].tag = root.getCompoundTag(name);
                }
                if (root.hasKey(String.valueOf(name) + "Name")) {
                    this.tags[i2].name = root.getString(String.valueOf(name) + "Name");
                }
                ++i2;
            }
        }
    }

    public void write() throws IOException {
        NBTTagCompound root = new NBTTagCompound();
        int i2 = 0;
        while (i2 < 7) {
            root.setTag("slot" + (i2 + 1), this.tags[i2].tag);
            root.setString("slot" + (i2 + 1) + "Name", this.tags[i2].name);
            ++i2;
        }
        CompressedStreamTools.write(root, this.file);
    }

    public void save() {
        try {
            this.write();
            NBTEdit.log(Level.TRACE, "NBTEdit saved successfully.");
        }
        catch (IOException e2) {
            NBTEdit.log(Level.WARN, "Unable to write NBTEdit save.");
            NBTEdit.throwing("SaveStates", "save", e2);
        }
    }

    public void load() {
        try {
            this.read();
            NBTEdit.log(Level.TRACE, "NBTEdit save loaded successfully.");
        }
        catch (IOException e2) {
            NBTEdit.log(Level.WARN, "Unable to read NBTEdit save.");
            NBTEdit.throwing("SaveStates", "load", e2);
        }
    }

    public SaveState getSaveState(int index) {
        return this.tags[index];
    }

    public static final class SaveState {
        public String name;
        public NBTTagCompound tag;

        public SaveState(String name) {
            this.name = name;
            this.tag = new NBTTagCompound();
        }
    }
}

