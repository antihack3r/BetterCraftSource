/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ChunkUtils {
    private static ReflectorClass chunkClass = new ReflectorClass(Chunk.class);
    private static ReflectorField fieldHasEntities = ChunkUtils.findFieldHasEntities();
    private static ReflectorField fieldPrecipitationHeightMap = new ReflectorField(chunkClass, int[].class, 0);

    public static boolean hasEntities(Chunk chunk) {
        return Reflector.getFieldValueBoolean(chunk, fieldHasEntities, true);
    }

    public static int getPrecipitationHeight(Chunk chunk, BlockPos pos) {
        int[] aint = (int[])Reflector.getFieldValue(chunk, fieldPrecipitationHeightMap);
        if (aint != null && aint.length == 256) {
            int j2;
            int i2 = pos.getX() & 0xF;
            int k2 = i2 | (j2 = pos.getZ() & 0xF) << 4;
            int l2 = aint[k2];
            if (l2 >= 0) {
                return l2;
            }
            BlockPos blockpos = chunk.getPrecipitationHeight(pos);
            return blockpos.getY();
        }
        return -1;
    }

    private static ReflectorField findFieldHasEntities() {
        try {
            Chunk chunk = new Chunk(null, 0, 0);
            ArrayList<Field> list = new ArrayList<Field>();
            ArrayList<Object> list1 = new ArrayList<Object>();
            Field[] afield = Chunk.class.getDeclaredFields();
            int i2 = 0;
            while (i2 < afield.length) {
                Field field = afield[i2];
                if (field.getType() == Boolean.TYPE) {
                    field.setAccessible(true);
                    list.add(field);
                    list1.add(field.get(chunk));
                }
                ++i2;
            }
            chunk.setHasEntities(false);
            ArrayList<Object> list2 = new ArrayList<Object>();
            for (Object e2 : list) {
                Field field1 = (Field)e2;
                list2.add(field1.get(chunk));
            }
            chunk.setHasEntities(true);
            ArrayList<Object> arrayList = new ArrayList<Object>();
            for (Object e3 : list) {
                Field field2 = (Field)e3;
                arrayList.add(field2.get(chunk));
            }
            ArrayList<Field> arrayList2 = new ArrayList<Field>();
            int j2 = 0;
            while (j2 < list.size()) {
                Field field3 = (Field)list.get(j2);
                Boolean obool = (Boolean)list2.get(j2);
                Boolean obool1 = (Boolean)arrayList.get(j2);
                if (!obool.booleanValue() && obool1.booleanValue()) {
                    arrayList2.add(field3);
                    Boolean obool2 = (Boolean)list1.get(j2);
                    field3.set(chunk, obool2);
                }
                ++j2;
            }
            if (arrayList2.size() == 1) {
                Field field4 = (Field)arrayList2.get(0);
                return new ReflectorField(field4);
            }
        }
        catch (Exception exception) {
            Config.warn(String.valueOf(exception.getClass().getName()) + " " + exception.getMessage());
        }
        Config.warn("Error finding Chunk.hasEntities");
        return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
    }
}

