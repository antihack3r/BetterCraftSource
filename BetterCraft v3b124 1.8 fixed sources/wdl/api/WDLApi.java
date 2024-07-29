/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.EntityRealigner;
import wdl.HologramHandler;
import wdl.MessageTypeCategory;
import wdl.WDL;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.api.IMessageTypeAdder;
import wdl.api.IWDLMessageType;
import wdl.api.IWDLMod;
import wdl.api.IWDLModDescripted;

public class WDLApi {
    private static Logger logger = LogManager.getLogger();
    private static Map<String, ModInfo<?>> wdlMods = new HashMap();

    static {
        logger.info("Loading default WDL extensions");
        WDLApi.addWDLMod("Hologram", "1.0", new HologramHandler());
        WDLApi.addWDLMod("EntityRealigner", "1.0", new EntityRealigner());
    }

    public static void saveTileEntity(BlockPos pos, TileEntity te2) {
        if (!WDLPluginChannels.canSaveTileEntities(pos.getX() << 16, pos.getZ() << 16)) {
            logger.warn("API attempted to call saveTileEntity when saving TileEntities is not allowed!  Pos: " + pos + ", te: " + te2 + ".  StackTrace: ");
            WDLApi.logStackTrace();
            return;
        }
        WDL.saveTileEntity(pos, te2);
    }

    public static void addWDLMod(String id2, String version, IWDLMod mod) {
        if (id2 == null) {
            throw new IllegalArgumentException("id must not be null!  (mod=" + mod + ", version=" + version + ")");
        }
        if (version == null) {
            throw new IllegalArgumentException("version must not be null!  (mod=" + mod + ", id=" + version + ")");
        }
        if (mod == null) {
            throw new IllegalArgumentException("mod must not be null!  (id=" + id2 + ", version=" + version + ")");
        }
        ModInfo info = new ModInfo(id2, version, mod, null);
        if (wdlMods.containsKey(id2)) {
            throw new IllegalArgumentException("A mod by the name of '" + id2 + "' is already registered by " + wdlMods.get(id2) + " (tried to register " + info + " over it)");
        }
        if (!mod.isValidEnvironment("1.8.9a-beta2")) {
            String errorMessage = mod.getEnvironmentErrorMessage("1.8.9a-beta2");
            if (errorMessage != null) {
                throw new IllegalArgumentException(errorMessage);
            }
            throw new IllegalArgumentException("Environment for " + info + " is incorrect!  Perhaps it is for a different" + " version of WDL?  You are running " + "1.8.9a-beta2" + ".");
        }
        wdlMods.put(id2, info);
        if (mod instanceof IMessageTypeAdder) {
            Map<String, IWDLMessageType> types = ((IMessageTypeAdder)mod).getMessageTypes();
            ModMessageTypeCategory category = new ModMessageTypeCategory(info);
            for (Map.Entry<String, IWDLMessageType> e2 : types.entrySet()) {
                WDLMessages.registerMessage(e2.getKey(), e2.getValue(), category);
            }
        }
    }

    public static <T extends IWDLMod> List<ModInfo<T>> getImplementingExtensions(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null!");
        }
        ArrayList<ModInfo<T>> returned = new ArrayList<ModInfo<T>>();
        for (ModInfo<?> info : wdlMods.values()) {
            if (!info.isEnabled() || !clazz.isAssignableFrom(info.mod.getClass())) continue;
            ModInfo<?> infoCasted = info;
            returned.add(infoCasted);
        }
        return returned;
    }

    public static <T extends IWDLMod> List<ModInfo<T>> getAllImplementingExtensions(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null!");
        }
        ArrayList<ModInfo<T>> returned = new ArrayList<ModInfo<T>>();
        for (ModInfo<?> info : wdlMods.values()) {
            if (!clazz.isAssignableFrom(info.mod.getClass())) continue;
            ModInfo<?> infoCasted = info;
            returned.add(infoCasted);
        }
        return returned;
    }

    public static Map<String, ModInfo<?>> getWDLMods() {
        return ImmutableMap.copyOf(wdlMods);
    }

    public static String getModInfo(String name) {
        if (!wdlMods.containsKey(name)) {
            return null;
        }
        return wdlMods.get(name).getInfo();
    }

    private static void logStackTrace() {
        StackTraceElement[] elements;
        StackTraceElement[] stackTraceElementArray = elements = Thread.currentThread().getStackTrace();
        int n2 = elements.length;
        int n3 = 0;
        while (n3 < n2) {
            StackTraceElement e2 = stackTraceElementArray[n3];
            logger.warn(e2.toString());
            ++n3;
        }
    }

    public static class ModInfo<T extends IWDLMod> {
        public final String id;
        public final String version;
        public final T mod;

        private ModInfo(String id2, String version, T mod) {
            this.id = id2;
            this.version = version;
            this.mod = mod;
        }

        public String toString() {
            return String.valueOf(this.id) + "v" + this.version + " (" + this.mod.toString() + "/" + this.mod.getClass().getName() + ")";
        }

        public String getDisplayName() {
            String name;
            if (this.mod instanceof IWDLModDescripted && (name = ((IWDLModDescripted)this.mod).getDisplayName()) != null && !name.isEmpty()) {
                return name;
            }
            return this.id;
        }

        public String getInfo() {
            StringBuilder info = new StringBuilder();
            info.append("Id: ").append(this.id).append('\n');
            info.append("Version: ").append(this.version).append('\n');
            if (this.mod instanceof IWDLModDescripted) {
                IWDLModDescripted dmod = (IWDLModDescripted)this.mod;
                String displayName = dmod.getDisplayName();
                String mainAuthor = dmod.getMainAuthor();
                String[] authors = dmod.getAuthors();
                String url = dmod.getURL();
                String description = dmod.getDescription();
                if (displayName != null && !displayName.isEmpty()) {
                    info.append("Display name: ").append(displayName).append('\n');
                }
                if (mainAuthor != null && !mainAuthor.isEmpty()) {
                    info.append("Main author: ").append(mainAuthor).append('\n');
                }
                if (authors != null && authors.length > 0) {
                    info.append("Authors: ");
                    int i2 = 0;
                    while (i2 < authors.length) {
                        if (!authors[i2].equals(mainAuthor)) {
                            if (i2 <= authors.length - 2) {
                                info.append(", ");
                            } else if (i2 == authors.length - 1) {
                                info.append(", and ");
                            } else {
                                info.append('\n');
                            }
                        }
                        ++i2;
                    }
                }
                if (url != null && !url.isEmpty()) {
                    info.append("URL: ").append(url).append('\n');
                }
                if (description != null && !description.isEmpty()) {
                    info.append("Description: \n").append(description).append('\n');
                }
            }
            info.append("Main class: ").append(this.mod.getClass().getName()).append('\n');
            info.append("Containing file: ");
            try {
                String path = new File(this.mod.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                String username = System.getProperty("user.name");
                path = path.replace(username, "<USERNAME>");
                info.append(path);
            }
            catch (Exception e2) {
                info.append("Unknown (").append(e2.toString()).append(')');
            }
            info.append('\n');
            Class<?>[] interfaces = this.mod.getClass().getInterfaces();
            info.append("Implemented interfaces (").append(interfaces.length).append(")\n");
            int i3 = 0;
            while (i3 < interfaces.length) {
                info.append(i3).append(": ").append(interfaces[i3].getName()).append('\n');
                ++i3;
            }
            info.append("Superclass: ").append(this.mod.getClass().getSuperclass().getName()).append('\n');
            ClassLoader loader = this.mod.getClass().getClassLoader();
            info.append("Classloader: ").append(loader);
            if (loader != null) {
                info.append(" (").append(loader.getClass().getName()).append(')');
            }
            info.append('\n');
            Annotation[] annotations = this.mod.getClass().getAnnotations();
            info.append("Annotations (").append(annotations.length).append(")\n");
            int i4 = 0;
            while (i4 < annotations.length) {
                info.append(i4).append(": ").append(annotations[i4].toString()).append(" (").append(annotations[i4].annotationType().getName()).append(")\n");
                ++i4;
            }
            return info.toString();
        }

        public boolean isEnabled() {
            return WDL.globalProps.getProperty("Extensions." + this.id + ".enabled", "true").equals("true");
        }

        public void setEnabled(boolean enabled) {
            WDL.globalProps.setProperty("Extensions." + this.id + ".enabled", Boolean.toString(enabled));
            WDL.saveGlobalProps();
        }

        public void toggleEnabled() {
            this.setEnabled(!this.isEnabled());
        }

        /* synthetic */ ModInfo(String string, String string2, IWDLMod iWDLMod, ModInfo modInfo) {
            this(string, string2, iWDLMod);
        }
    }

    private static class ModMessageTypeCategory
    extends MessageTypeCategory {
        private ModInfo<?> mod;

        public ModMessageTypeCategory(ModInfo<?> mod) {
            super(mod.id);
        }

        @Override
        public String getDisplayName() {
            return this.mod.getDisplayName();
        }
    }
}

