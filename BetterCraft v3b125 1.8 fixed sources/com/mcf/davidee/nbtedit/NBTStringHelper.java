/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit;

import com.google.common.base.Strings;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTStringHelper {
    public static final char SECTION_SIGN = '\u00a7';

    public static String getNBTName(NamedNBT namedNBT) {
        String name = namedNBT.getName();
        NBTBase obj = namedNBT.getNBT();
        String s2 = NBTStringHelper.toString(obj);
        return Strings.isNullOrEmpty(name) ? s2 : String.valueOf(name) + ": " + s2;
    }

    public static String getNBTNameSpecial(NamedNBT namedNBT) {
        String name = namedNBT.getName();
        NBTBase obj = namedNBT.getNBT();
        String s2 = NBTStringHelper.toString(obj);
        return Strings.isNullOrEmpty(name) ? s2 : String.valueOf(name) + ": " + s2 + '\u00a7' + 'r';
    }

    public static NBTBase newTag(byte type) {
        switch (type) {
            case 0: {
                return new NBTTagEnd();
            }
            case 1: {
                return new NBTTagByte(0);
            }
            case 2: {
                return new NBTTagShort();
            }
            case 3: {
                return new NBTTagInt(0);
            }
            case 4: {
                return new NBTTagLong(0L);
            }
            case 5: {
                return new NBTTagFloat(0.0f);
            }
            case 6: {
                return new NBTTagDouble(0.0);
            }
            case 7: {
                return new NBTTagByteArray(new byte[0]);
            }
            case 8: {
                return new NBTTagString("");
            }
            case 9: {
                return new NBTTagList();
            }
            case 10: {
                return new NBTTagCompound();
            }
            case 11: {
                return new NBTTagIntArray(new int[0]);
            }
        }
        return null;
    }

    public static String toString(NBTBase base) {
        switch (base.getId()) {
            case 1: {
                return "" + ((NBTTagByte)base).getByte();
            }
            case 2: {
                return "" + ((NBTTagShort)base).getShort();
            }
            case 3: {
                return "" + ((NBTTagInt)base).getInt();
            }
            case 4: {
                return "" + ((NBTTagLong)base).getLong();
            }
            case 5: {
                return "" + ((NBTTagFloat)base).getFloat();
            }
            case 6: {
                return "" + ((NBTTagDouble)base).getDouble();
            }
            case 7: {
                return base.toString();
            }
            case 8: {
                return ((NBTTagString)base).getString();
            }
            case 9: {
                return "(TagList)";
            }
            case 10: {
                return "(TagCompound)";
            }
            case 11: {
                return base.toString();
            }
        }
        return "?";
    }

    public static String getButtonName(byte id2) {
        switch (id2) {
            case 1: {
                return "Byte";
            }
            case 2: {
                return "Short";
            }
            case 3: {
                return "Int";
            }
            case 4: {
                return "Long";
            }
            case 5: {
                return "Float";
            }
            case 6: {
                return "Double";
            }
            case 7: {
                return "Byte[]";
            }
            case 8: {
                return "String";
            }
            case 9: {
                return "List";
            }
            case 10: {
                return "Compound";
            }
            case 11: {
                return "Int[]";
            }
            case 12: {
                return "Edit";
            }
            case 13: {
                return "Delete";
            }
            case 14: {
                return "Copy";
            }
            case 15: {
                return "Cut";
            }
            case 16: {
                return "Paste";
            }
        }
        return "Unknown";
    }
}

