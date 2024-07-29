/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;

public class Player {
    private int frame = 0;
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean closed = false;
    private boolean complete = false;
    private int lastPosition = 0;

    public Player(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    public Player(InputStream stream, AudioDevice device) throws JavaLayerException {
        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();
        if (device != null) {
            this.audio = device;
        } else {
            FactoryRegistry r2 = FactoryRegistry.systemRegistry();
            this.audio = r2.createAudioDevice();
        }
        this.audio.open(this.decoder);
    }

    public void play() throws JavaLayerException {
        this.play(Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean play(int frames) throws JavaLayerException {
        AudioDevice out;
        boolean ret = true;
        while (frames-- > 0 && ret) {
            ret = this.decodeFrame();
        }
        if (!ret && (out = this.audio) != null) {
            out.flush();
            Player player = this;
            synchronized (player) {
                this.complete = !this.closed;
                this.close();
            }
        }
        return ret;
    }

    public synchronized void close() {
        AudioDevice out = this.audio;
        if (out != null) {
            this.closed = true;
            this.audio = null;
            out.close();
            this.lastPosition = out.getPosition();
            try {
                this.bitstream.close();
            }
            catch (BitstreamException bitstreamException) {
                // empty catch block
            }
        }
    }

    public synchronized boolean isComplete() {
        return this.complete;
    }

    public int getPosition() {
        int position = this.lastPosition;
        AudioDevice out = this.audio;
        if (out != null) {
            position = out.getPosition();
        }
        return position;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean decodeFrame() throws JavaLayerException {
        Header h2;
        AudioDevice out;
        block9: {
            block8: {
                try {
                    out = this.audio;
                    if (out != null) break block8;
                    return false;
                }
                catch (RuntimeException ex2) {
                    throw new JavaLayerException("Exception decoding audio frame", ex2);
                }
            }
            h2 = this.bitstream.readFrame();
            if (h2 != null) break block9;
            return false;
        }
        SampleBuffer output = (SampleBuffer)this.decoder.decodeFrame(h2, this.bitstream);
        Player player = this;
        synchronized (player) {
            out = this.audio;
            if (out != null) {
                out.write(output.getBuffer(), 0, output.getBufferLength());
            }
        }
        this.bitstream.closeFrame();
        return true;
    }

    public boolean setGain(float newGain) {
        if (this.audio instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice jsAudio = (JavaSoundAudioDevice)this.audio;
            try {
                jsAudio.write(null, 0, 0);
            }
            catch (JavaLayerException ex2) {
                ex2.printStackTrace();
            }
            return jsAudio.setLineGain(newGain);
        }
        return false;
    }
}

