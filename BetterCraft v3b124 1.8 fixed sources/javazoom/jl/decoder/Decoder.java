/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.DecoderErrors;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Equalizer;
import javazoom.jl.decoder.FrameDecoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.LayerIDecoder;
import javazoom.jl.decoder.LayerIIDecoder;
import javazoom.jl.decoder.LayerIIIDecoder;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.OutputChannels;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.decoder.SynthesisFilter;

public class Decoder
implements DecoderErrors {
    private static final Params DEFAULT_PARAMS = new Params();
    private Obuffer output;
    private SynthesisFilter filter1;
    private SynthesisFilter filter2;
    private LayerIIIDecoder l3decoder;
    private LayerIIDecoder l2decoder;
    private LayerIDecoder l1decoder;
    private int outputFrequency;
    private int outputChannels;
    private Equalizer equalizer = new Equalizer();
    private Params params;
    private boolean initialized;

    public Decoder() {
        this(null);
    }

    public Decoder(Params params0) {
        if (params0 == null) {
            params0 = DEFAULT_PARAMS;
        }
        this.params = params0;
        Equalizer eq2 = this.params.getInitialEqualizerSettings();
        if (eq2 != null) {
            this.equalizer.setFrom(eq2);
        }
    }

    public static Params getDefaultParams() {
        return (Params)DEFAULT_PARAMS.clone();
    }

    public void setEqualizer(Equalizer eq2) {
        if (eq2 == null) {
            eq2 = Equalizer.PASS_THRU_EQ;
        }
        this.equalizer.setFrom(eq2);
        float[] factors = this.equalizer.getBandFactors();
        if (this.filter1 != null) {
            this.filter1.setEQ(factors);
        }
        if (this.filter2 != null) {
            this.filter2.setEQ(factors);
        }
    }

    public Obuffer decodeFrame(Header header, Bitstream stream) throws DecoderException {
        if (!this.initialized) {
            this.initialize(header);
        }
        int layer = header.layer();
        this.output.clear_buffer();
        FrameDecoder decoder = this.retrieveDecoder(header, stream, layer);
        decoder.decodeFrame();
        this.output.write_buffer(1);
        return this.output;
    }

    public void setOutputBuffer(Obuffer out) {
        this.output = out;
    }

    public int getOutputFrequency() {
        return this.outputFrequency;
    }

    public int getOutputChannels() {
        return this.outputChannels;
    }

    public int getOutputBlockSize() {
        return 2304;
    }

    protected DecoderException newDecoderException(int errorcode) {
        return new DecoderException(errorcode, null);
    }

    protected DecoderException newDecoderException(int errorcode, Throwable throwable) {
        return new DecoderException(errorcode, throwable);
    }

    protected FrameDecoder retrieveDecoder(Header header, Bitstream stream, int layer) throws DecoderException {
        FrameDecoder decoder = null;
        switch (layer) {
            case 3: {
                if (this.l3decoder == null) {
                    this.l3decoder = new LayerIIIDecoder(stream, header, this.filter1, this.filter2, this.output, 0);
                }
                decoder = this.l3decoder;
                break;
            }
            case 2: {
                if (this.l2decoder == null) {
                    this.l2decoder = new LayerIIDecoder();
                    this.l2decoder.create(stream, header, this.filter1, this.filter2, this.output, 0);
                }
                decoder = this.l2decoder;
                break;
            }
            case 1: {
                if (this.l1decoder == null) {
                    this.l1decoder = new LayerIDecoder();
                    this.l1decoder.create(stream, header, this.filter1, this.filter2, this.output, 0);
                }
                decoder = this.l1decoder;
            }
        }
        if (decoder == null) {
            throw this.newDecoderException(513, null);
        }
        return decoder;
    }

    private void initialize(Header header) throws DecoderException {
        int channels;
        float scalefactor = 32700.0f;
        int mode = header.mode();
        int layer = header.layer();
        int n2 = channels = mode == 3 ? 1 : 2;
        if (this.output == null) {
            this.output = new SampleBuffer(header.frequency(), channels);
        }
        float[] factors = this.equalizer.getBandFactors();
        this.filter1 = new SynthesisFilter(0, scalefactor, factors);
        if (channels == 2) {
            this.filter2 = new SynthesisFilter(1, scalefactor, factors);
        }
        this.outputChannels = channels;
        this.outputFrequency = header.frequency();
        this.initialized = true;
    }

    public static class Params
    implements Cloneable {
        private OutputChannels outputChannels = OutputChannels.BOTH;
        private Equalizer equalizer = new Equalizer();

        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException ex2) {
                throw new InternalError(this + ": " + ex2);
            }
        }

        public void setOutputChannels(OutputChannels out) {
            if (out == null) {
                throw new NullPointerException("out");
            }
            this.outputChannels = out;
        }

        public OutputChannels getOutputChannels() {
            return this.outputChannels;
        }

        public Equalizer getInitialEqualizerSettings() {
            return this.equalizer;
        }
    }
}

