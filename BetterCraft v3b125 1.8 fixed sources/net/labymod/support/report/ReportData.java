/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.core.asm.LabyModCoreMod;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import org.apache.commons.lang3.SerializationUtils;

public class ReportData {
    private final Throwable cause;
    private Map<String, Callable<String>> entries = new HashMap<String, Callable<String>>();

    public ReportData(Throwable causeThrowable) {
        this.cause = causeThrowable;
        this.putAllEntries();
    }

    private void putAllEntries() {
        final Gson gson = new GsonBuilder().create();
        this.entries.put("mc_uuid", new Callable<String>(){

            @Override
            public String call() {
                return LabyMod.getInstance().getPlayerUUID().toString();
            }
        });
        this.entries.put("mc_username", new Callable<String>(){

            @Override
            public String call() {
                return LabyMod.getInstance().getPlayerName();
            }
        });
        this.entries.put("mc_version", new Callable<String>(){

            @Override
            public String call() {
                return Source.ABOUT_MC_VERSION;
            }
        });
        this.entries.put("labymod_version", new Callable<String>(){

            @Override
            public String call() {
                return "3.6.6";
            }
        });
        this.entries.put("os", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("os.name")) + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.entries.put("java_version", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("java.version")) + ", " + System.getProperty("java.vendor");
            }
        });
        this.entries.put("java_vm_version", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(System.getProperty("java.vm.name")) + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.entries.put("mc_memory", new Callable<String>(){

            @Override
            public String call() {
                Runtime runtime = Runtime.getRuntime();
                long i2 = runtime.maxMemory();
                long j2 = runtime.totalMemory();
                long k2 = runtime.freeMemory();
                long l2 = i2 / 1024L / 1024L;
                long i22 = j2 / 1024L / 1024L;
                long j22 = k2 / 1024L / 1024L;
                return String.valueOf(k2) + " bytes (" + j22 + " MB) / " + j2 + " bytes (" + i22 + " MB) up to " + i2 + " bytes (" + l2 + " MB)";
            }
        });
        this.entries.put("jvm_flags", new Callable<String>(){

            @Override
            public String call() {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List<String> list = runtimemxbean.getInputArguments();
                return gson.toJson(list, new TypeToken<List<String>>(){}.getType());
            }
        });
        this.entries.put("addons", new Callable<String>(){

            @Override
            public String call() {
                ArrayList<String> list = new ArrayList<String>();
                for (LabyModAddon addon : AddonLoader.getAddons()) {
                    if (addon == null || addon.about == null || addon.about.uuid == null || addon.about.name == null) continue;
                    list.add(String.valueOf(addon.about.name) + " (" + addon.about.uuid.toString() + ")");
                }
                return gson.toJson(list, new TypeToken<List<String>>(){}.getType());
            }
        });
        this.entries.put("vanilla_forge", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(LabyModCoreMod.isForge() ? 0 : 1);
            }
        });
        this.entries.put("exception", new Callable<String>(){

            @Override
            public String call() {
                byte[] bytes = SerializationUtils.serialize(ReportData.this.cause);
                return Base64.getEncoder().encodeToString(bytes);
            }
        });
    }

    public String createJsonReport() {
        JsonObject reportData = new JsonObject();
        for (Map.Entry<String, Callable<String>> entry : this.entries.entrySet()) {
            String key = entry.getKey();
            String value = "Unknown";
            try {
                value = entry.getValue().call();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            reportData.addProperty(key, value);
        }
        return reportData.toString();
    }
}

