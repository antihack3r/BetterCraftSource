/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.labymod.account.AccountLoginHandler;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyModForge;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

public class GuiOpenListener {
    private static final boolean IS_MC_18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    private GuiScreen lastScreen;
    private Field defaultInputTextField;
    private Boolean replayModInstalled = null;
    private boolean replacedThings;
    private static final String[] serverListServerData = LabyModCore.getMappingAdapter().getServerListServerDataMappings();
    private static final String[] entityRendererItemRendererNames = LabyModCore.getMappingAdapter().getEntityRendererItemRendererMappings();
    private static final String[] entityRendererNames = LabyModCore.getMappingAdapter().getEntityRendererMappings();
    private static final String[] itemRendererNames = LabyModCore.getMappingAdapter().getItemRendererMappings();
    private static final String[] upperChestInventory = LabyModCore.getMappingAdapter().getUpperChestInventoryMappings();
    private static final String[] lowerChestInventory = LabyModCore.getMappingAdapter().getLowerChestInventoryMappings();

    public GuiOpenListener() {
        try {
            this.defaultInputTextField = AccountLoginHandler.ReflectionHelper.findField(GuiChat.class, LabyModCore.getMappingAdapter().getDefaultInputFieldTextMappings());
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    private boolean isReplayModInstalled() {
        if (!LabyModForge.isForge()) {
            return false;
        }
        if (this.replayModInstalled != null) {
            return this.replayModInstalled;
        }
        try {
            Class<?> loader = Class.forName("net.minecraftforge.fml.common.Loader");
            Method method = loader.getMethod("isModLoaded", String.class);
            boolean result = (Boolean)method.invoke(null, "replaymod");
            this.replayModInstalled = result;
            if (result) {
                Debug.log(Debug.EnumDebugMode.GENERAL, "ReplayMod detected!");
            }
        }
        catch (Exception e2) {
            this.replayModInstalled = false;
        }
        return this.replayModInstalled;
    }
}

