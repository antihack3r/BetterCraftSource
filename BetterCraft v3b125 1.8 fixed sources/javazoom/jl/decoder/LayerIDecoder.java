/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Crc16;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.FrameDecoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.SynthesisFilter;

class LayerIDecoder
implements FrameDecoder {
    protected Bitstream stream;
    protected Header header;
    protected SynthesisFilter filter1;
    protected SynthesisFilter filter2;
    protected Obuffer buffer;
    protected int which_channels;
    protected int mode;
    protected int num_subbands;
    protected Subband[] subbands;
    protected Crc16 crc = new Crc16();

    public void create(Bitstream stream0, Header header0, SynthesisFilter filtera, SynthesisFilter filterb, Obuffer buffer0, int which_ch0) {
        this.stream = stream0;
        this.header = header0;
        this.filter1 = filtera;
        this.filter2 = filterb;
        this.buffer = buffer0;
        this.which_channels = which_ch0;
    }

    @Override
    public void decodeFrame() throws DecoderException {
        this.num_subbands = this.header.number_of_subbands();
        this.subbands = new Subband[32];
        this.mode = this.header.mode();
        this.createSubbands();
        this.readAllocation();
        this.readScaleFactorSelection();
        if (this.crc != null || this.header.checksum_ok()) {
            this.readScaleFactors();
            this.readSampleData();
        }
    }

    protected void createSubbands() {
        if (this.mode == 3) {
            int i2 = 0;
            while (i2 < this.num_subbands) {
                this.subbands[i2] = new SubbandLayer1(i2);
                ++i2;
            }
        } else if (this.mode == 1) {
            int i3 = 0;
            while (i3 < this.header.intensity_stereo_bound()) {
                this.subbands[i3] = new SubbandLayer1Stereo(i3);
                ++i3;
            }
            while (i3 < this.num_subbands) {
                this.subbands[i3] = new SubbandLayer1IntensityStereo(i3);
                ++i3;
            }
        } else {
            int i4 = 0;
            while (i4 < this.num_subbands) {
                this.subbands[i4] = new SubbandLayer1Stereo(i4);
                ++i4;
            }
        }
    }

    protected void readAllocation() throws DecoderException {
        int i2 = 0;
        while (i2 < this.num_subbands) {
            this.subbands[i2].read_allocation(this.stream, this.header, this.crc);
            ++i2;
        }
    }

    protected void readScaleFactorSelection() {
    }

    protected void readScaleFactors() {
        int i2 = 0;
        while (i2 < this.num_subbands) {
            this.subbands[i2].read_scalefactor(this.stream, this.header);
            ++i2;
        }
    }

    protected void readSampleData() {
        boolean read_ready = false;
        boolean write_ready = false;
        int mode = this.header.mode();
        do {
            int i2 = 0;
            while (i2 < this.num_subbands) {
                read_ready = this.subbands[i2].read_sampledata(this.stream);
                ++i2;
            }
            do {
                i2 = 0;
                while (i2 < this.num_subbands) {
                    write_ready = this.subbands[i2].put_next_sample(this.which_channels, this.filter1, this.filter2);
                    ++i2;
                }
                this.filter1.calculate_pcm_samples(this.buffer);
                if (this.which_channels != 0 || mode == 3) continue;
                this.filter2.calculate_pcm_samples(this.buffer);
            } while (!write_ready);
        } while (!read_ready);
    }

    static abstract class Subband {
        public static final float[] scalefactors = new float[]{2.0f, 1.587401f, 1.2599211f, 1.0f, 0.7937005f, 0.62996054f, 0.5f, 0.39685026f, 0.31498027f, 0.25f, 0.19842513f, 0.15749013f, 0.125f, 0.099212565f, 0.07874507f, 0.0625f, 0.049606282f, 0.039372534f, 0.03125f, 0.024803141f, 0.019686267f, 0.015625f, 0.012401571f, 0.009843133f, 0.0078125f, 0.0062007853f, 0.0049215667f, 0.00390625f, 0.0031003926f, 0.0024607833f, 0.001953125f, 0.0015501963f, 0.0012303917f, 9.765625E-4f, 7.7509816E-4f, 6.1519584E-4f, 4.8828125E-4f, 3.8754908E-4f, 3.0759792E-4f, 2.4414062E-4f, 1.9377454E-4f, 1.5379896E-4f, 1.2207031E-4f, 9.688727E-5f, 7.689948E-5f, 6.1035156E-5f, 4.8443635E-5f, 3.844974E-5f, 3.0517578E-5f, 2.4221818E-5f, 1.922487E-5f, 1.5258789E-5f, 1.2110909E-5f, 9.612435E-6f, 7.6293945E-6f, 6.0554544E-6f, 4.8062175E-6f, 3.8146973E-6f, 3.0277272E-6f, 2.4031087E-6f, 1.9073486E-6f, 1.5138636E-6f, 1.2015544E-6f, 0.0f};

        Subband() {
        }

        public abstract void read_allocation(Bitstream var1, Header var2, Crc16 var3) throws DecoderException;

        public abstract void read_scalefactor(Bitstream var1, Header var2);

        public abstract boolean read_sampledata(Bitstream var1);

        public abstract boolean put_next_sample(int var1, SynthesisFilter var2, SynthesisFilter var3);
    }

    static class SubbandLayer1
    extends Subband {
        public static final float[] table_factor = new float[]{0.0f, 0.6666667f, 0.2857143f, 0.13333334f, 0.06451613f, 0.031746034f, 0.015748031f, 0.007843138f, 0.0039138943f, 0.0019550342f, 9.770396E-4f, 4.884005E-4f, 2.4417043E-4f, 1.2207776E-4f, 6.103702E-5f};
        public static final float[] table_offset = new float[]{0.0f, -0.6666667f, -0.8571429f, -0.9333334f, -0.9677419f, -0.98412704f, -0.992126f, -0.9960785f, -0.99804306f, -0.9990225f, -0.9995115f, -0.99975586f, -0.9998779f, -0.99993896f, -0.9999695f};
        protected int subbandnumber;
        protected int samplenumber;
        protected int allocation;
        protected float scalefactor;
        protected int samplelength;
        protected float sample;
        protected float factor;
        protected float offset;

        public SubbandLayer1(int subbandnumber) {
            this.subbandnumber = subbandnumber;
            this.samplenumber = 0;
        }

        @Override
        public void read_allocation(Bitstream stream, Header header, Crc16 crc) throws DecoderException {
            this.allocation = stream.get_bits(4);
            if (this.allocation == 15) {
                throw new DecoderException(514, null);
            }
            if (crc != null) {
                crc.add_bits(this.allocation, 4);
            }
            if (this.allocation != 0) {
                this.samplelength = this.allocation + 1;
                this.factor = table_factor[this.allocation];
                this.offset = table_offset[this.allocation];
            }
        }

        @Override
        public void read_scalefactor(Bitstream stream, Header header) {
            if (this.allocation != 0) {
                this.scalefactor = scalefactors[stream.get_bits(6)];
            }
        }

        @Override
        public boolean read_sampledata(Bitstream stream) {
            if (this.allocation != 0) {
                this.sample = stream.get_bits(this.samplelength);
            }
            if (++this.samplenumber == 12) {
                this.samplenumber = 0;
                return true;
            }
            return false;
        }

        @Override
        public boolean put_next_sample(int channels, SynthesisFilter filter1, SynthesisFilter filter2) {
            if (this.allocation != 0 && channels != 2) {
                float scaled_sample = (this.sample * this.factor + this.offset) * this.scalefactor;
                filter1.input_sample(scaled_sample, this.subbandnumber);
            }
            return true;
        }
    }

    static class SubbandLayer1IntensityStereo
    extends SubbandLayer1 {
        protected float channel2_scalefactor;

        public SubbandLayer1IntensityStereo(int subbandnumber) {
            super(subbandnumber);
        }

        @Override
        public void read_allocation(Bitstream stream, Header header, Crc16 crc) throws DecoderException {
            super.read_allocation(stream, header, crc);
        }

        @Override
        public void read_scalefactor(Bitstream stream, Header header) {
            if (this.allocation != 0) {
                this.scalefactor = scalefactors[stream.get_bits(6)];
                this.channel2_scalefactor = scalefactors[stream.get_bits(6)];
            }
        }

        @Override
        public boolean read_sampledata(Bitstream stream) {
            return super.read_sampledata(stream);
        }

        @Override
        public boolean put_next_sample(int channels, SynthesisFilter filter1, SynthesisFilter filter2) {
            if (this.allocation != 0) {
                this.sample = this.sample * this.factor + this.offset;
                if (channels == 0) {
                    float sample1 = this.sample * this.scalefactor;
                    float sample2 = this.sample * this.channel2_scalefactor;
                    filter1.input_sample(sample1, this.subbandnumber);
                    filter2.input_sample(sample2, this.subbandnumber);
                } else if (channels == 1) {
                    float sample1 = this.sample * this.scalefactor;
                    filter1.input_sample(sample1, this.subbandnumber);
                } else {
                    float sample2 = this.sample * this.channel2_scalefactor;
                    filter1.input_sample(sample2, this.subbandnumber);
                }
            }
            return true;
        }
    }

    static class SubbandLayer1Stereo
    extends SubbandLayer1 {
        protected int channel2_allocation;
        protected float channel2_scalefactor;
        protected int channel2_samplelength;
        protected float channel2_sample;
        protected float channel2_factor;
        protected float channel2_offset;

        public SubbandLayer1Stereo(int subbandnumber) {
            super(subbandnumber);
        }

        @Override
        public void read_allocation(Bitstream stream, Header header, Crc16 crc) throws DecoderException {
            this.allocation = stream.get_bits(4);
            this.channel2_allocation = stream.get_bits(4);
            if (crc != null) {
                crc.add_bits(this.allocation, 4);
                crc.add_bits(this.channel2_allocation, 4);
            }
            if (this.allocation != 0) {
                this.samplelength = this.allocation + 1;
                this.factor = table_factor[this.allocation];
                this.offset = table_offset[this.allocation];
            }
            if (this.channel2_allocation != 0) {
                this.channel2_samplelength = this.channel2_allocation + 1;
                this.channel2_factor = table_factor[this.channel2_allocation];
                this.channel2_offset = table_offset[this.channel2_allocation];
            }
        }

        @Override
        public void read_scalefactor(Bitstream stream, Header header) {
            if (this.allocation != 0) {
                this.scalefactor = scalefactors[stream.get_bits(6)];
            }
            if (this.channel2_allocation != 0) {
                this.channel2_scalefactor = scalefactors[stream.get_bits(6)];
            }
        }

        @Override
        public boolean read_sampledata(Bitstream stream) {
            boolean returnvalue = super.read_sampledata(stream);
            if (this.channel2_allocation != 0) {
                this.channel2_sample = stream.get_bits(this.channel2_samplelength);
            }
            return returnvalue;
        }

        @Override
        public boolean put_next_sample(int channels, SynthesisFilter filter1, SynthesisFilter filter2) {
            super.put_next_sample(channels, filter1, filter2);
            if (this.channel2_allocation != 0 && channels != 1) {
                float sample2 = (this.channel2_sample * this.channel2_factor + this.channel2_offset) * this.channel2_scalefactor;
                if (channels == 0) {
                    filter2.input_sample(sample2, this.subbandnumber);
                } else {
                    filter1.input_sample(sample2, this.subbandnumber);
                }
            }
            return true;
        }
    }
}

