/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUmesh;
import org.lwjgl.util.glu.tessellation.Geom;
import org.lwjgl.util.glu.tessellation.Mesh;

class TessMono {
    TessMono() {
    }

    static boolean __gl_meshTessellateMonoRegion(GLUface face) {
        GLUhalfEdge tempHalfEdge;
        GLUhalfEdge up2 = face.anEdge;
        assert (up2.Lnext != up2 && up2.Lnext.Lnext != up2);
        while (Geom.VertLeq(up2.Sym.Org, up2.Org)) {
            up2 = up2.Onext.Sym;
        }
        while (Geom.VertLeq(up2.Org, up2.Sym.Org)) {
            up2 = up2.Lnext;
        }
        GLUhalfEdge lo2 = up2.Onext.Sym;
        while (up2.Lnext != lo2) {
            if (Geom.VertLeq(up2.Sym.Org, lo2.Org)) {
                while (lo2.Lnext != up2 && (Geom.EdgeGoesLeft(lo2.Lnext) || Geom.EdgeSign(lo2.Org, lo2.Sym.Org, lo2.Lnext.Sym.Org) <= 0.0)) {
                    tempHalfEdge = Mesh.__gl_meshConnect(lo2.Lnext, lo2);
                    if (tempHalfEdge == null) {
                        return false;
                    }
                    lo2 = tempHalfEdge.Sym;
                }
                lo2 = lo2.Onext.Sym;
                continue;
            }
            while (lo2.Lnext != up2 && (Geom.EdgeGoesRight(up2.Onext.Sym) || Geom.EdgeSign(up2.Sym.Org, up2.Org, up2.Onext.Sym.Org) >= 0.0)) {
                tempHalfEdge = Mesh.__gl_meshConnect(up2, up2.Onext.Sym);
                if (tempHalfEdge == null) {
                    return false;
                }
                up2 = tempHalfEdge.Sym;
            }
            up2 = up2.Lnext;
        }
        assert (lo2.Lnext != up2);
        while (lo2.Lnext.Lnext != up2) {
            tempHalfEdge = Mesh.__gl_meshConnect(lo2.Lnext, lo2);
            if (tempHalfEdge == null) {
                return false;
            }
            lo2 = tempHalfEdge.Sym;
        }
        return true;
    }

    public static boolean __gl_meshTessellateInterior(GLUmesh mesh) {
        GLUface f2 = mesh.fHead.next;
        while (f2 != mesh.fHead) {
            GLUface next = f2.next;
            if (f2.inside && !TessMono.__gl_meshTessellateMonoRegion(f2)) {
                return false;
            }
            f2 = next;
        }
        return true;
    }

    public static void __gl_meshDiscardExterior(GLUmesh mesh) {
        GLUface f2 = mesh.fHead.next;
        while (f2 != mesh.fHead) {
            GLUface next = f2.next;
            if (!f2.inside) {
                Mesh.__gl_meshZapFace(f2);
            }
            f2 = next;
        }
    }

    public static boolean __gl_meshSetWindingNumber(GLUmesh mesh, int value, boolean keepOnlyBoundary) {
        GLUhalfEdge e2 = mesh.eHead.next;
        while (e2 != mesh.eHead) {
            GLUhalfEdge eNext = e2.next;
            if (e2.Sym.Lface.inside != e2.Lface.inside) {
                e2.winding = e2.Lface.inside ? value : -value;
            } else if (!keepOnlyBoundary) {
                e2.winding = 0;
            } else if (!Mesh.__gl_meshDelete(e2)) {
                return false;
            }
            e2 = eNext;
        }
        return true;
    }
}

