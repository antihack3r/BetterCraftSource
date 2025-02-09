/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;
import net.optifine.shaders.Program;
import net.optifine.shaders.ProgramStage;

public class Programs {
    private List<Program> programs = new ArrayList<Program>();
    private Program programNone = this.make("", ProgramStage.NONE, true);

    public Program make(String name, ProgramStage programStage, Program backupProgram) {
        int i2 = this.programs.size();
        Program program = new Program(i2, name, programStage, backupProgram);
        this.programs.add(program);
        return program;
    }

    private Program make(String name, ProgramStage programStage, boolean ownBackup) {
        int i2 = this.programs.size();
        Program program = new Program(i2, name, programStage, ownBackup);
        this.programs.add(program);
        return program;
    }

    public Program makeGbuffers(String name, Program backupProgram) {
        return this.make(name, ProgramStage.GBUFFERS, backupProgram);
    }

    public Program makeComposite(String name) {
        return this.make(name, ProgramStage.COMPOSITE, this.programNone);
    }

    public Program makeDeferred(String name) {
        return this.make(name, ProgramStage.DEFERRED, this.programNone);
    }

    public Program makeShadow(String name, Program backupProgram) {
        return this.make(name, ProgramStage.SHADOW, backupProgram);
    }

    public Program makeVirtual(String name) {
        return this.make(name, ProgramStage.NONE, true);
    }

    public Program[] makeComposites(String prefix, int count) {
        Program[] aprogram = new Program[count];
        int i2 = 0;
        while (i2 < count) {
            String s2 = i2 == 0 ? prefix : String.valueOf(prefix) + i2;
            aprogram[i2] = this.makeComposite(s2);
            ++i2;
        }
        return aprogram;
    }

    public Program[] makeDeferreds(String prefix, int count) {
        Program[] aprogram = new Program[count];
        int i2 = 0;
        while (i2 < count) {
            String s2 = i2 == 0 ? prefix : String.valueOf(prefix) + i2;
            aprogram[i2] = this.makeDeferred(s2);
            ++i2;
        }
        return aprogram;
    }

    public Program getProgramNone() {
        return this.programNone;
    }

    public int getCount() {
        return this.programs.size();
    }

    public Program getProgram(String name) {
        if (name == null) {
            return null;
        }
        int i2 = 0;
        while (i2 < this.programs.size()) {
            Program program = this.programs.get(i2);
            String s2 = program.getName();
            if (s2.equals(name)) {
                return program;
            }
            ++i2;
        }
        return null;
    }

    public String[] getProgramNames() {
        String[] astring = new String[this.programs.size()];
        int i2 = 0;
        while (i2 < astring.length) {
            astring[i2] = this.programs.get(i2).getName();
            ++i2;
        }
        return astring;
    }

    public Program[] getPrograms() {
        Program[] aprogram = this.programs.toArray(new Program[this.programs.size()]);
        return aprogram;
    }

    public Program[] getPrograms(Program programFrom, Program programTo) {
        int j2;
        int i2 = programFrom.getIndex();
        if (i2 > (j2 = programTo.getIndex())) {
            int k2 = i2;
            i2 = j2;
            j2 = k2;
        }
        Program[] aprogram = new Program[j2 - i2 + 1];
        int l2 = 0;
        while (l2 < aprogram.length) {
            aprogram[l2] = this.programs.get(i2 + l2);
            ++l2;
        }
        return aprogram;
    }

    public String toString() {
        return this.programs.toString();
    }
}

