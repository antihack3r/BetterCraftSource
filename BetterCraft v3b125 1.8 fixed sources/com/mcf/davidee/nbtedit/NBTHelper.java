/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit;

import com.mcf.davidee.nbtedit.utils.ReflectionHelperUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper {
    public static NBTTagCompound nbtRead(DataInputStream in2) throws IOException {
        return CompressedStreamTools.read(in2);
    }

    public static void nbtWrite(NBTTagCompound compound, DataOutput out) throws IOException {
        CompressedStreamTools.write(compound, out);
    }

    public static Map<String, NBTBase> getMap(NBTTagCompound tag) {
        return (Map)ReflectionHelperUtils.getPrivateValue(NBTTagCompound.class, tag, 0);
    }

    public static NBTBase getTagAt(NBTTagList tag, int index) {
        List list = (List)ReflectionHelperUtils.getPrivateValue(NBTTagList.class, tag, 1);
        return (NBTBase)list.get(index);
    }

    public static void writeToBuffer(NBTTagCompound nbt, ByteBuf buf) {
        if (nbt == null) {
            buf.writeByte(0);
        } else {
            try {
                CompressedStreamTools.write(nbt, new ByteBufOutputStream(buf));
            }
            catch (IOException e2) {
                throw new EncoderException(e2);
            }
        }
    }

    public static NBTTagCompound readNbtFromBuffer(ByteBuf buf) {
        int index = buf.readerIndex();
        byte isNull = buf.readByte();
        if (isNull == 0) {
            return null;
        }
        buf.readerIndex(index);
        try {
            return CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(0x200000L));
        }
        catch (IOException ioexception) {
            throw new EncoderException(ioexception);
        }
    }
}

