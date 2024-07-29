/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.util.glu;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.PixelStoreState;
import org.lwjgl.util.glu.Util;

public class MipMap
extends Util {
    public static int gluBuild2DMipmaps(int target, int components, int width, int height, int format, int type, ByteBuffer data) {
        ByteBuffer image;
        int h2;
        if (width < 1 || height < 1) {
            return 100901;
        }
        int bpp2 = MipMap.bytesPerPixel(format, type);
        if (bpp2 == 0) {
            return 100900;
        }
        int maxSize = MipMap.glGetIntegerv(3379);
        int w2 = MipMap.nearestPower(width);
        if (w2 > maxSize) {
            w2 = maxSize;
        }
        if ((h2 = MipMap.nearestPower(height)) > maxSize) {
            h2 = maxSize;
        }
        PixelStoreState pss = new PixelStoreState();
        GL11.glPixelStorei(3330, 0);
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3331, 0);
        GL11.glPixelStorei(3332, 0);
        int retVal = 0;
        boolean done = false;
        if (w2 != width || h2 != height) {
            image = BufferUtils.createByteBuffer((w2 + 4) * h2 * bpp2);
            int error = MipMap.gluScaleImage(format, width, height, type, data, w2, h2, type, image);
            if (error != 0) {
                retVal = error;
                done = true;
            }
            GL11.glPixelStorei(3314, 0);
            GL11.glPixelStorei(3317, 1);
            GL11.glPixelStorei(3315, 0);
            GL11.glPixelStorei(3316, 0);
        } else {
            image = data;
        }
        ByteBuffer bufferA = null;
        ByteBuffer bufferB = null;
        int level = 0;
        while (!done) {
            int newH;
            if (image != data) {
                GL11.glPixelStorei(3314, 0);
                GL11.glPixelStorei(3317, 1);
                GL11.glPixelStorei(3315, 0);
                GL11.glPixelStorei(3316, 0);
            }
            GL11.glTexImage2D(target, level, components, w2, h2, 0, format, type, image);
            if (w2 == 1 && h2 == 1) break;
            int newW = w2 < 2 ? 1 : w2 >> 1;
            int n2 = newH = h2 < 2 ? 1 : h2 >> 1;
            ByteBuffer newImage = bufferA == null ? (bufferA = BufferUtils.createByteBuffer((newW + 4) * newH * bpp2)) : (bufferB == null ? (bufferB = BufferUtils.createByteBuffer((newW + 4) * newH * bpp2)) : bufferB);
            int error = MipMap.gluScaleImage(format, w2, h2, type, image, newW, newH, type, newImage);
            if (error != 0) {
                retVal = error;
                done = true;
            }
            image = newImage;
            if (bufferB != null) {
                bufferB = bufferA;
            }
            w2 = newW;
            h2 = newH;
            ++level;
        }
        pss.save();
        return retVal;
    }

    public static int gluScaleImage(int format, int widthIn, int heightIn, int typein, ByteBuffer dataIn, int widthOut, int heightOut, int typeOut, ByteBuffer dataOut) {
        int j2;
        int i2;
        int k2;
        int sizeout;
        int sizein;
        int components = MipMap.compPerPix(format);
        if (components == -1) {
            return 100900;
        }
        float[] tempIn = new float[widthIn * heightIn * components];
        float[] tempOut = new float[widthOut * heightOut * components];
        switch (typein) {
            case 5121: {
                sizein = 1;
                break;
            }
            case 5126: {
                sizein = 4;
                break;
            }
            default: {
                return 1280;
            }
        }
        switch (typeOut) {
            case 5121: {
                sizeout = 1;
                break;
            }
            case 5126: {
                sizeout = 4;
                break;
            }
            default: {
                return 1280;
            }
        }
        PixelStoreState pss = new PixelStoreState();
        int rowlen = pss.unpackRowLength > 0 ? pss.unpackRowLength : widthIn;
        int rowstride = sizein >= pss.unpackAlignment ? components * rowlen : pss.unpackAlignment / sizein * MipMap.ceil(components * rowlen * sizein, pss.unpackAlignment);
        switch (typein) {
            case 5121: {
                k2 = 0;
                dataIn.rewind();
                for (i2 = 0; i2 < heightIn; ++i2) {
                    int ubptr = i2 * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components;
                    for (j2 = 0; j2 < widthIn * components; ++j2) {
                        tempIn[k2++] = dataIn.get(ubptr++) & 0xFF;
                    }
                }
                break;
            }
            case 5126: {
                k2 = 0;
                dataIn.rewind();
                for (i2 = 0; i2 < heightIn; ++i2) {
                    int fptr = 4 * (i2 * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);
                    for (j2 = 0; j2 < widthIn * components; ++j2) {
                        tempIn[k2++] = dataIn.getFloat(fptr);
                        fptr += 4;
                    }
                }
                break;
            }
            default: {
                return 100900;
            }
        }
        float sx2 = (float)widthIn / (float)widthOut;
        float sy2 = (float)heightIn / (float)heightOut;
        float[] c2 = new float[components];
        for (int iy2 = 0; iy2 < heightOut; ++iy2) {
            for (int ix2 = 0; ix2 < widthOut; ++ix2) {
                int src;
                int ic2;
                int x0 = (int)((float)ix2 * sx2);
                int x1 = (int)((float)(ix2 + 1) * sx2);
                int y0 = (int)((float)iy2 * sy2);
                int y1 = (int)((float)(iy2 + 1) * sy2);
                int readPix = 0;
                for (ic2 = 0; ic2 < components; ++ic2) {
                    c2[ic2] = 0.0f;
                }
                for (int ix0 = x0; ix0 < x1; ++ix0) {
                    for (int iy0 = y0; iy0 < y1; ++iy0) {
                        src = (iy0 * widthIn + ix0) * components;
                        for (int ic3 = 0; ic3 < components; ++ic3) {
                            int n2 = ic3;
                            c2[n2] = c2[n2] + tempIn[src + ic3];
                        }
                        ++readPix;
                    }
                }
                int dst = (iy2 * widthOut + ix2) * components;
                if (readPix == 0) {
                    src = (y0 * widthIn + x0) * components;
                    for (ic2 = 0; ic2 < components; ++ic2) {
                        tempOut[dst++] = tempIn[src + ic2];
                    }
                    continue;
                }
                for (k2 = 0; k2 < components; ++k2) {
                    tempOut[dst++] = c2[k2] / (float)readPix;
                }
            }
        }
        rowlen = pss.packRowLength > 0 ? pss.packRowLength : widthOut;
        rowstride = sizeout >= pss.packAlignment ? components * rowlen : pss.packAlignment / sizeout * MipMap.ceil(components * rowlen * sizeout, pss.packAlignment);
        switch (typeOut) {
            case 5121: {
                k2 = 0;
                for (i2 = 0; i2 < heightOut; ++i2) {
                    int ubptr = i2 * rowstride + pss.packSkipRows * rowstride + pss.packSkipPixels * components;
                    for (j2 = 0; j2 < widthOut * components; ++j2) {
                        dataOut.put(ubptr++, (byte)tempOut[k2++]);
                    }
                }
                break;
            }
            case 5126: {
                k2 = 0;
                for (i2 = 0; i2 < heightOut; ++i2) {
                    int fptr = 4 * (i2 * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);
                    for (j2 = 0; j2 < widthOut * components; ++j2) {
                        dataOut.putFloat(fptr, tempOut[k2++]);
                        fptr += 4;
                    }
                }
                break;
            }
            default: {
                return 100900;
            }
        }
        return 0;
    }
}

