/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core.asm;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import net.labymod.addon.AddonLoader;
import net.labymod.support.util.CapturePrintStream;
import net.labymod.support.util.Debug;
import net.minecraft.launchwrapper.Launch;

public class LabyModCoreMod {
    private static boolean obfuscated;
    private static boolean forge;

    static {
        System.setErr(new CapturePrintStream(System.err));
        System.setOut(new CapturePrintStream(System.out));
        System.setProperty("java.net.preferIPv4Stack", "true");
        Debug.init();
    }

    public static boolean isObfuscated() {
        return obfuscated;
    }

    public static void setObfuscated(boolean obfuscated) {
        LabyModCoreMod.obfuscated = obfuscated;
    }

    public static boolean isForge() {
        return forge;
    }

    public static void setForge(boolean forge) {
        LabyModCoreMod.forge = forge;
    }

    public String[] getASMTransformerClass() {
        StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
        int n2 = stackTraceElementArray.length;
        int n3 = 0;
        while (n3 < n2) {
            StackTraceElement stackTraceElement = stackTraceElementArray[n3];
            if (stackTraceElement.getClassName().contains("FMLPluginWrapper")) {
                forge = true;
                break;
            }
            ++n3;
        }
        ArrayList<String> transformers = new ArrayList<String>();
        transformers.add("net.labymod.core.asm.LabyModTransformer");
        if (forge) {
            transformers.addAll(AddonLoader.getTransformerClasses(Launch.classLoader));
        } else {
            transformers.addAll(AddonLoader.getTransformerClasses((URLClassLoader)this.getClass().getClassLoader()));
        }
        return transformers.toArray(new String[transformers.size()]);
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        obfuscated = (Boolean)data.get("runtimeDeobfuscationEnabled");
        forge = true;
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

