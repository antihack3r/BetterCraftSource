/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

public class jlp {
    private String fFilename = null;
    private boolean remote = false;

    public static void main(String[] args) {
        int retval = 0;
        try {
            jlp player = jlp.createInstance(args);
            if (player != null) {
                player.play();
            }
        }
        catch (Exception ex2) {
            System.err.println(ex2);
            ex2.printStackTrace(System.err);
            retval = 1;
        }
        System.exit(retval);
    }

    public static jlp createInstance(String[] args) {
        jlp player = new jlp();
        if (!player.parseArgs(args)) {
            player = null;
        }
        return player;
    }

    private jlp() {
    }

    public jlp(String filename) {
        this.init(filename);
    }

    protected void init(String filename) {
        this.fFilename = filename;
    }

    protected boolean parseArgs(String[] args) {
        boolean parsed = false;
        if (args.length == 1) {
            this.init(args[0]);
            parsed = true;
            this.remote = false;
        } else if (args.length == 2) {
            if (!args[0].equals("-url")) {
                this.showUsage();
            } else {
                this.init(args[1]);
                parsed = true;
                this.remote = true;
            }
        } else {
            this.showUsage();
        }
        return parsed;
    }

    public void showUsage() {
        System.out.println("Usage: jlp [-url] <filename>");
        System.out.println("");
        System.out.println(" e.g. : java javazoom.jl.player.jlp localfile.mp3");
        System.out.println("        java javazoom.jl.player.jlp -url http://www.server.com/remotefile.mp3");
        System.out.println("        java javazoom.jl.player.jlp -url http://www.shoutcastserver.com:8000");
    }

    public void play() throws JavaLayerException {
        try {
            System.out.println("playing " + this.fFilename + "...");
            InputStream in2 = null;
            in2 = this.remote ? this.getURLInputStream() : this.getInputStream();
            AudioDevice dev = this.getAudioDevice();
            Player player = new Player(in2, dev);
            player.play();
        }
        catch (IOException ex2) {
            throw new JavaLayerException("Problem playing file " + this.fFilename, ex2);
        }
        catch (Exception ex3) {
            throw new JavaLayerException("Problem playing file " + this.fFilename, ex3);
        }
    }

    protected InputStream getURLInputStream() throws Exception {
        URL url = new URL(this.fFilename);
        InputStream fin = url.openStream();
        BufferedInputStream bin2 = new BufferedInputStream(fin);
        return bin2;
    }

    protected InputStream getInputStream() throws IOException {
        FileInputStream fin = new FileInputStream(this.fFilename);
        BufferedInputStream bin2 = new BufferedInputStream(fin);
        return bin2;
    }

    protected AudioDevice getAudioDevice() throws JavaLayerException {
        return FactoryRegistry.systemRegistry().createAudioDevice();
    }
}

