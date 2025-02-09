/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import java.util.Enumeration;
import java.util.Hashtable;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.AudioDeviceFactory;
import javazoom.jl.player.JavaSoundAudioDeviceFactory;

public class FactoryRegistry
extends AudioDeviceFactory {
    private static FactoryRegistry instance = null;
    protected Hashtable factories = new Hashtable();

    public static synchronized FactoryRegistry systemRegistry() {
        if (instance == null) {
            instance = new FactoryRegistry();
            instance.registerDefaultFactories();
        }
        return instance;
    }

    public void addFactory(AudioDeviceFactory factory) {
        this.factories.put(factory.getClass(), factory);
    }

    public void removeFactoryType(Class cls) {
        this.factories.remove(cls);
    }

    public void removeFactory(AudioDeviceFactory factory) {
        this.factories.remove(factory.getClass());
    }

    @Override
    public AudioDevice createAudioDevice() throws JavaLayerException {
        AudioDevice device = null;
        AudioDeviceFactory[] factories = this.getFactoriesPriority();
        if (factories == null) {
            throw new JavaLayerException(this + ": no factories registered");
        }
        JavaLayerException lastEx = null;
        int i2 = 0;
        while (device == null && i2 < factories.length) {
            try {
                device = factories[i2].createAudioDevice();
            }
            catch (JavaLayerException ex2) {
                lastEx = ex2;
            }
            ++i2;
        }
        if (device == null && lastEx != null) {
            throw new JavaLayerException("Cannot create AudioDevice", lastEx);
        }
        return device;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AudioDeviceFactory[] getFactoriesPriority() {
        AudioDeviceFactory[] fa2 = null;
        Hashtable hashtable = this.factories;
        synchronized (hashtable) {
            int size = this.factories.size();
            if (size != 0) {
                fa2 = new AudioDeviceFactory[size];
                int idx = 0;
                Enumeration e2 = this.factories.elements();
                while (e2.hasMoreElements()) {
                    AudioDeviceFactory factory = (AudioDeviceFactory)e2.nextElement();
                    fa2[idx++] = factory;
                }
            }
        }
        return fa2;
    }

    protected void registerDefaultFactories() {
        this.addFactory(new JavaSoundAudioDeviceFactory());
    }
}

