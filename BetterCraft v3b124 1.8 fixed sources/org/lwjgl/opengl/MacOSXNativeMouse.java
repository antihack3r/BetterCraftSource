/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.EventQueue;
import org.lwjgl.opengl.MacOSXDisplay;

final class MacOSXNativeMouse
extends EventQueue {
    private static final int WHEEL_SCALE = 120;
    private static final int NUM_BUTTONS = 3;
    private ByteBuffer window_handle;
    private MacOSXDisplay display;
    private boolean grabbed;
    private float accum_dx;
    private float accum_dy;
    private int accum_dz;
    private float last_x;
    private float last_y;
    private boolean saved_control_state;
    private final ByteBuffer event = ByteBuffer.allocate(22);
    private IntBuffer delta_buffer = BufferUtils.createIntBuffer(2);
    private int skip_event;
    private final byte[] buttons = new byte[3];

    MacOSXNativeMouse(MacOSXDisplay display, ByteBuffer window_handle) {
        super(22);
        this.display = display;
        this.window_handle = window_handle;
    }

    private native void nSetCursorPosition(ByteBuffer var1, int var2, int var3);

    public static native void nGrabMouse(boolean var0);

    private native void nRegisterMouseListener(ByteBuffer var1);

    private native void nUnregisterMouseListener(ByteBuffer var1);

    private static native long nCreateCursor(int var0, int var1, int var2, int var3, int var4, IntBuffer var5, int var6, IntBuffer var7, int var8) throws LWJGLException;

    private static native void nDestroyCursor(long var0);

    private static native void nSetCursor(long var0) throws LWJGLException;

    public synchronized void register() {
        this.nRegisterMouseListener(this.window_handle);
    }

    public static long createCursor(int width, int height, int xHotspot, int yHotspot, int numImages, IntBuffer images, IntBuffer delays) throws LWJGLException {
        return MacOSXNativeMouse.nCreateCursor(width, height, xHotspot, yHotspot, numImages, images, images.position(), delays, delays != null ? delays.position() : -1);
    }

    public static void destroyCursor(long cursor_handle) {
        MacOSXNativeMouse.nDestroyCursor(cursor_handle);
    }

    public static void setCursor(long cursor_handle) throws LWJGLException {
        MacOSXNativeMouse.nSetCursor(cursor_handle);
    }

    public synchronized void setCursorPosition(int x2, int y2) {
        this.nSetCursorPosition(this.window_handle, x2, y2);
    }

    public synchronized void unregister() {
        this.nUnregisterMouseListener(this.window_handle);
    }

    public synchronized void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
        MacOSXNativeMouse.nGrabMouse(grabbed);
        this.skip_event = 1;
        this.accum_dy = 0.0f;
        this.accum_dx = 0.0f;
    }

    public synchronized boolean isGrabbed() {
        return this.grabbed;
    }

    protected void resetCursorToCenter() {
        this.clearEvents();
        this.accum_dy = 0.0f;
        this.accum_dx = 0.0f;
        if (this.display != null) {
            this.last_x = this.display.getWidth() / 2;
            this.last_y = this.display.getHeight() / 2;
        }
    }

    private void putMouseEvent(byte button, byte state, int dz2, long nanos) {
        if (this.grabbed) {
            this.putMouseEventWithCoords(button, state, 0, 0, dz2, nanos);
        } else {
            this.putMouseEventWithCoords(button, state, (int)this.last_x, (int)this.last_y, dz2, nanos);
        }
    }

    protected void putMouseEventWithCoords(byte button, byte state, int coord1, int coord2, int dz2, long nanos) {
        this.event.clear();
        this.event.put(button).put(state).putInt(coord1).putInt(coord2).putInt(dz2).putLong(nanos);
        this.event.flip();
        this.putEvent(this.event);
    }

    public synchronized void poll(IntBuffer coord_buffer, ByteBuffer buttons_buffer) {
        if (this.grabbed) {
            coord_buffer.put(0, (int)this.accum_dx);
            coord_buffer.put(1, (int)this.accum_dy);
        } else {
            coord_buffer.put(0, (int)this.last_x);
            coord_buffer.put(1, (int)this.last_y);
        }
        coord_buffer.put(2, this.accum_dz);
        this.accum_dz = 0;
        this.accum_dx = this.accum_dy = (float)0;
        int old_position = buttons_buffer.position();
        buttons_buffer.put(this.buttons, 0, this.buttons.length);
        buttons_buffer.position(old_position);
    }

    private void setCursorPos(float x2, float y2, long nanos) {
        if (this.grabbed) {
            return;
        }
        float dx2 = x2 - this.last_x;
        float dy2 = y2 - this.last_y;
        this.addDelta(dx2, dy2);
        this.last_x = x2;
        this.last_y = y2;
        this.putMouseEventWithCoords((byte)-1, (byte)0, (int)x2, (int)y2, 0, nanos);
    }

    protected void addDelta(float dx2, float dy2) {
        this.accum_dx += dx2;
        this.accum_dy += -dy2;
    }

    public synchronized void setButton(int button, int state, long nanos) {
        this.buttons[button] = (byte)state;
        this.putMouseEvent((byte)button, (byte)state, 0, nanos);
    }

    public synchronized void mouseMoved(float x2, float y2, float dx2, float dy2, float dz2, long nanos) {
        if (this.skip_event > 0) {
            --this.skip_event;
            if (this.skip_event == 0) {
                this.last_x = x2;
                this.last_y = y2;
            }
            return;
        }
        if (dz2 != 0.0f) {
            if (dy2 == 0.0f) {
                dy2 = dx2;
            }
            int wheel_amount = (int)(dy2 * 120.0f);
            this.accum_dz += wheel_amount;
            this.putMouseEvent((byte)-1, (byte)0, wheel_amount, nanos);
        } else if (this.grabbed) {
            if (dx2 != 0.0f || dy2 != 0.0f) {
                this.putMouseEventWithCoords((byte)-1, (byte)0, (int)dx2, (int)(-dy2), 0, nanos);
                this.addDelta(dx2, dy2);
            }
        } else {
            this.setCursorPos(x2, y2, nanos);
        }
    }
}

