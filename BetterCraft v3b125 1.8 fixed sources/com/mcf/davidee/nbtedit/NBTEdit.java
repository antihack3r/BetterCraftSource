/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit;

import com.mcf.davidee.nbtedit.nbt.NBTNodeSorter;
import com.mcf.davidee.nbtedit.nbt.NBTTree;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.SaveStates;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class NBTEdit {
    public static final String MODID = "nbtedit";
    public static final String NAME = "In-game NBTEdit";
    public static final String VERSION = "1.11.2-2.0.2";
    public static final NBTNodeSorter SORTER = new NBTNodeSorter();
    public static Logger logger;
    public static NamedNBT clipboard;
    public static boolean opOnly;
    public static boolean editOtherPlayers;
    public static NBTEdit instance;
    private SaveStates saves;
    static final String SEP;

    static {
        clipboard = null;
        opOnly = true;
        editOtherPlayers = false;
        instance = new NBTEdit();
        SEP = System.getProperty("line.separator");
    }

    public static void log(Level l2, String s2) {
        logger.log(l2, s2);
    }

    public static void throwing(String cls, String mthd, Throwable thr) {
        logger.warn("class: " + cls + " method: " + mthd, thr);
    }

    public static void logTag(NBTTagCompound tag) {
        NBTTree tree = new NBTTree(tag);
        String sb2 = "";
        for (String s2 : tree.toStrings()) {
            sb2 = String.valueOf(sb2) + SEP + "\t\t\t" + s2;
        }
        NBTEdit.log(Level.TRACE, sb2);
    }

    public static SaveStates getSaveStates() {
        return NBTEdit.instance.saves;
    }
}

