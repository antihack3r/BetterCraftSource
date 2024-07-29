/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormat {
    public static final int vertexSizeBlock = 14;
    public static final int offsetMidTexCoord = 8;
    public static final int offsetTangent = 10;
    public static final int offsetEntity = 12;
    public static final VertexFormat defVertexFormatTextured = SVertexFormat.makeDefVertexFormatTextured();

    public static VertexFormat makeDefVertexFormatBlock() {
        VertexFormat vertexformat = new VertexFormat();
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUsage.COLOR, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2));
        vertexformat.addElement(new VertexFormatElement(1, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.UV, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.NORMAL, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.PADDING, 1));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.PADDING, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        return vertexformat;
    }

    public static VertexFormat makeDefVertexFormatItem() {
        VertexFormat vertexformat = new VertexFormat();
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUsage.COLOR, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.NORMAL, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.PADDING, 1));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.PADDING, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        return vertexformat;
    }

    public static VertexFormat makeDefVertexFormatTextured() {
        VertexFormat vertexformat = new VertexFormat();
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUsage.PADDING, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.NORMAL, 3));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.PADDING, 1));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.PADDING, 2));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        vertexformat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        return vertexformat;
    }

    public static void setDefBakedFormat(VertexFormat vf2) {
        if (vf2 != null) {
            vf2.clear();
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUsage.COLOR, 4));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 2));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.NORMAL, 3));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUsage.PADDING, 1));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.PADDING, 2));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
            vf2.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUsage.PADDING, 4));
        }
    }

    public static VertexFormat duplicate(VertexFormat src) {
        if (src == null) {
            return null;
        }
        VertexFormat vertexformat = new VertexFormat();
        SVertexFormat.copy(src, vertexformat);
        return vertexformat;
    }

    public static void copy(VertexFormat src, VertexFormat dst) {
        if (src != null && dst != null) {
            dst.clear();
            int i2 = 0;
            while (i2 < src.getElementCount()) {
                dst.addElement(src.getElement(i2));
                ++i2;
            }
        }
    }
}

