/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.converter;

import java.io.PrintWriter;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.Crc16;
import javazoom.jl.decoder.JavaLayerException;

public class jlc {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int argc = args.length + 1;
        String[] argv = new String[argc];
        argv[0] = "jlc";
        int i2 = 0;
        while (i2 < args.length) {
            argv[i2 + 1] = args[i2];
            ++i2;
        }
        jlcArgs ma2 = new jlcArgs();
        if (!ma2.processArgs(argv)) {
            System.exit(1);
        }
        Converter conv = new Converter();
        int detail = ma2.verbose_mode ? ma2.verbose_level : 0;
        Converter.PrintWriterProgressListener listener = new Converter.PrintWriterProgressListener(new PrintWriter(System.out, true), detail);
        try {
            conv.convert(ma2.filename, ma2.output_filename, listener);
        }
        catch (JavaLayerException ex2) {
            System.err.println("Convertion failure: " + ex2);
        }
        System.exit(0);
    }

    static class jlcArgs {
        public int which_c = 0;
        public int output_mode;
        public boolean use_own_scalefactor = false;
        public float scalefactor = 32768.0f;
        public String output_filename;
        public String filename;
        public boolean verbose_mode = false;
        public int verbose_level = 3;

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean processArgs(String[] argv) {
            this.filename = null;
            Crc16[] crc = new Crc16[1];
            int argc = argv.length;
            this.verbose_mode = false;
            this.output_mode = 0;
            this.output_filename = "";
            if (argc < 2 || argv[1].equals("-h")) {
                return this.Usage();
            }
            int i2 = 1;
            while (i2 < argc) {
                if (argv[i2].charAt(0) == '-') {
                    if (argv[i2].startsWith("-v")) {
                        this.verbose_mode = true;
                        if (argv[i2].length() > 2) {
                            try {
                                String level = argv[i2].substring(2);
                                this.verbose_level = Integer.parseInt(level);
                            }
                            catch (NumberFormatException ex2) {
                                System.err.println("Invalid verbose level. Using default.");
                            }
                        }
                        System.out.println("Verbose Activated (level " + this.verbose_level + ")");
                    } else {
                        if (!argv[i2].equals("-p")) return this.Usage();
                        if (++i2 == argc) {
                            System.out.println("Please specify an output filename after the -p option!");
                            System.exit(1);
                        }
                        this.output_filename = argv[i2];
                    }
                } else {
                    this.filename = argv[i2];
                    System.out.println("FileName = " + argv[i2]);
                    if (this.filename == null) {
                        return this.Usage();
                    }
                }
                ++i2;
            }
            if (this.filename != null) return true;
            return this.Usage();
        }

        public boolean Usage() {
            System.out.println("JavaLayer Converter :");
            System.out.println("  -v[x]         verbose mode. ");
            System.out.println("                default = 2");
            System.out.println("  -p name    output as a PCM wave file");
            System.out.println("");
            System.out.println("  More info on http://www.javazoom.net");
            return false;
        }
    }
}

