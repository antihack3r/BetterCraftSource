/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.render.VboRange;
import net.optifine.util.LinkedList;

public class VboRegion {
    private EnumWorldBlockLayer layer = null;
    private int glBufferId = OpenGlHelper.glGenBuffers();
    private int capacity = 4096;
    private int positionTop = 0;
    private int sizeUsed;
    private LinkedList<VboRange> rangeList = new LinkedList();
    private VboRange compactRangeLast = null;
    private IntBuffer bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
    private IntBuffer bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
    private int drawMode = 7;
    private final int vertexBytes = DefaultVertexFormats.BLOCK.getNextOffset();

    public VboRegion(EnumWorldBlockLayer layer) {
        this.layer = layer;
        this.bindBuffer();
        long i2 = this.toBytes(this.capacity);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, i2, OpenGlHelper.GL_STATIC_DRAW);
        this.unbindBuffer();
    }

    public void bufferData(ByteBuffer data, VboRange range) {
        int i2 = range.getPosition();
        int j2 = range.getSize();
        int k2 = this.toVertex(data.limit());
        if (k2 <= 0) {
            if (i2 >= 0) {
                range.setPosition(-1);
                range.setSize(0);
                this.rangeList.remove(range.getNode());
                this.sizeUsed -= j2;
            }
        } else {
            if (k2 > j2) {
                range.setPosition(this.positionTop);
                range.setSize(k2);
                this.positionTop += k2;
                if (i2 >= 0) {
                    this.rangeList.remove(range.getNode());
                }
                this.rangeList.addLast(range.getNode());
            }
            range.setSize(k2);
            this.sizeUsed += k2 - j2;
            this.checkVboSize(range.getPositionNext());
            long l2 = this.toBytes(range.getPosition());
            this.bindBuffer();
            OpenGlHelper.glBufferSubData(OpenGlHelper.GL_ARRAY_BUFFER, l2, data);
            this.unbindBuffer();
            if (this.positionTop > this.sizeUsed * 11 / 10) {
                this.compactRanges(1);
            }
        }
    }

    private void compactRanges(int countMax) {
        if (!this.rangeList.isEmpty()) {
            VboRange vborange = this.compactRangeLast;
            if (vborange == null || !this.rangeList.contains(vborange.getNode())) {
                vborange = this.rangeList.getFirst().getItem();
            }
            int i2 = vborange.getPosition();
            VboRange vborange1 = vborange.getPrev();
            i2 = vborange1 == null ? 0 : vborange1.getPositionNext();
            int j2 = 0;
            while (vborange != null && j2 < countMax) {
                ++j2;
                if (vborange.getPosition() == i2) {
                    i2 += vborange.getSize();
                    vborange = vborange.getNext();
                    continue;
                }
                int k2 = vborange.getPosition() - i2;
                if (vborange.getSize() <= k2) {
                    this.copyVboData(vborange.getPosition(), i2, vborange.getSize());
                    vborange.setPosition(i2);
                    i2 += vborange.getSize();
                    vborange = vborange.getNext();
                    continue;
                }
                this.checkVboSize(this.positionTop + vborange.getSize());
                this.copyVboData(vborange.getPosition(), this.positionTop, vborange.getSize());
                vborange.setPosition(this.positionTop);
                this.positionTop += vborange.getSize();
                VboRange vborange2 = vborange.getNext();
                this.rangeList.remove(vborange.getNode());
                this.rangeList.addLast(vborange.getNode());
                vborange = vborange2;
            }
            if (vborange == null) {
                this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
            }
            this.compactRangeLast = vborange;
        }
    }

    private void checkRanges() {
        int i2 = 0;
        int j2 = 0;
        VboRange vborange = this.rangeList.getFirst().getItem();
        while (vborange != null) {
            ++i2;
            j2 += vborange.getSize();
            if (vborange.getPosition() < 0 || vborange.getSize() <= 0 || vborange.getPositionNext() > this.positionTop) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
            VboRange vborange1 = vborange.getPrev();
            if (vborange1 != null && vborange.getPosition() < vborange1.getPositionNext()) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
            VboRange vborange2 = vborange.getNext();
            if (vborange2 != null && vborange.getPositionNext() > vborange2.getPosition()) {
                throw new RuntimeException("Invalid range: " + vborange);
            }
            vborange = vborange.getNext();
        }
        if (i2 != this.rangeList.getSize()) {
            throw new RuntimeException("Invalid count: " + i2 + " <> " + this.rangeList.getSize());
        }
        if (j2 != this.sizeUsed) {
            throw new RuntimeException("Invalid size: " + j2 + " <> " + this.sizeUsed);
        }
    }

    private void checkVboSize(int sizeMin) {
        if (this.capacity < sizeMin) {
            this.expandVbo(sizeMin);
        }
    }

    private void copyVboData(int posFrom, int posTo, int size) {
        long i2 = this.toBytes(posFrom);
        long j2 = this.toBytes(posTo);
        long k2 = this.toBytes(size);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, this.glBufferId);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, i2, j2, k2);
        Config.checkGlError("Copy VBO range");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
    }

    private void expandVbo(int sizeMin) {
        int i2 = this.capacity * 6 / 4;
        while (i2 < sizeMin) {
            i2 = i2 * 6 / 4;
        }
        long j2 = this.toBytes(this.capacity);
        long k2 = this.toBytes(i2);
        int l2 = OpenGlHelper.glGenBuffers();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, l2);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, k2, OpenGlHelper.GL_STATIC_DRAW);
        Config.checkGlError("Expand VBO");
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, l2);
        OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, 0L, 0L, j2);
        Config.checkGlError("Copy VBO: " + k2);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
        OpenGlHelper.glDeleteBuffers(this.glBufferId);
        this.bufferIndexVertex = Config.createDirectIntBuffer(i2);
        this.bufferCountVertex = Config.createDirectIntBuffer(i2);
        this.glBufferId = l2;
        this.capacity = i2;
    }

    public void bindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
    }

    public void drawArrays(int drawMode, VboRange range) {
        if (this.drawMode != drawMode) {
            if (this.bufferIndexVertex.position() > 0) {
                throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + drawMode);
            }
            this.drawMode = drawMode;
        }
        this.bufferIndexVertex.put(range.getPosition());
        this.bufferCountVertex.put(range.getSize());
    }

    public void finishDraw(VboRenderList vboRenderList) {
        this.bindBuffer();
        vboRenderList.setupArrayPointers();
        this.bufferIndexVertex.flip();
        this.bufferCountVertex.flip();
        GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
        this.bufferIndexVertex.limit(this.bufferIndexVertex.capacity());
        this.bufferCountVertex.limit(this.bufferCountVertex.capacity());
        if (this.positionTop > this.sizeUsed * 11 / 10) {
            this.compactRanges(1);
        }
    }

    public void unbindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }

    public void deleteGlBuffers() {
        if (this.glBufferId >= 0) {
            OpenGlHelper.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }

    private long toBytes(int vertex) {
        return (long)vertex * (long)this.vertexBytes;
    }

    private int toVertex(long bytes) {
        return (int)(bytes / (long)this.vertexBytes);
    }

    public int getPositionTop() {
        return this.positionTop;
    }
}

