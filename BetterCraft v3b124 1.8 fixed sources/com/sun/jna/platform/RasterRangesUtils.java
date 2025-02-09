/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RasterRangesUtils {
    private static final int[] subColMasks = new int[]{128, 64, 32, 16, 8, 4, 2, 1};
    private static final Comparator<Object> COMPARATOR = new Comparator<Object>(){

        @Override
        public int compare(Object o1, Object o2) {
            return ((Rectangle)o1).x - ((Rectangle)o2).x;
        }
    };

    public static boolean outputOccupiedRanges(Raster raster, RangesOutput out) {
        DataBuffer data;
        boolean hasAlpha;
        Rectangle bounds = raster.getBounds();
        SampleModel sampleModel = raster.getSampleModel();
        boolean bl2 = hasAlpha = sampleModel.getNumBands() == 4;
        if (raster.getParent() == null && bounds.x == 0 && bounds.y == 0 && (data = raster.getDataBuffer()).getNumBanks() == 1) {
            if (sampleModel instanceof MultiPixelPackedSampleModel) {
                MultiPixelPackedSampleModel packedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
                if (packedSampleModel.getPixelBitStride() == 1) {
                    return RasterRangesUtils.outputOccupiedRangesOfBinaryPixels(((DataBufferByte)data).getData(), bounds.width, bounds.height, out);
                }
            } else if (sampleModel instanceof SinglePixelPackedSampleModel && sampleModel.getDataType() == 3) {
                return RasterRangesUtils.outputOccupiedRanges(((DataBufferInt)data).getData(), bounds.width, bounds.height, hasAlpha ? -16777216 : 0xFFFFFF, out);
            }
        }
        int[] pixels = raster.getPixels(0, 0, bounds.width, bounds.height, (int[])null);
        return RasterRangesUtils.outputOccupiedRanges(pixels, bounds.width, bounds.height, hasAlpha ? -16777216 : 0xFFFFFF, out);
    }

    public static boolean outputOccupiedRangesOfBinaryPixels(byte[] binaryBits, int w2, int h2, RangesOutput out) {
        HashSet<Object> rects = new HashSet<Object>();
        TreeSet<Object> prevLine = Collections.EMPTY_SET;
        int scanlineBytes = binaryBits.length / h2;
        for (int row = 0; row < h2; ++row) {
            TreeSet<Object> treeSet = new TreeSet<Object>(COMPARATOR);
            int rowOffsetBytes = row * scanlineBytes;
            int startCol = -1;
            for (int byteCol = 0; byteCol < scanlineBytes; ++byteCol) {
                int firstByteCol = byteCol << 3;
                byte byteColBits = binaryBits[rowOffsetBytes + byteCol];
                if (byteColBits == 0) {
                    if (startCol < 0) continue;
                    treeSet.add(new Rectangle(startCol, row, firstByteCol - startCol, 1));
                    startCol = -1;
                    continue;
                }
                if (byteColBits == 255) {
                    if (startCol >= 0) continue;
                    startCol = firstByteCol;
                    continue;
                }
                for (int subCol = 0; subCol < 8; ++subCol) {
                    int col = firstByteCol | subCol;
                    if ((byteColBits & subColMasks[subCol]) != 0) {
                        if (startCol >= 0) continue;
                        startCol = col;
                        continue;
                    }
                    if (startCol < 0) continue;
                    treeSet.add(new Rectangle(startCol, row, col - startCol, 1));
                    startCol = -1;
                }
            }
            if (startCol >= 0) {
                treeSet.add(new Rectangle(startCol, row, w2 - startCol, 1));
            }
            Set<Rectangle> unmerged = RasterRangesUtils.mergeRects((Set<Rectangle>)prevLine, treeSet);
            rects.addAll(unmerged);
            prevLine = treeSet;
        }
        rects.addAll(prevLine);
        for (Rectangle rectangle : rects) {
            if (out.outputRange(rectangle.x, rectangle.y, rectangle.width, rectangle.height)) continue;
            return false;
        }
        return true;
    }

    public static boolean outputOccupiedRanges(int[] pixels, int w2, int h2, int occupationMask, RangesOutput out) {
        HashSet<Object> rects = new HashSet<Object>();
        TreeSet<Object> prevLine = Collections.EMPTY_SET;
        for (int row = 0; row < h2; ++row) {
            TreeSet<Object> treeSet = new TreeSet<Object>(COMPARATOR);
            int idxOffset = row * w2;
            int startCol = -1;
            for (int col = 0; col < w2; ++col) {
                if ((pixels[idxOffset + col] & occupationMask) != 0) {
                    if (startCol >= 0) continue;
                    startCol = col;
                    continue;
                }
                if (startCol < 0) continue;
                treeSet.add(new Rectangle(startCol, row, col - startCol, 1));
                startCol = -1;
            }
            if (startCol >= 0) {
                treeSet.add(new Rectangle(startCol, row, w2 - startCol, 1));
            }
            Set<Rectangle> unmerged = RasterRangesUtils.mergeRects((Set<Rectangle>)prevLine, treeSet);
            rects.addAll(unmerged);
            prevLine = treeSet;
        }
        rects.addAll(prevLine);
        for (Rectangle rectangle : rects) {
            if (out.outputRange(rectangle.x, rectangle.y, rectangle.width, rectangle.height)) continue;
            return false;
        }
        return true;
    }

    private static Set<Rectangle> mergeRects(Set<Rectangle> prev, Set<Rectangle> current) {
        HashSet<Rectangle> unmerged = new HashSet<Rectangle>(prev);
        if (!prev.isEmpty() && !current.isEmpty()) {
            Rectangle[] pr2 = prev.toArray(new Rectangle[prev.size()]);
            Rectangle[] cr2 = current.toArray(new Rectangle[current.size()]);
            int ipr = 0;
            int icr = 0;
            while (ipr < pr2.length && icr < cr2.length) {
                while (cr2[icr].x < pr2[ipr].x) {
                    if (++icr != cr2.length) continue;
                    return unmerged;
                }
                if (cr2[icr].x == pr2[ipr].x && cr2[icr].width == pr2[ipr].width) {
                    unmerged.remove(pr2[ipr]);
                    cr2[icr].y = pr2[ipr].y;
                    cr2[icr].height = pr2[ipr].height + 1;
                    ++icr;
                    continue;
                }
                ++ipr;
            }
        }
        return unmerged;
    }

    public static interface RangesOutput {
        public boolean outputRange(int var1, int var2, int var3, int var4);
    }
}

