/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.util;

import java.io.File;
import net.labymod.support.DebugConsoleGui;
import net.labymod.utils.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Debug {
    public static final File DEBUG_FILE = new File("LabyMod/", ".debug");
    private static boolean active = false;
    private static final Logger logger = LogManager.getLogger();
    private static DebugConsoleGui debugConsoleGui;

    public static void init() {
        if (DEBUG_FILE.exists()) {
            DEBUG_FILE.delete();
            Debug.openDebugConsole();
        } else {
            String debugMode = System.getProperty("debugMode");
            if (debugMode != null) {
                if (debugMode.equals("true")) {
                    active = true;
                    logger.info("[Debug] Started debug logging");
                } else if (debugMode.equals("GUI")) {
                    Debug.openDebugConsole();
                } else if (!debugMode.equals("false")) {
                    logger.info("[Debug] Invalid debug mode: " + debugMode);
                }
            }
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static void openDebugConsole() {
        if (debugConsoleGui != null) {
            debugConsoleGui.toFront();
            return;
        }
        debugConsoleGui = new DebugConsoleGui(new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                Debug.debugConsoleGui = null;
            }
        });
        active = true;
        logger.info("[Debug] Started debug GUI");
    }

    public static void log(EnumDebugMode debugMode, String message) {
        if (!active) {
            return;
        }
        logger.info("{}", "[Debug] [" + debugMode.name() + "] " + message);
    }

    public static DebugConsoleGui getDebugConsoleGui() {
        return debugConsoleGui;
    }

    static /* synthetic */ DebugConsoleGui access$0() {
        return debugConsoleGui;
    }

    public static enum EnumDebugMode {
        ADDON,
        API,
        UPDATER,
        COSMETIC_IMAGE_MANAGER,
        USER_MANAGER,
        MINECRAFT,
        TEAMSPEAK,
        LABYMOD_CHAT,
        ACCOUNT_MANAGER,
        CONFIG_MANAGER,
        ASM,
        PLUGINMESSAGE,
        GENERAL,
        LANGUAGE,
        CCP,
        EMOTE,
        STICKER,
        DISCORD,
        LABY_PLAY;

    }
}

