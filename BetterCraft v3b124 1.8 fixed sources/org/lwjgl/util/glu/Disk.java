/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

public class Disk
extends Quadric {
    public void draw(float innerRadius, float outerRadius, int slices, int loops) {
        if (this.normals != 100002) {
            if (this.orientation == 100020) {
                GL11.glNormal3f(0.0f, 0.0f, 1.0f);
            } else {
                GL11.glNormal3f(0.0f, 0.0f, -1.0f);
            }
        }
        float da2 = (float)Math.PI * 2 / (float)slices;
        float dr2 = (outerRadius - innerRadius) / (float)loops;
        switch (this.drawStyle) {
            case 100012: {
                float dtc = 2.0f * outerRadius;
                float r1 = innerRadius;
                for (int l2 = 0; l2 < loops; ++l2) {
                    float ca2;
                    float sa2;
                    float a2;
                    int s2;
                    float r2 = r1 + dr2;
                    if (this.orientation == 100020) {
                        GL11.glBegin(8);
                        for (s2 = 0; s2 <= slices; ++s2) {
                            a2 = s2 == slices ? 0.0f : (float)s2 * da2;
                            sa2 = this.sin(a2);
                            ca2 = this.cos(a2);
                            this.TXTR_COORD(0.5f + sa2 * r2 / dtc, 0.5f + ca2 * r2 / dtc);
                            GL11.glVertex2f(r2 * sa2, r2 * ca2);
                            this.TXTR_COORD(0.5f + sa2 * r1 / dtc, 0.5f + ca2 * r1 / dtc);
                            GL11.glVertex2f(r1 * sa2, r1 * ca2);
                        }
                        GL11.glEnd();
                    } else {
                        GL11.glBegin(8);
                        for (s2 = slices; s2 >= 0; --s2) {
                            a2 = s2 == slices ? 0.0f : (float)s2 * da2;
                            sa2 = this.sin(a2);
                            ca2 = this.cos(a2);
                            this.TXTR_COORD(0.5f - sa2 * r2 / dtc, 0.5f + ca2 * r2 / dtc);
                            GL11.glVertex2f(r2 * sa2, r2 * ca2);
                            this.TXTR_COORD(0.5f - sa2 * r1 / dtc, 0.5f + ca2 * r1 / dtc);
                            GL11.glVertex2f(r1 * sa2, r1 * ca2);
                        }
                        GL11.glEnd();
                    }
                    r1 = r2;
                }
                break;
            }
            case 100011: {
                int s3;
                int l3;
                for (l3 = 0; l3 <= loops; ++l3) {
                    float r2 = innerRadius + (float)l3 * dr2;
                    GL11.glBegin(2);
                    for (s3 = 0; s3 < slices; ++s3) {
                        float a3 = (float)s3 * da2;
                        GL11.glVertex2f(r2 * this.sin(a3), r2 * this.cos(a3));
                    }
                    GL11.glEnd();
                }
                for (s3 = 0; s3 < slices; ++s3) {
                    float a4 = (float)s3 * da2;
                    float x2 = this.sin(a4);
                    float y2 = this.cos(a4);
                    GL11.glBegin(3);
                    for (l3 = 0; l3 <= loops; ++l3) {
                        float r3 = innerRadius + (float)l3 * dr2;
                        GL11.glVertex2f(r3 * x2, r3 * y2);
                    }
                    GL11.glEnd();
                }
                break;
            }
            case 100010: {
                GL11.glBegin(0);
                for (int s4 = 0; s4 < slices; ++s4) {
                    float a5 = (float)s4 * da2;
                    float x3 = this.sin(a5);
                    float y3 = this.cos(a5);
                    for (int l4 = 0; l4 <= loops; ++l4) {
                        float r4 = innerRadius * (float)l4 * dr2;
                        GL11.glVertex2f(r4 * x3, r4 * y3);
                    }
                }
                GL11.glEnd();
                break;
            }
            case 100013: {
                float y4;
                float x4;
                float a6;
                if ((double)innerRadius != 0.0) {
                    GL11.glBegin(2);
                    a6 = 0.0f;
                    while ((double)a6 < 6.2831854820251465) {
                        x4 = innerRadius * this.sin(a6);
                        y4 = innerRadius * this.cos(a6);
                        GL11.glVertex2f(x4, y4);
                        a6 += da2;
                    }
                    GL11.glEnd();
                }
                GL11.glBegin(2);
                for (a6 = 0.0f; a6 < (float)Math.PI * 2; a6 += da2) {
                    x4 = outerRadius * this.sin(a6);
                    y4 = outerRadius * this.cos(a6);
                    GL11.glVertex2f(x4, y4);
                }
                GL11.glEnd();
                break;
            }
            default: {
                return;
            }
        }
    }
}

