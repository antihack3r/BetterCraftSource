/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple2d;
import javax.vecmath.VecMathUtil;

public abstract class Tuple2f
implements Serializable,
Cloneable {
    static final long serialVersionUID = 9011180388985266884L;
    public float x;
    public float y;

    public Tuple2f(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public Tuple2f(float[] t2) {
        this.x = t2[0];
        this.y = t2[1];
    }

    public Tuple2f(Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public Tuple2f(Tuple2d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
    }

    public Tuple2f() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public final void set(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public final void set(float[] t2) {
        this.x = t2[0];
        this.y = t2[1];
    }

    public final void set(Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public final void set(Tuple2d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
    }

    public final void get(float[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
    }

    public final void add(Tuple2f t1, Tuple2f t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
    }

    public final void add(Tuple2f t1) {
        this.x += t1.x;
        this.y += t1.y;
    }

    public final void sub(Tuple2f t1, Tuple2f t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }

    public final void sub(Tuple2f t1) {
        this.x -= t1.x;
        this.y -= t1.y;
    }

    public final void negate(Tuple2f t1) {
        this.x = -t1.x;
        this.y = -t1.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void scale(float s2, Tuple2f t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
    }

    public final void scale(float s2) {
        this.x *= s2;
        this.y *= s2;
    }

    public final void scaleAdd(float s2, Tuple2f t1, Tuple2f t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
    }

    public final void scaleAdd(float s2, Tuple2f t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
    }

    public int hashCode() {
        long bits = 1L;
        bits = VecMathUtil.hashFloatBits(bits, this.x);
        bits = VecMathUtil.hashFloatBits(bits, this.y);
        return VecMathUtil.hashFinish(bits);
    }

    public boolean equals(Tuple2f t1) {
        try {
            return this.x == t1.x && this.y == t1.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple2f t2 = (Tuple2f)t1;
            return this.x == t2.x && this.y == t2.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple2f t1, float epsilon) {
        float diff = this.x - t1.x;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f2 = diff < 0.0f ? -diff : diff;
        if (f2 > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f3 = diff < 0.0f ? -diff : diff;
        return !(f3 > epsilon);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public final void clamp(float min, float max, Tuple2f t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
    }

    public final void clampMin(float min, Tuple2f t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
    }

    public final void clampMax(float max, Tuple2f t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
    }

    public final void absolute(Tuple2f t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
    }

    public final void clamp(float min, float max) {
        if (this.x > max) {
            this.x = max;
        } else if (this.x < min) {
            this.x = min;
        }
        if (this.y > max) {
            this.y = max;
        } else if (this.y < min) {
            this.y = min;
        }
    }

    public final void clampMin(float min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
    }

    public final void clampMax(float max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }

    public final void interpolate(Tuple2f t1, Tuple2f t2, float alpha) {
        this.x = (1.0f - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0f - alpha) * t1.y + alpha * t2.y;
    }

    public final void interpolate(Tuple2f t1, float alpha) {
        this.x = (1.0f - alpha) * this.x + alpha * t1.x;
        this.y = (1.0f - alpha) * this.y + alpha * t1.y;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float x2) {
        this.x = x2;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float y2) {
        this.y = y2;
    }
}

