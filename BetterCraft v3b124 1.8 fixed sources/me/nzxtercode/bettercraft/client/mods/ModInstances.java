/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods;

import java.util.ArrayList;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import me.nzxtercode.bettercraft.client.mods.impl.ModArmorStatus;
import me.nzxtercode.bettercraft.client.mods.impl.ModInfo;
import me.nzxtercode.bettercraft.client.mods.impl.ModKeystrokes;
import me.nzxtercode.bettercraft.client.mods.impl.ModRadar;
import me.nzxtercode.bettercraft.client.mods.impl.ModSkin;

public class ModInstances {
    public static ArrayList<ModRender> getAllMods = new ArrayList();
    private static ModArmorStatus modArmorStatus;
    private static ModInfo modInfo;
    private static ModKeystrokes modKeyStrokes;
    private static ModSkin modSkin;
    private static ModRadar modRadar;

    public static void register(HUDManager hud) {
        modArmorStatus = new ModArmorStatus();
        hud.register(modArmorStatus);
        getAllMods.add(modArmorStatus);
        modInfo = new ModInfo();
        hud.register(modInfo);
        getAllMods.add(modInfo);
        modKeyStrokes = new ModKeystrokes();
        hud.register(modKeyStrokes);
        getAllMods.add(modKeyStrokes);
        modSkin = new ModSkin();
        hud.register(modSkin);
        getAllMods.add(modSkin);
        modRadar = new ModRadar();
        hud.register(modRadar);
        getAllMods.add(modRadar);
    }

    public static ArrayList<ModRender> getAllMods() {
        return getAllMods;
    }
}

