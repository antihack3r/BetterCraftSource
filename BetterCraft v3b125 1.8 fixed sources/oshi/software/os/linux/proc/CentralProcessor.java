/*
 * Decompiled with CFR 0.152.
 */
package oshi.software.os.linux.proc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import oshi.hardware.Processor;
import oshi.util.FormatUtil;

public class CentralProcessor
implements Processor {
    private String _vendor;
    private String _name;
    private String _identifier = null;
    private String _stepping;
    private String _model;
    private String _family;
    private boolean _cpu64;

    public String getVendor() {
        return this._vendor;
    }

    public void setVendor(String vendor) {
        this._vendor = vendor;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getIdentifier() {
        if (this._identifier == null) {
            StringBuilder sb2 = new StringBuilder();
            if (this.getVendor().contentEquals("GenuineIntel")) {
                sb2.append(this.isCpu64bit() ? "Intel64" : "x86");
            } else {
                sb2.append(this.getVendor());
            }
            sb2.append(" Family ");
            sb2.append(this.getFamily());
            sb2.append(" Model ");
            sb2.append(this.getModel());
            sb2.append(" Stepping ");
            sb2.append(this.getStepping());
            this._identifier = sb2.toString();
        }
        return this._identifier;
    }

    public void setIdentifier(String identifier) {
        this._identifier = identifier;
    }

    public boolean isCpu64bit() {
        return this._cpu64;
    }

    public void setCpu64(boolean cpu64) {
        this._cpu64 = cpu64;
    }

    public String getStepping() {
        return this._stepping;
    }

    public void setStepping(String _stepping) {
        this._stepping = _stepping;
    }

    public String getModel() {
        return this._model;
    }

    public void setModel(String _model) {
        this._model = _model;
    }

    public String getFamily() {
        return this._family;
    }

    public void setFamily(String _family) {
        this._family = _family;
    }

    public float getLoad() {
        Scanner in2 = null;
        try {
            in2 = new Scanner(new FileReader("/proc/stat"));
        }
        catch (FileNotFoundException e2) {
            System.err.println("Problem with: /proc/stat");
            System.err.println(e2.getMessage());
            return -1.0f;
        }
        in2.useDelimiter("\n");
        String[] result = in2.next().split(" ");
        ArrayList<Float> loads = new ArrayList<Float>();
        for (String load : result) {
            if (!load.matches("-?\\d+(\\.\\d+)?")) continue;
            loads.add(Float.valueOf(load));
        }
        float totalCpuLoad = (((Float)loads.get(0)).floatValue() + ((Float)loads.get(2)).floatValue()) * 100.0f / (((Float)loads.get(0)).floatValue() + ((Float)loads.get(2)).floatValue() + ((Float)loads.get(3)).floatValue());
        return FormatUtil.round(totalCpuLoad, 2);
    }

    public String toString() {
        return this.getName();
    }
}

