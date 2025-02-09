/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.report;

import java.io.File;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.main.Updater;
import net.labymod.main.lang.LanguageManager;
import net.labymod.support.report.ReportArguments;
import net.labymod.support.report.ReportData;
import net.minecraft.crash.CrashReport;

public class CrashReportHandler {
    private static final CrashReportHandler INSTANCE = new CrashReportHandler();

    public void report(File mcReportFile, CrashReport mcCrashReport) {
        Updater updater;
        ReportData reportData = new ReportData(mcCrashReport.getCrashCause());
        Updater updater2 = updater = LabyMod.getInstance() == null ? null : LabyMod.getInstance().getUpdater();
        if (updater == null) {
            updater = new Updater();
        }
        ReportArguments reportArguments = new ReportArguments();
        reportArguments.setMinecraftCrashReportFile(mcReportFile.getAbsolutePath());
        reportArguments.setCrashReportJson(reportData.createJsonReport());
        String key = mcCrashReport.getCrashCause() instanceof OutOfMemoryError ? "memory" : (this.isCrashCausedByMinecraft(mcCrashReport) ? "minecraft" : "labymod");
        reportArguments.setTitle(LanguageManager.translate("crash_reporter_title_" + key));
        UUID uuid = this.getTargetAddonUUID(mcCrashReport);
        if (uuid != null) {
            reportArguments.setAddonUUID(uuid);
            LabyModAddon addon = AddonLoader.getAddonByUUID(uuid);
            File jarFile = AddonLoader.getFiles().get(uuid);
            if (jarFile != null) {
                reportArguments.setAddonPath(jarFile.getAbsolutePath());
                reportArguments.setAddonName(jarFile.getName());
            }
            if (addon != null && addon.about != null && addon.about.name != null && !addon.about.name.isEmpty()) {
                reportArguments.setAddonName(addon.about.name);
            }
            if (reportArguments.getAddonName() != null) {
                reportArguments.setTitle(LanguageManager.translate("crash_reporter_title_addon", reportArguments.getAddonName()));
            }
        }
        updater.executeReport(reportArguments);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean isCrashCausedByMinecraft(CrashReport mcCrashReport) {
        try {
            StackTraceElement[] stackTraceElementArray = mcCrashReport.getCrashCause().getStackTrace();
            int n2 = stackTraceElementArray.length;
            int n3 = 0;
            while (true) {
                if (n3 >= n2) {
                    return true;
                }
                StackTraceElement frame = stackTraceElementArray[n3];
                if (frame != null && frame.getClassName() != null && frame.getClassName().toLowerCase().contains("labymod")) {
                    return false;
                }
                ++n3;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        return true;
    }

    private UUID getTargetAddonUUID(CrashReport mcCrashReport) {
        try {
            StackTraceElement[] stackTraceElementArray = mcCrashReport.getCrashCause().getStackTrace();
            int n2 = stackTraceElementArray.length;
            int n3 = 0;
            while (n3 < n2) {
                StackTraceElement frame = stackTraceElementArray[n3];
                try {
                    Class<?> type = Class.forName(frame.getClassName());
                    UUID uuid = AddonLoader.getUUIDByClass(type);
                    if (uuid != null) {
                        return uuid;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                ++n3;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    public static CrashReportHandler getInstance() {
        return INSTANCE;
    }
}

