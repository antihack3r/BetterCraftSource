/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

public class JavaSoundAudioDevice
extends AudioDeviceBase {
    private SourceDataLine source = null;
    private AudioFormat fmt = null;
    private byte[] byteBuf = new byte[4096];

    protected void setAudioFormat(AudioFormat fmt0) {
        this.fmt = fmt0;
    }

    protected AudioFormat getAudioFormat() {
        this.fmt = new AudioFormat(44100.0f, 16, 2, true, false);
        return this.fmt;
    }

    protected DataLine.Info getSourceLineInfo() {
        AudioFormat fmt = this.getAudioFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        return info;
    }

    public void open(AudioFormat fmt) throws JavaLayerException {
        if (!this.isOpen()) {
            this.setAudioFormat(fmt);
            this.openImpl();
            this.setOpen(true);
        }
    }

    public boolean setLineGain(float gain) {
        if (this.source != null) {
            FloatControl volControl = (FloatControl)this.source.getControl(FloatControl.Type.MASTER_GAIN);
            float newGain = Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum());
            volControl.setValue(newGain);
            return true;
        }
        return false;
    }

    @Override
    public void openImpl() throws JavaLayerException {
    }

    public void createSource() throws JavaLayerException {
        Throwable t2 = null;
        try {
            Line line = AudioSystem.getLine(this.getSourceLineInfo());
            if (line instanceof SourceDataLine) {
                this.source = (SourceDataLine)line;
                this.source.open(this.fmt);
                this.source.start();
            }
        }
        catch (RuntimeException ex2) {
            t2 = ex2;
        }
        catch (LinkageError ex3) {
            t2 = ex3;
        }
        catch (LineUnavailableException ex4) {
            t2 = ex4;
        }
        if (this.source == null) {
            throw new JavaLayerException("cannot obtain source audio line", t2);
        }
    }

    public int millisecondsToBytes(AudioFormat fmt, int time) {
        return (int)((double)((float)time * (fmt.getSampleRate() * (float)fmt.getChannels() * (float)fmt.getSampleSizeInBits())) / 8000.0);
    }

    @Override
    protected void closeImpl() {
        if (this.source != null) {
            this.source.close();
        }
    }

    @Override
    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
        if (this.source == null) {
            this.createSource();
        }
        byte[] b2 = this.toByteArray(samples, offs, len);
        this.source.write(b2, 0, len * 2);
    }

    protected byte[] getByteArray(int length) {
        if (this.byteBuf.length < length) {
            this.byteBuf = new byte[length + 1024];
        }
        return this.byteBuf;
    }

    protected byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b2 = this.getByteArray(len * 2);
        int idx = 0;
        while (len-- > 0) {
            short s2 = samples[offs++];
            b2[idx++] = (byte)s2;
            b2[idx++] = (byte)(s2 >>> 8);
        }
        return b2;
    }

    @Override
    protected void flushImpl() {
        if (this.source != null) {
            this.source.drain();
        }
    }

    @Override
    public int getPosition() {
        int pos = 0;
        if (this.source != null) {
            pos = (int)(this.source.getMicrosecondPosition() / 1000L);
        }
        return pos;
    }

    public void test() throws JavaLayerException {
        this.open(new AudioFormat(22000.0f, 16, 1, true, false));
        short[] data = new short[2200];
        this.write(data, 0, data.length);
        this.flush();
        this.close();
    }
}

