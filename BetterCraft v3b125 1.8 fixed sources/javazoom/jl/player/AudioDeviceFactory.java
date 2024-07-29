/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;

public abstract class AudioDeviceFactory {
    public abstract AudioDevice createAudioDevice() throws JavaLayerException;

    protected AudioDevice instantiate(ClassLoader loader, String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        AudioDevice dev = null;
        Class<?> cls = null;
        cls = loader == null ? Class.forName(name) : loader.loadClass(name);
        Object o2 = cls.newInstance();
        dev = (AudioDevice)o2;
        return dev;
    }
}

