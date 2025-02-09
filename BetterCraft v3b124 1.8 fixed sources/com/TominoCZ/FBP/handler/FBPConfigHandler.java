/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPObfUtil;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import net.minecraft.block.material.Material;

public class FBPConfigHandler {
    static FileInputStream fis;
    static InputStreamReader isr;
    static BufferedReader br;

    public static void init() {
        try {
            FBPConfigHandler.defaults(false);
            if (!Paths.get(FBP.config.getParent(), new String[0]).toFile().exists()) {
                Paths.get(FBP.config.getParent(), new String[0]).toFile().mkdirs();
            }
            if (!FBP.config.exists()) {
                FBP.config.createNewFile();
                FBPConfigHandler.write();
            }
            if (!FBP.particleBlacklistFile.exists()) {
                FBP.particleBlacklistFile.createNewFile();
            }
            if (!FBP.floatingMaterialsFile.exists()) {
                FBP.floatingMaterialsFile.createNewFile();
                FBP.getInstance().floatingMaterials.clear();
                FBP.getInstance().floatingMaterials.add(Material.leaves);
                FBP.getInstance().floatingMaterials.add(Material.plants);
                FBP.getInstance().floatingMaterials.add(Material.ice);
                FBP.getInstance().floatingMaterials.add(Material.packedIce);
                FBP.getInstance().floatingMaterials.add(Material.carpet);
                FBP.getInstance().floatingMaterials.add(Material.wood);
                FBP.getInstance().floatingMaterials.add(Material.web);
            } else {
                FBPConfigHandler.readFloatingMaterials();
            }
            FBPConfigHandler.read();
            FBPConfigHandler.readParticleBlacklist();
            FBPConfigHandler.write();
            FBPConfigHandler.writeParticleBlacklist();
            FBPConfigHandler.writeFloatingMaterials();
            FBPConfigHandler.closeStreams();
        }
        catch (IOException e2) {
            FBPConfigHandler.closeStreams();
            FBPConfigHandler.write();
        }
    }

    public static void write() {
        try {
            PrintWriter writer = new PrintWriter(FBP.config.getPath(), "UTF-8");
            writer.println("enabled=" + FBP.enabled);
            writer.println("weatherParticleDensity=" + FBP.weatherParticleDensity);
            writer.println("particlesPerAxis=" + FBP.particlesPerAxis);
            writer.println("restOnFloor=" + FBP.restOnFloor);
            writer.println("waterPhysics=" + FBP.waterPhysics);
            writer.println("fancyFlame=" + FBP.fancyFlame);
            writer.println("fancySmoke=" + FBP.fancySmoke);
            writer.println("fancyRain=" + FBP.fancyRain);
            writer.println("fancySnow=" + FBP.fancySnow);
            writer.println("smartBreaking=" + FBP.smartBreaking);
            writer.println("lowTraction=" + FBP.lowTraction);
            writer.println("bounceOffWalls=" + FBP.bounceOffWalls);
            writer.println("showInMillis=" + FBP.showInMillis);
            writer.println("randomRotation=" + FBP.randomRotation);
            writer.println("cartoonMode=" + FBP.cartoonMode);
            writer.println("entityCollision=" + FBP.entityCollision);
            writer.println("randomizedScale=" + FBP.randomizedScale);
            writer.println("randomFadingSpeed=" + FBP.randomFadingSpeed);
            writer.println("spawnRedstoneBlockParticles=" + FBP.spawnRedstoneBlockParticles);
            writer.println("spawnWhileFrozen=" + FBP.spawnWhileFrozen);
            writer.println("infiniteDuration=" + FBP.infiniteDuration);
            writer.println("minAge=" + FBP.minAge);
            writer.println("maxAge=" + FBP.maxAge);
            writer.println("scaleMult=" + FBP.scaleMult);
            writer.println("gravityMult=" + FBP.gravityMult);
            writer.print("rotationMult=" + FBP.rotationMult);
            writer.close();
        }
        catch (Exception e2) {
            FBPConfigHandler.closeStreams();
            if (!FBP.config.exists()) {
                if (!Paths.get(FBP.config.getParent(), new String[0]).toFile().exists()) {
                    Paths.get(FBP.config.getParent(), new String[0]).toFile().mkdirs();
                }
                try {
                    FBP.config.createNewFile();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            FBPConfigHandler.write();
        }
    }

    public static void writeParticleBlacklist() {
        block6: {
            try {
                PrintWriter writer = new PrintWriter(FBP.particleBlacklistFile.getPath(), "UTF-8");
                for (String ex2 : FBP.getInstance().blockParticleBlacklist) {
                    writer.println(ex2);
                }
                writer.close();
            }
            catch (Exception e2) {
                FBPConfigHandler.closeStreams();
                if (FBP.particleBlacklistFile.exists()) break block6;
                if (!Paths.get(FBP.particleBlacklistFile.getParent(), new String[0]).toFile().exists()) {
                    Paths.get(FBP.particleBlacklistFile.getParent(), new String[0]).toFile().mkdirs();
                }
                try {
                    FBP.particleBlacklistFile.createNewFile();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    static void writeFloatingMaterials() {
        try {
            Field[] materials;
            PrintWriter writer = new PrintWriter(FBP.floatingMaterialsFile.getPath(), "UTF-8");
            Field[] fieldArray = materials = Material.class.getDeclaredFields();
            int n2 = materials.length;
            int n3 = 0;
            while (n3 < n2) {
                Field f2 = fieldArray[n3];
                String fieldName = f2.getName();
                if (f2.getType() == Material.class) {
                    String translated = FBPObfUtil.translateObfMaterialName(fieldName).toLowerCase();
                    try {
                        Material mat = (Material)f2.get(null);
                        if (mat != Material.air) {
                            boolean flag = FBP.getInstance().doesMaterialFloat(mat);
                            writer.println(String.valueOf(translated) + "=" + flag);
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                ++n3;
            }
            writer.close();
        }
        catch (Exception e2) {
            FBPConfigHandler.closeStreams();
        }
    }

    static void read() {
        try {
            String line;
            fis = new FileInputStream(FBP.config);
            isr = new InputStreamReader((InputStream)fis, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if ((line = line.replaceAll(" ", "")).contains("enabled=")) {
                    FBP.enabled = Boolean.valueOf(line.replace("enabled=", ""));
                    continue;
                }
                if (line.contains("weatherParticleDensity=")) {
                    FBP.weatherParticleDensity = Double.valueOf(line.replace("weatherParticleDensity=", ""));
                    continue;
                }
                if (line.contains("particlesPerAxis=")) {
                    FBP.particlesPerAxis = Integer.valueOf(line.replace("particlesPerAxis=", ""));
                    continue;
                }
                if (line.contains("restOnFloor=")) {
                    FBP.restOnFloor = Boolean.valueOf(line.replace("restOnFloor=", ""));
                    continue;
                }
                if (line.contains("waterPhysics=")) {
                    FBP.waterPhysics = Boolean.valueOf(line.replace("waterPhysics=", ""));
                    continue;
                }
                if (line.contains("fancyFlame=")) {
                    FBP.fancyFlame = Boolean.valueOf(line.replace("fancyFlame=", ""));
                    continue;
                }
                if (line.contains("fancySmoke=")) {
                    FBP.fancySmoke = Boolean.valueOf(line.replace("fancySmoke=", ""));
                    continue;
                }
                if (line.contains("fancyRain=")) {
                    FBP.fancyRain = Boolean.valueOf(line.replace("fancyRain=", ""));
                    continue;
                }
                if (line.contains("fancySnow=")) {
                    FBP.fancySnow = Boolean.valueOf(line.replace("fancySnow=", ""));
                    continue;
                }
                if (line.contains("smartBreaking=")) {
                    FBP.smartBreaking = Boolean.valueOf(line.replace("smartBreaking=", ""));
                    continue;
                }
                if (line.contains("lowTraction=")) {
                    FBP.lowTraction = Boolean.valueOf(line.replace("lowTraction=", ""));
                    continue;
                }
                if (line.contains("bounceOffWalls=")) {
                    FBP.bounceOffWalls = Boolean.valueOf(line.replace("bounceOffWalls=", ""));
                    continue;
                }
                if (line.contains("showInMillis=")) {
                    FBP.showInMillis = Boolean.valueOf(line.replace("showInMillis=", ""));
                    continue;
                }
                if (line.contains("randomRotation=")) {
                    FBP.randomRotation = Boolean.valueOf(line.replace("randomRotation=", ""));
                    continue;
                }
                if (line.contains("cartoonMode=")) {
                    FBP.cartoonMode = Boolean.valueOf(line.replace("cartoonMode=", ""));
                    continue;
                }
                if (line.contains("entityCollision=")) {
                    FBP.entityCollision = Boolean.valueOf(line.replace("entityCollision=", ""));
                    continue;
                }
                if (line.contains("randomFadingSpeed=")) {
                    FBP.randomFadingSpeed = Boolean.valueOf(line.replace("randomFadingSpeed=", ""));
                    continue;
                }
                if (line.contains("smoothTransitions=")) {
                    FBP.randomizedScale = Boolean.valueOf(line.replace("randomizedScale=", ""));
                    continue;
                }
                if (line.contains("spawnWhileFrozen=")) {
                    FBP.spawnWhileFrozen = Boolean.valueOf(line.replace("spawnWhileFrozen=", ""));
                    continue;
                }
                if (line.contains("spawnRedstoneBlockParticles=")) {
                    FBP.spawnRedstoneBlockParticles = Boolean.valueOf(line.replace("spawnRedstoneBlockParticles=", ""));
                    continue;
                }
                if (line.contains("infiniteDuration=")) {
                    FBP.infiniteDuration = Boolean.valueOf(line.replace("infiniteDuration=", ""));
                    continue;
                }
                if (line.contains("minAge=")) {
                    FBP.minAge = Integer.valueOf(line.replace("minAge=", ""));
                    continue;
                }
                if (line.contains("maxAge=")) {
                    FBP.maxAge = Integer.valueOf(line.replace("maxAge=", ""));
                    continue;
                }
                if (line.contains("scaleMult=")) {
                    FBP.scaleMult = Double.valueOf(line.replace("scaleMult=", ""));
                    continue;
                }
                if (line.contains("gravityMult=")) {
                    FBP.gravityMult = Double.valueOf(line.replace("gravityMult=", ""));
                    continue;
                }
                if (!line.contains("rotationMult=")) continue;
                FBP.rotationMult = Double.valueOf(line.replace("rotationMult=", ""));
            }
            FBPConfigHandler.closeStreams();
        }
        catch (Exception e2) {
            FBPConfigHandler.closeStreams();
            FBPConfigHandler.write();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static void readParticleBlacklist() {
        try {
            String line;
            fis = new FileInputStream(FBP.particleBlacklistFile);
            isr = new InputStreamReader((InputStream)fis, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            FBP.getInstance().resetBlacklist();
            while ((line = br.readLine()) != null && !(line = line.replaceAll(" ", "").toLowerCase()).equals("")) {
                FBP.getInstance().addToBlacklist(line);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        FBPConfigHandler.closeStreams();
    }

    static void readFloatingMaterials() {
        try {
            String line;
            fis = new FileInputStream(FBP.floatingMaterialsFile);
            isr = new InputStreamReader((InputStream)fis, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            FBP.getInstance().floatingMaterials.clear();
            Field[] materials = Material.class.getDeclaredFields();
            while ((line = br.readLine()) != null) {
                String[] split = (line = line.trim().toLowerCase()).split("=");
                if (split.length < 2) continue;
                String materialName = split[0].replace("_", "");
                boolean flag = Boolean.parseBoolean(split[1]);
                if (!flag) continue;
                boolean found = false;
                Field[] fieldArray = materials;
                int n2 = materials.length;
                int n3 = 0;
                while (n3 < n2) {
                    String translated;
                    Field f2 = fieldArray[n3];
                    String fieldName = f2.getName();
                    if (f2.getType() == Material.class && materialName.equals(translated = FBPObfUtil.translateObfMaterialName(fieldName).toLowerCase().replace("_", ""))) {
                        try {
                            Material mat = (Material)f2.get(null);
                            if (!FBP.getInstance().floatingMaterials.contains(mat)) {
                                FBP.getInstance().floatingMaterials.add(mat);
                            }
                            found = true;
                            break;
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    ++n3;
                }
                if (found) continue;
                System.out.println("[FBP]: Material not recognized: " + materialName);
            }
            FBPConfigHandler.closeStreams();
        }
        catch (Exception e2) {
            FBPConfigHandler.closeStreams();
            FBPConfigHandler.write();
        }
    }

    static void closeStreams() {
        try {
            br.close();
            isr.close();
            fis.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void defaults(boolean write) {
        FBP.minAge = 10;
        FBP.maxAge = 55;
        FBP.scaleMult = 0.75;
        FBP.gravityMult = 1.0;
        FBP.rotationMult = 1.0;
        FBP.particlesPerAxis = 4;
        FBP.weatherParticleDensity = 1.0;
        FBP.lowTraction = false;
        FBP.bounceOffWalls = true;
        FBP.randomRotation = true;
        FBP.cartoonMode = false;
        FBP.entityCollision = false;
        FBP.randomizedScale = true;
        FBP.randomFadingSpeed = true;
        FBP.spawnRedstoneBlockParticles = false;
        FBP.infiniteDuration = false;
        FBP.spawnWhileFrozen = true;
        FBP.smartBreaking = true;
        FBP.fancyRain = true;
        FBP.fancySnow = true;
        FBP.fancySmoke = true;
        FBP.fancyFlame = true;
        FBP.waterPhysics = true;
        FBP.restOnFloor = true;
        if (write) {
            FBPConfigHandler.write();
        }
    }
}

