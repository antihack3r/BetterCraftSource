/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import org.lwjgl.opengl.PixelFormatLWJGL;

public final class PixelFormat
implements PixelFormatLWJGL {
    private int bpp;
    private int alpha;
    private int depth;
    private int stencil;
    private int samples;
    private int colorSamples;
    private int num_aux_buffers;
    private int accum_bpp;
    private int accum_alpha;
    private boolean stereo;
    private boolean floating_point;
    private boolean floating_point_packed;
    private boolean sRGB;

    public PixelFormat() {
        this(0, 8, 0);
    }

    public PixelFormat(int alpha, int depth, int stencil) {
        this(alpha, depth, stencil, 0);
    }

    public PixelFormat(int alpha, int depth, int stencil, int samples) {
        this(0, alpha, depth, stencil, samples);
    }

    public PixelFormat(int bpp2, int alpha, int depth, int stencil, int samples) {
        this(bpp2, alpha, depth, stencil, samples, 0, 0, 0, false);
    }

    public PixelFormat(int bpp2, int alpha, int depth, int stencil, int samples, int num_aux_buffers, int accum_bpp, int accum_alpha, boolean stereo) {
        this(bpp2, alpha, depth, stencil, samples, num_aux_buffers, accum_bpp, accum_alpha, stereo, false);
    }

    public PixelFormat(int bpp2, int alpha, int depth, int stencil, int samples, int num_aux_buffers, int accum_bpp, int accum_alpha, boolean stereo, boolean floating_point) {
        this.bpp = bpp2;
        this.alpha = alpha;
        this.depth = depth;
        this.stencil = stencil;
        this.samples = samples;
        this.num_aux_buffers = num_aux_buffers;
        this.accum_bpp = accum_bpp;
        this.accum_alpha = accum_alpha;
        this.stereo = stereo;
        this.floating_point = floating_point;
        this.floating_point_packed = false;
        this.sRGB = false;
    }

    private PixelFormat(PixelFormat pf2) {
        this.bpp = pf2.bpp;
        this.alpha = pf2.alpha;
        this.depth = pf2.depth;
        this.stencil = pf2.stencil;
        this.samples = pf2.samples;
        this.colorSamples = pf2.colorSamples;
        this.num_aux_buffers = pf2.num_aux_buffers;
        this.accum_bpp = pf2.accum_bpp;
        this.accum_alpha = pf2.accum_alpha;
        this.stereo = pf2.stereo;
        this.floating_point = pf2.floating_point;
        this.floating_point_packed = pf2.floating_point_packed;
        this.sRGB = pf2.sRGB;
    }

    public int getBitsPerPixel() {
        return this.bpp;
    }

    public PixelFormat withBitsPerPixel(int bpp2) {
        if (bpp2 < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel specified: " + bpp2);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.bpp = bpp2;
        return pf2;
    }

    public int getAlphaBits() {
        return this.alpha;
    }

    public PixelFormat withAlphaBits(int alpha) {
        if (alpha < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits specified: " + alpha);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.alpha = alpha;
        return pf2;
    }

    public int getDepthBits() {
        return this.depth;
    }

    public PixelFormat withDepthBits(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Invalid number of depth bits specified: " + depth);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.depth = depth;
        return pf2;
    }

    public int getStencilBits() {
        return this.stencil;
    }

    public PixelFormat withStencilBits(int stencil) {
        if (stencil < 0) {
            throw new IllegalArgumentException("Invalid number of stencil bits specified: " + stencil);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.stencil = stencil;
        return pf2;
    }

    public int getSamples() {
        return this.samples;
    }

    public PixelFormat withSamples(int samples) {
        if (samples < 0) {
            throw new IllegalArgumentException("Invalid number of samples specified: " + samples);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.samples = samples;
        return pf2;
    }

    public PixelFormat withCoverageSamples(int colorSamples) {
        return this.withCoverageSamples(colorSamples, this.samples);
    }

    public PixelFormat withCoverageSamples(int colorSamples, int coverageSamples) {
        if (coverageSamples < 0 || colorSamples < 0 || coverageSamples == 0 && 0 < colorSamples || coverageSamples < colorSamples) {
            throw new IllegalArgumentException("Invalid number of coverage samples specified: " + coverageSamples + " - " + colorSamples);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.samples = coverageSamples;
        pf2.colorSamples = colorSamples;
        return pf2;
    }

    public int getAuxBuffers() {
        return this.num_aux_buffers;
    }

    public PixelFormat withAuxBuffers(int num_aux_buffers) {
        if (num_aux_buffers < 0) {
            throw new IllegalArgumentException("Invalid number of auxiliary buffers specified: " + num_aux_buffers);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.num_aux_buffers = num_aux_buffers;
        return pf2;
    }

    public int getAccumulationBitsPerPixel() {
        return this.accum_bpp;
    }

    public PixelFormat withAccumulationBitsPerPixel(int accum_bpp) {
        if (accum_bpp < 0) {
            throw new IllegalArgumentException("Invalid number of bits per pixel in the accumulation buffer specified: " + accum_bpp);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.accum_bpp = accum_bpp;
        return pf2;
    }

    public int getAccumulationAlpha() {
        return this.accum_alpha;
    }

    public PixelFormat withAccumulationAlpha(int accum_alpha) {
        if (accum_alpha < 0) {
            throw new IllegalArgumentException("Invalid number of alpha bits in the accumulation buffer specified: " + accum_alpha);
        }
        PixelFormat pf2 = new PixelFormat(this);
        pf2.accum_alpha = accum_alpha;
        return pf2;
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public PixelFormat withStereo(boolean stereo) {
        PixelFormat pf2 = new PixelFormat(this);
        pf2.stereo = stereo;
        return pf2;
    }

    public boolean isFloatingPoint() {
        return this.floating_point;
    }

    public PixelFormat withFloatingPoint(boolean floating_point) {
        PixelFormat pf2 = new PixelFormat(this);
        pf2.floating_point = floating_point;
        if (floating_point) {
            pf2.floating_point_packed = false;
        }
        return pf2;
    }

    public PixelFormat withFloatingPointPacked(boolean floating_point_packed) {
        PixelFormat pf2 = new PixelFormat(this);
        pf2.floating_point_packed = floating_point_packed;
        if (floating_point_packed) {
            pf2.floating_point = false;
        }
        return pf2;
    }

    public boolean isSRGB() {
        return this.sRGB;
    }

    public PixelFormat withSRGB(boolean sRGB) {
        PixelFormat pf2 = new PixelFormat(this);
        pf2.sRGB = sRGB;
        return pf2;
    }
}

