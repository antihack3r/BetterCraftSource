/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.ShaderMacro;
import net.optifine.shaders.config.ShaderMacros;
import net.optifine.shaders.config.ShaderOption;

public class MacroProcessor {
    public static InputStream process(InputStream in2, String path) throws IOException {
        String s2 = Config.readInputStream(in2, "ASCII");
        String s1 = MacroProcessor.getMacroHeader(s2);
        if (!s1.isEmpty()) {
            s2 = String.valueOf(s1) + s2;
            if (Shaders.saveFinalShaders) {
                String s22 = String.valueOf(path.replace(':', '/')) + ".pre";
                Shaders.saveShader(s22, s2);
            }
            s2 = MacroProcessor.process(s2);
        }
        if (Shaders.saveFinalShaders) {
            String s3 = path.replace(':', '/');
            Shaders.saveShader(s3, s2);
        }
        byte[] abyte = s2.getBytes("ASCII");
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
        return bytearrayinputstream;
    }

    public static String process(String strIn) throws IOException {
        StringReader stringreader = new StringReader(strIn);
        BufferedReader bufferedreader = new BufferedReader(stringreader);
        MacroState macrostate = new MacroState();
        StringBuilder stringbuilder = new StringBuilder();
        while (true) {
            String s2;
            if ((s2 = bufferedreader.readLine()) == null) {
                s2 = stringbuilder.toString();
                return s2;
            }
            if (!macrostate.processLine(s2) || MacroState.isMacroLine(s2)) continue;
            stringbuilder.append(s2);
            stringbuilder.append("\n");
        }
    }

    /*
     * Unable to fully structure code
     */
    private static String getMacroHeader(String str) throws IOException {
        stringbuilder = new StringBuilder();
        list = null;
        list1 = null;
        stringreader = new StringReader(str);
        bufferedreader = new BufferedReader(stringreader);
        block0: while (true) {
            if ((s = bufferedreader.readLine()) == null) {
                return stringbuilder.toString();
            }
            if (!MacroState.isMacroLine(s)) continue;
            if (stringbuilder.length() == 0) {
                stringbuilder.append(ShaderMacros.getFixedMacroLines());
            }
            if (list1 == null) {
                list1 = new ArrayList<ShaderMacro>(Arrays.asList(ShaderMacros.getExtensions()));
            }
            iterator = list1.iterator();
            while (true) {
                if (iterator.hasNext()) ** break;
                continue block0;
                shadermacro = (ShaderMacro)iterator.next();
                if (!s.contains(shadermacro.getName())) continue;
                stringbuilder.append(shadermacro.getSourceLine());
                stringbuilder.append("\n");
                iterator.remove();
            }
            break;
        }
    }

    private static List<ShaderOption> getMacroOptions() {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        ShaderOption[] ashaderoption = Shaders.getShaderPackOptions();
        int i2 = 0;
        while (i2 < ashaderoption.length) {
            ShaderOption shaderoption = ashaderoption[i2];
            String s2 = shaderoption.getSourceLine();
            if (s2 != null && s2.startsWith("#")) {
                list.add(shaderoption);
            }
            ++i2;
        }
        return list;
    }
}

