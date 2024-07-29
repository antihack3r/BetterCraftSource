/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Crc16;

public final class Header {
    public static final int[][] frequencies = new int[][]{{22050, 24000, 16000, 1}, {44100, 48000, 32000, 1}, {11025, 12000, 8000, 1}};
    public static final int MPEG2_LSF = 0;
    public static final int MPEG25_LSF = 2;
    public static final int MPEG1 = 1;
    public static final int STEREO = 0;
    public static final int JOINT_STEREO = 1;
    public static final int DUAL_CHANNEL = 2;
    public static final int SINGLE_CHANNEL = 3;
    public static final int FOURTYFOUR_POINT_ONE = 0;
    public static final int FOURTYEIGHT = 1;
    public static final int THIRTYTWO = 2;
    private int h_layer;
    private int h_protection_bit;
    private int h_bitrate_index;
    private int h_padding_bit;
    private int h_mode_extension;
    private int h_version;
    private int h_mode;
    private int h_sample_frequency;
    private int h_number_of_subbands;
    private int h_intensity_stereo_bound;
    private boolean h_copyright;
    private boolean h_original;
    private double[] h_vbr_time_per_frame = new double[]{-1.0, 384.0, 1152.0, 1152.0};
    private boolean h_vbr;
    private int h_vbr_frames;
    private int h_vbr_scale;
    private int h_vbr_bytes;
    private byte[] h_vbr_toc;
    private byte syncmode = Bitstream.INITIAL_SYNC;
    private Crc16 crc;
    public short checksum;
    public int framesize;
    public int nSlots;
    private int _headerstring = -1;
    public static final int[][][] bitrates;
    public static final String[][][] bitrate_str;

    static {
        int[][][] nArrayArray = new int[3][][];
        int[][] nArrayArray2 = new int[3][];
        int[] nArray = new int[16];
        nArray[1] = 32000;
        nArray[2] = 48000;
        nArray[3] = 56000;
        nArray[4] = 64000;
        nArray[5] = 80000;
        nArray[6] = 96000;
        nArray[7] = 112000;
        nArray[8] = 128000;
        nArray[9] = 144000;
        nArray[10] = 160000;
        nArray[11] = 176000;
        nArray[12] = 192000;
        nArray[13] = 224000;
        nArray[14] = 256000;
        nArrayArray2[0] = nArray;
        int[] nArray2 = new int[16];
        nArray2[1] = 8000;
        nArray2[2] = 16000;
        nArray2[3] = 24000;
        nArray2[4] = 32000;
        nArray2[5] = 40000;
        nArray2[6] = 48000;
        nArray2[7] = 56000;
        nArray2[8] = 64000;
        nArray2[9] = 80000;
        nArray2[10] = 96000;
        nArray2[11] = 112000;
        nArray2[12] = 128000;
        nArray2[13] = 144000;
        nArray2[14] = 160000;
        nArrayArray2[1] = nArray2;
        int[] nArray3 = new int[16];
        nArray3[1] = 8000;
        nArray3[2] = 16000;
        nArray3[3] = 24000;
        nArray3[4] = 32000;
        nArray3[5] = 40000;
        nArray3[6] = 48000;
        nArray3[7] = 56000;
        nArray3[8] = 64000;
        nArray3[9] = 80000;
        nArray3[10] = 96000;
        nArray3[11] = 112000;
        nArray3[12] = 128000;
        nArray3[13] = 144000;
        nArray3[14] = 160000;
        nArrayArray2[2] = nArray3;
        nArrayArray[0] = nArrayArray2;
        int[][] nArrayArray3 = new int[3][];
        int[] nArray4 = new int[16];
        nArray4[1] = 32000;
        nArray4[2] = 64000;
        nArray4[3] = 96000;
        nArray4[4] = 128000;
        nArray4[5] = 160000;
        nArray4[6] = 192000;
        nArray4[7] = 224000;
        nArray4[8] = 256000;
        nArray4[9] = 288000;
        nArray4[10] = 320000;
        nArray4[11] = 352000;
        nArray4[12] = 384000;
        nArray4[13] = 416000;
        nArray4[14] = 448000;
        nArrayArray3[0] = nArray4;
        int[] nArray5 = new int[16];
        nArray5[1] = 32000;
        nArray5[2] = 48000;
        nArray5[3] = 56000;
        nArray5[4] = 64000;
        nArray5[5] = 80000;
        nArray5[6] = 96000;
        nArray5[7] = 112000;
        nArray5[8] = 128000;
        nArray5[9] = 160000;
        nArray5[10] = 192000;
        nArray5[11] = 224000;
        nArray5[12] = 256000;
        nArray5[13] = 320000;
        nArray5[14] = 384000;
        nArrayArray3[1] = nArray5;
        int[] nArray6 = new int[16];
        nArray6[1] = 32000;
        nArray6[2] = 40000;
        nArray6[3] = 48000;
        nArray6[4] = 56000;
        nArray6[5] = 64000;
        nArray6[6] = 80000;
        nArray6[7] = 96000;
        nArray6[8] = 112000;
        nArray6[9] = 128000;
        nArray6[10] = 160000;
        nArray6[11] = 192000;
        nArray6[12] = 224000;
        nArray6[13] = 256000;
        nArray6[14] = 320000;
        nArrayArray3[2] = nArray6;
        nArrayArray[1] = nArrayArray3;
        int[][] nArrayArray4 = new int[3][];
        int[] nArray7 = new int[16];
        nArray7[1] = 32000;
        nArray7[2] = 48000;
        nArray7[3] = 56000;
        nArray7[4] = 64000;
        nArray7[5] = 80000;
        nArray7[6] = 96000;
        nArray7[7] = 112000;
        nArray7[8] = 128000;
        nArray7[9] = 144000;
        nArray7[10] = 160000;
        nArray7[11] = 176000;
        nArray7[12] = 192000;
        nArray7[13] = 224000;
        nArray7[14] = 256000;
        nArrayArray4[0] = nArray7;
        int[] nArray8 = new int[16];
        nArray8[1] = 8000;
        nArray8[2] = 16000;
        nArray8[3] = 24000;
        nArray8[4] = 32000;
        nArray8[5] = 40000;
        nArray8[6] = 48000;
        nArray8[7] = 56000;
        nArray8[8] = 64000;
        nArray8[9] = 80000;
        nArray8[10] = 96000;
        nArray8[11] = 112000;
        nArray8[12] = 128000;
        nArray8[13] = 144000;
        nArray8[14] = 160000;
        nArrayArray4[1] = nArray8;
        int[] nArray9 = new int[16];
        nArray9[1] = 8000;
        nArray9[2] = 16000;
        nArray9[3] = 24000;
        nArray9[4] = 32000;
        nArray9[5] = 40000;
        nArray9[6] = 48000;
        nArray9[7] = 56000;
        nArray9[8] = 64000;
        nArray9[9] = 80000;
        nArray9[10] = 96000;
        nArray9[11] = 112000;
        nArray9[12] = 128000;
        nArray9[13] = 144000;
        nArray9[14] = 160000;
        nArrayArray4[2] = nArray9;
        nArrayArray[2] = nArrayArray4;
        bitrates = nArrayArray;
        bitrate_str = new String[][][]{{{"free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "176 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "forbidden"}, {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden"}, {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden"}}, {{"free format", "32 kbit/s", "64 kbit/s", "96 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "288 kbit/s", "320 kbit/s", "352 kbit/s", "384 kbit/s", "416 kbit/s", "448 kbit/s", "forbidden"}, {"free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s", "384 kbit/s", "forbidden"}, {"free format", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s", "forbidden"}}, {{"free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "176 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "forbidden"}, {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden"}, {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s", "forbidden"}}};
    }

    Header() {
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append("Layer ");
        buffer.append(this.layer_string());
        buffer.append(" frame ");
        buffer.append(this.mode_string());
        buffer.append(' ');
        buffer.append(this.version_string());
        if (!this.checksums()) {
            buffer.append(" no");
        }
        buffer.append(" checksums");
        buffer.append(' ');
        buffer.append(this.sample_frequency_string());
        buffer.append(',');
        buffer.append(' ');
        buffer.append(this.bitrate_string());
        String s2 = buffer.toString();
        return s2;
    }

    void read_header(Bitstream stream, Crc16[] crcp) throws BitstreamException {
        int headerstring;
        boolean sync = false;
        do {
            this._headerstring = headerstring = stream.syncHeader(this.syncmode);
            if (this.syncmode == Bitstream.INITIAL_SYNC) {
                this.h_version = headerstring >>> 19 & 1;
                if ((headerstring >>> 20 & 1) == 0) {
                    if (this.h_version == 0) {
                        this.h_version = 2;
                    } else {
                        throw stream.newBitstreamException(256);
                    }
                }
                if ((this.h_sample_frequency = headerstring >>> 10 & 3) == 3) {
                    throw stream.newBitstreamException(256);
                }
            }
            this.h_layer = 4 - (headerstring >>> 17) & 3;
            this.h_protection_bit = headerstring >>> 16 & 1;
            this.h_bitrate_index = headerstring >>> 12 & 0xF;
            this.h_padding_bit = headerstring >>> 9 & 1;
            this.h_mode = headerstring >>> 6 & 3;
            this.h_mode_extension = headerstring >>> 4 & 3;
            this.h_intensity_stereo_bound = this.h_mode == 1 ? (this.h_mode_extension << 2) + 4 : 0;
            if ((headerstring >>> 3 & 1) == 1) {
                this.h_copyright = true;
            }
            if ((headerstring >>> 2 & 1) == 1) {
                this.h_original = true;
            }
            if (this.h_layer == 1) {
                this.h_number_of_subbands = 32;
            } else {
                int channel_bitrate = this.h_bitrate_index;
                if (this.h_mode != 3) {
                    channel_bitrate = channel_bitrate == 4 ? 1 : (channel_bitrate -= 4);
                }
                this.h_number_of_subbands = channel_bitrate == 1 || channel_bitrate == 2 ? (this.h_sample_frequency == 2 ? 12 : 8) : (this.h_sample_frequency == 1 || channel_bitrate >= 3 && channel_bitrate <= 5 ? 27 : 30);
            }
            if (this.h_intensity_stereo_bound > this.h_number_of_subbands) {
                this.h_intensity_stereo_bound = this.h_number_of_subbands;
            }
            this.calculate_framesize();
            int framesizeloaded = stream.read_frame_data(this.framesize);
            if (this.framesize >= 0 && framesizeloaded != this.framesize) {
                throw stream.newBitstreamException(261);
            }
            if (stream.isSyncCurrentPosition(this.syncmode)) {
                if (this.syncmode == Bitstream.INITIAL_SYNC) {
                    this.syncmode = Bitstream.STRICT_SYNC;
                    stream.set_syncword(headerstring & 0xFFF80CC0);
                }
                sync = true;
                continue;
            }
            stream.unreadFrame();
        } while (!sync);
        stream.parse_frame();
        if (this.h_protection_bit == 0) {
            this.checksum = (short)stream.get_bits(16);
            if (this.crc == null) {
                this.crc = new Crc16();
            }
            this.crc.add_bits(headerstring, 16);
            crcp[0] = this.crc;
        } else {
            crcp[0] = null;
        }
    }

    void parseVBR(byte[] firstframe) throws BitstreamException {
        String xing = "Xing";
        byte[] tmp = new byte[4];
        int offset = 0;
        offset = this.h_version == 1 ? (this.h_mode == 3 ? 17 : 32) : (this.h_mode == 3 ? 9 : 17);
        try {
            System.arraycopy(firstframe, offset, tmp, 0, 4);
            if (xing.equals(new String(tmp))) {
                this.h_vbr = true;
                this.h_vbr_frames = -1;
                this.h_vbr_bytes = -1;
                this.h_vbr_scale = -1;
                this.h_vbr_toc = new byte[100];
                int length = 4;
                byte[] flags = new byte[4];
                System.arraycopy(firstframe, offset + length, flags, 0, flags.length);
                length += flags.length;
                if ((flags[3] & 1) != 0) {
                    System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
                    this.h_vbr_frames = tmp[0] << 24 & 0xFF000000 | tmp[1] << 16 & 0xFF0000 | tmp[2] << 8 & 0xFF00 | tmp[3] & 0xFF;
                    length += 4;
                }
                if ((flags[3] & 2) != 0) {
                    System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
                    this.h_vbr_bytes = tmp[0] << 24 & 0xFF000000 | tmp[1] << 16 & 0xFF0000 | tmp[2] << 8 & 0xFF00 | tmp[3] & 0xFF;
                    length += 4;
                }
                if ((flags[3] & 4) != 0) {
                    System.arraycopy(firstframe, offset + length, this.h_vbr_toc, 0, this.h_vbr_toc.length);
                    length += this.h_vbr_toc.length;
                }
                if ((flags[3] & 8) != 0) {
                    System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
                    this.h_vbr_scale = tmp[0] << 24 & 0xFF000000 | tmp[1] << 16 & 0xFF0000 | tmp[2] << 8 & 0xFF00 | tmp[3] & 0xFF;
                    length += 4;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e2) {
            throw new BitstreamException("XingVBRHeader Corrupted", (Throwable)e2);
        }
        String vbri = "VBRI";
        offset = 32;
        try {
            System.arraycopy(firstframe, offset, tmp, 0, 4);
            if (vbri.equals(new String(tmp))) {
                this.h_vbr = true;
                this.h_vbr_frames = -1;
                this.h_vbr_bytes = -1;
                this.h_vbr_scale = -1;
                this.h_vbr_toc = new byte[100];
                int length = 10;
                System.arraycopy(firstframe, offset + length, tmp, 0, tmp.length);
                this.h_vbr_bytes = tmp[0] << 24 & 0xFF000000 | tmp[1] << 16 & 0xFF0000 | tmp[2] << 8 & 0xFF00 | tmp[3] & 0xFF;
                System.arraycopy(firstframe, offset + (length += 4), tmp, 0, tmp.length);
                this.h_vbr_frames = tmp[0] << 24 & 0xFF000000 | tmp[1] << 16 & 0xFF0000 | tmp[2] << 8 & 0xFF00 | tmp[3] & 0xFF;
                length += 4;
            }
        }
        catch (ArrayIndexOutOfBoundsException e3) {
            throw new BitstreamException("VBRIVBRHeader Corrupted", (Throwable)e3);
        }
    }

    public int version() {
        return this.h_version;
    }

    public int layer() {
        return this.h_layer;
    }

    public int bitrate_index() {
        return this.h_bitrate_index;
    }

    public int sample_frequency() {
        return this.h_sample_frequency;
    }

    public int frequency() {
        return frequencies[this.h_version][this.h_sample_frequency];
    }

    public int mode() {
        return this.h_mode;
    }

    public boolean checksums() {
        return this.h_protection_bit == 0;
    }

    public boolean copyright() {
        return this.h_copyright;
    }

    public boolean original() {
        return this.h_original;
    }

    public boolean vbr() {
        return this.h_vbr;
    }

    public int vbr_scale() {
        return this.h_vbr_scale;
    }

    public byte[] vbr_toc() {
        return this.h_vbr_toc;
    }

    public boolean checksum_ok() {
        return this.checksum == this.crc.checksum();
    }

    public boolean padding() {
        return this.h_padding_bit != 0;
    }

    public int slots() {
        return this.nSlots;
    }

    public int mode_extension() {
        return this.h_mode_extension;
    }

    public int calculate_framesize() {
        if (this.h_layer == 1) {
            this.framesize = 12 * bitrates[this.h_version][0][this.h_bitrate_index] / frequencies[this.h_version][this.h_sample_frequency];
            if (this.h_padding_bit != 0) {
                ++this.framesize;
            }
            this.framesize <<= 2;
            this.nSlots = 0;
        } else {
            this.framesize = 144 * bitrates[this.h_version][this.h_layer - 1][this.h_bitrate_index] / frequencies[this.h_version][this.h_sample_frequency];
            if (this.h_version == 0 || this.h_version == 2) {
                this.framesize >>= 1;
            }
            if (this.h_padding_bit != 0) {
                ++this.framesize;
            }
            this.nSlots = this.h_layer == 3 ? (this.h_version == 1 ? this.framesize - (this.h_mode == 3 ? 17 : 32) - (this.h_protection_bit != 0 ? 0 : 2) - 4 : this.framesize - (this.h_mode == 3 ? 9 : 17) - (this.h_protection_bit != 0 ? 0 : 2) - 4) : 0;
        }
        this.framesize -= 4;
        return this.framesize;
    }

    public int max_number_of_frames(int streamsize) {
        if (this.h_vbr) {
            return this.h_vbr_frames;
        }
        if (this.framesize + 4 - this.h_padding_bit == 0) {
            return 0;
        }
        return streamsize / (this.framesize + 4 - this.h_padding_bit);
    }

    public int min_number_of_frames(int streamsize) {
        if (this.h_vbr) {
            return this.h_vbr_frames;
        }
        if (this.framesize + 5 - this.h_padding_bit == 0) {
            return 0;
        }
        return streamsize / (this.framesize + 5 - this.h_padding_bit);
    }

    public float ms_per_frame() {
        if (this.h_vbr) {
            double tpf = this.h_vbr_time_per_frame[this.layer()] / (double)this.frequency();
            if (this.h_version == 0 || this.h_version == 2) {
                tpf /= 2.0;
            }
            return (float)(tpf * 1000.0);
        }
        float[][] ms_per_frame_array = new float[][]{{8.707483f, 8.0f, 12.0f}, {26.12245f, 24.0f, 36.0f}, {26.12245f, 24.0f, 36.0f}};
        return ms_per_frame_array[this.h_layer - 1][this.h_sample_frequency];
    }

    public float total_ms(int streamsize) {
        return (float)this.max_number_of_frames(streamsize) * this.ms_per_frame();
    }

    public int getSyncHeader() {
        return this._headerstring;
    }

    public String layer_string() {
        switch (this.h_layer) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            case 3: {
                return "III";
            }
        }
        return null;
    }

    public String bitrate_string() {
        if (this.h_vbr) {
            return String.valueOf(Integer.toString(this.bitrate() / 1000)) + " kb/s";
        }
        return bitrate_str[this.h_version][this.h_layer - 1][this.h_bitrate_index];
    }

    public int bitrate() {
        if (this.h_vbr) {
            return (int)((float)(this.h_vbr_bytes * 8) / (this.ms_per_frame() * (float)this.h_vbr_frames)) * 1000;
        }
        return bitrates[this.h_version][this.h_layer - 1][this.h_bitrate_index];
    }

    public int bitrate_instant() {
        return bitrates[this.h_version][this.h_layer - 1][this.h_bitrate_index];
    }

    public String sample_frequency_string() {
        switch (this.h_sample_frequency) {
            case 2: {
                if (this.h_version == 1) {
                    return "32 kHz";
                }
                if (this.h_version == 0) {
                    return "16 kHz";
                }
                return "8 kHz";
            }
            case 0: {
                if (this.h_version == 1) {
                    return "44.1 kHz";
                }
                if (this.h_version == 0) {
                    return "22.05 kHz";
                }
                return "11.025 kHz";
            }
            case 1: {
                if (this.h_version == 1) {
                    return "48 kHz";
                }
                if (this.h_version == 0) {
                    return "24 kHz";
                }
                return "12 kHz";
            }
        }
        return null;
    }

    public String mode_string() {
        switch (this.h_mode) {
            case 0: {
                return "Stereo";
            }
            case 1: {
                return "Joint stereo";
            }
            case 2: {
                return "Dual channel";
            }
            case 3: {
                return "Single channel";
            }
        }
        return null;
    }

    public String version_string() {
        switch (this.h_version) {
            case 1: {
                return "MPEG-1";
            }
            case 0: {
                return "MPEG-2 LSF";
            }
            case 2: {
                return "MPEG-2.5 LSF";
            }
        }
        return null;
    }

    public int number_of_subbands() {
        return this.h_number_of_subbands;
    }

    public int intensity_stereo_bound() {
        return this.h_intensity_stereo_bound;
    }
}

