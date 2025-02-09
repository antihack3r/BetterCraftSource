/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
    private final Entity owner;
    private boolean isBlank = true;
    private static final Map<Class<?>, Integer> dataTypes = Maps.newHashMap();
    private final Map<Integer, WatchableObject> watchedObjects = Maps.newHashMap();
    private boolean objectChanged;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    public BiomeGenBase spawnBiome = BiomeGenBase.plains;
    public BlockPos spawnPosition = BlockPos.ORIGIN;

    static {
        dataTypes.put(Byte.class, 0);
        dataTypes.put(Short.class, 1);
        dataTypes.put(Integer.class, 2);
        dataTypes.put(Float.class, 3);
        dataTypes.put(String.class, 4);
        dataTypes.put(ItemStack.class, 5);
        dataTypes.put(BlockPos.class, 6);
        dataTypes.put(Rotations.class, 7);
    }

    public DataWatcher(Entity owner) {
        this.owner = owner;
    }

    public <T> void addObject(int id2, T object) {
        Integer integer = dataTypes.get(object.getClass());
        if (integer == null) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        }
        if (id2 > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id2 + "! (Max is " + 31 + ")");
        }
        if (this.watchedObjects.containsKey(id2)) {
            throw new IllegalArgumentException("Duplicate id value for " + id2 + "!");
        }
        WatchableObject datawatcher$watchableobject = new WatchableObject(integer, id2, object);
        this.lock.writeLock().lock();
        this.watchedObjects.put(id2, datawatcher$watchableobject);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    public void addObjectByDataType(int id2, int type) {
        WatchableObject datawatcher$watchableobject = new WatchableObject(type, id2, null);
        this.lock.writeLock().lock();
        this.watchedObjects.put(id2, datawatcher$watchableobject);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    public byte getWatchableObjectByte(int id2) {
        return (Byte)this.getWatchedObject(id2).getObject();
    }

    public short getWatchableObjectShort(int id2) {
        return (Short)this.getWatchedObject(id2).getObject();
    }

    public int getWatchableObjectInt(int id2) {
        return (Integer)this.getWatchedObject(id2).getObject();
    }

    public float getWatchableObjectFloat(int id2) {
        return ((Float)this.getWatchedObject(id2).getObject()).floatValue();
    }

    public String getWatchableObjectString(int id2) {
        return (String)this.getWatchedObject(id2).getObject();
    }

    public ItemStack getWatchableObjectItemStack(int id2) {
        return (ItemStack)this.getWatchedObject(id2).getObject();
    }

    private WatchableObject getWatchedObject(int id2) {
        WatchableObject datawatcher$watchableobject;
        this.lock.readLock().lock();
        try {
            datawatcher$watchableobject = this.watchedObjects.get(id2);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Synched entity data");
            crashreportcategory.addCrashSection("Data ID", id2);
            throw new ReportedException(crashreport);
        }
        this.lock.readLock().unlock();
        return datawatcher$watchableobject;
    }

    public Rotations getWatchableObjectRotations(int id2) {
        return (Rotations)this.getWatchedObject(id2).getObject();
    }

    public <T> void updateObject(int id2, T newData) {
        WatchableObject datawatcher$watchableobject = this.getWatchedObject(id2);
        if (ObjectUtils.notEqual(newData, datawatcher$watchableobject.getObject())) {
            datawatcher$watchableobject.setObject(newData);
            this.owner.onDataWatcherUpdate(id2);
            datawatcher$watchableobject.setWatched(true);
            this.objectChanged = true;
        }
    }

    public void setObjectWatched(int id2) {
        this.getWatchedObject(id2).watched = true;
        this.objectChanged = true;
    }

    public boolean hasObjectChanged() {
        return this.objectChanged;
    }

    public static void writeWatchedListToPacketBuffer(List<WatchableObject> objectsList, PacketBuffer buffer) throws IOException {
        if (objectsList != null) {
            for (WatchableObject datawatcher$watchableobject : objectsList) {
                DataWatcher.writeWatchableObjectToPacketBuffer(buffer, datawatcher$watchableobject);
            }
        }
        buffer.writeByte(127);
    }

    public List<WatchableObject> getChanged() {
        ArrayList<WatchableObject> list = null;
        if (this.objectChanged) {
            this.lock.readLock().lock();
            for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values()) {
                if (!datawatcher$watchableobject.isWatched()) continue;
                datawatcher$watchableobject.setWatched(false);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(datawatcher$watchableobject);
            }
            this.lock.readLock().unlock();
        }
        this.objectChanged = false;
        return list;
    }

    public void writeTo(PacketBuffer buffer) throws IOException {
        this.lock.readLock().lock();
        for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values()) {
            DataWatcher.writeWatchableObjectToPacketBuffer(buffer, datawatcher$watchableobject);
        }
        this.lock.readLock().unlock();
        buffer.writeByte(127);
    }

    public List<WatchableObject> getAllWatched() {
        ArrayList<WatchableObject> list = null;
        this.lock.readLock().lock();
        for (WatchableObject datawatcher$watchableobject : this.watchedObjects.values()) {
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(datawatcher$watchableobject);
        }
        this.lock.readLock().unlock();
        return list;
    }

    private static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, WatchableObject object) throws IOException {
        int i2 = (object.getObjectType() << 5 | object.getDataValueId() & 0x1F) & 0xFF;
        buffer.writeByte(i2);
        switch (object.getObjectType()) {
            case 0: {
                buffer.writeByte(((Byte)object.getObject()).byteValue());
                break;
            }
            case 1: {
                buffer.writeShort(((Short)object.getObject()).shortValue());
                break;
            }
            case 2: {
                buffer.writeInt((Integer)object.getObject());
                break;
            }
            case 3: {
                buffer.writeFloat(((Float)object.getObject()).floatValue());
                break;
            }
            case 4: {
                buffer.writeString((String)object.getObject());
                break;
            }
            case 5: {
                ItemStack itemstack = (ItemStack)object.getObject();
                buffer.writeItemStackToBuffer(itemstack);
                break;
            }
            case 6: {
                BlockPos blockpos = (BlockPos)object.getObject();
                buffer.writeInt(blockpos.getX());
                buffer.writeInt(blockpos.getY());
                buffer.writeInt(blockpos.getZ());
                break;
            }
            case 7: {
                Rotations rotations = (Rotations)object.getObject();
                buffer.writeFloat(rotations.getX());
                buffer.writeFloat(rotations.getY());
                buffer.writeFloat(rotations.getZ());
            }
        }
    }

    public static List<WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer buffer) throws IOException {
        ArrayList<WatchableObject> list = null;
        byte i2 = buffer.readByte();
        while (i2 != 127) {
            if (list == null) {
                list = Lists.newArrayList();
            }
            int j2 = (i2 & 0xE0) >> 5;
            int k2 = i2 & 0x1F;
            WatchableObject datawatcher$watchableobject = null;
            switch (j2) {
                case 0: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, buffer.readByte());
                    break;
                }
                case 1: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, buffer.readShort());
                    break;
                }
                case 2: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, buffer.readInt());
                    break;
                }
                case 3: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, Float.valueOf(buffer.readFloat()));
                    break;
                }
                case 4: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, buffer.readStringFromBuffer(Short.MAX_VALUE));
                    break;
                }
                case 5: {
                    datawatcher$watchableobject = new WatchableObject(j2, k2, buffer.readItemStackFromBuffer());
                    break;
                }
                case 6: {
                    int l2 = buffer.readInt();
                    int i1 = buffer.readInt();
                    int j1 = buffer.readInt();
                    datawatcher$watchableobject = new WatchableObject(j2, k2, new BlockPos(l2, i1, j1));
                    break;
                }
                case 7: {
                    float f2 = buffer.readFloat();
                    float f1 = buffer.readFloat();
                    float f22 = buffer.readFloat();
                    datawatcher$watchableobject = new WatchableObject(j2, k2, new Rotations(f2, f1, f22));
                }
            }
            list.add(datawatcher$watchableobject);
            i2 = buffer.readByte();
        }
        return list;
    }

    public void updateWatchedObjectsFromList(List<WatchableObject> p_75687_1_) {
        this.lock.writeLock().lock();
        for (WatchableObject datawatcher$watchableobject : p_75687_1_) {
            WatchableObject datawatcher$watchableobject1 = this.watchedObjects.get(datawatcher$watchableobject.getDataValueId());
            if (datawatcher$watchableobject1 == null) continue;
            datawatcher$watchableobject1.setObject(datawatcher$watchableobject.getObject());
            this.owner.onDataWatcherUpdate(datawatcher$watchableobject.getDataValueId());
        }
        this.lock.writeLock().unlock();
        this.objectChanged = true;
    }

    public boolean getIsBlank() {
        return this.isBlank;
    }

    public void func_111144_e() {
        this.objectChanged = false;
    }

    public static class WatchableObject {
        private final int objectType;
        private final int dataValueId;
        private Object watchedObject;
        private boolean watched;

        public WatchableObject(int type, int id2, Object object) {
            this.dataValueId = id2;
            this.watchedObject = object;
            this.objectType = type;
            this.watched = true;
        }

        public int getDataValueId() {
            return this.dataValueId;
        }

        public void setObject(Object object) {
            this.watchedObject = object;
        }

        public Object getObject() {
            return this.watchedObject;
        }

        public int getObjectType() {
            return this.objectType;
        }

        public boolean isWatched() {
            return this.watched;
        }

        public void setWatched(boolean watched) {
            this.watched = watched;
        }
    }
}

