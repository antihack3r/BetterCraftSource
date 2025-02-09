/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player.advanced;

import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class AdvancedPlayer {
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean closed = false;
    private boolean complete = false;
    private int lastPosition = 0;
    private PlaybackListener listener;

    public AdvancedPlayer(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    public AdvancedPlayer(InputStream stream, AudioDevice device) throws JavaLayerException {
        this.bitstream = new Bitstream(stream);
        this.audio = device != null ? device : FactoryRegistry.systemRegistry().createAudioDevice();
        this.decoder = new Decoder();
        this.audio.open(this.decoder);
    }

    public void play() throws JavaLayerException {
        this.play(Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        if (this.listener != null) {
            this.listener.playbackStarted(this.createEvent(PlaybackEvent.STARTED));
        }
        while (frames-- > 0 && ret) {
            ret = this.decodeFrame();
        }
        AudioDevice out = this.audio;
        if (out != null) {
            out.flush();
            AdvancedPlayer advancedPlayer = this;
            synchronized (advancedPlayer) {
                this.complete = !this.closed;
                this.close();
            }
            if (this.listener != null) {
                this.listener.playbackFinished(this.createEvent(out, PlaybackEvent.STOPPED));
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
        AdvancedPlayer advancedPlayer = this;
        synchronized (advancedPlayer) {
            out = this.audio;
            if (out != null) {
                out.write(output.getBuffer(), 0, output.getBufferLength());
            }
        }
        this.bitstream.closeFrame();
        return true;
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header h2 = this.bitstream.readFrame();
        if (h2 == null) {
            return false;
        }
        this.bitstream.closeFrame();
        return true;
    }

    public boolean play(int start, int end) throws JavaLayerException {
        boolean ret = true;
        int offset = start;
        while (offset-- > 0 && ret) {
            ret = this.skipFrame();
        }
        return this.play(end - start);
    }

    private PlaybackEvent createEvent(int id2) {
        return this.createEvent(this.audio, id2);
    }

    private PlaybackEvent createEvent(AudioDevice dev, int id2) {
        return new PlaybackEvent(this, id2, dev.getPosition());
    }

    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public PlaybackListener getPlayBackListener() {
        return this.listener;
    }

    public void stop() {
        this.listener.playbackFinished(this.createEvent(PlaybackEvent.STOPPED));
        this.close();
    }
}

