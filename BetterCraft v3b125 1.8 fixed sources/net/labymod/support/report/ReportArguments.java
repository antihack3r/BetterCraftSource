/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.report;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class ReportArguments {
    private String title;
    private String minecraftCrashReportFile;
    private String crashReportJson;
    private String addonPath;
    private UUID addonUUID;
    private String addonName;

    public String getTitle() {
        return this.title;
    }

    public String getMinecraftCrashReportFile() {
        return this.minecraftCrashReportFile;
    }

    public String getCrashReportJson() {
        return this.crashReportJson;
    }

    public String getAddonPath() {
        return this.addonPath;
    }

    public UUID getAddonUUID() {
        return this.addonUUID;
    }

    public String getAddonName() {
        return this.addonName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMinecraftCrashReportFile(String minecraftCrashReportFile) {
        this.minecraftCrashReportFile = minecraftCrashReportFile;
    }

    public void setCrashReportJson(String crashReportJson) {
        this.crashReportJson = crashReportJson;
    }

    public void setAddonPath(String addonPath) {
        this.addonPath = addonPath;
    }

    public void setAddonUUID(UUID addonUUID) {
        this.addonUUID = addonUUID;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public ReportArguments() {
    }

    @ConstructorProperties(value={"title", "minecraftCrashReportFile", "crashReportJson", "addonPath", "addonUUID", "addonName"})
    public ReportArguments(String title, String minecraftCrashReportFile, String crashReportJson, String addonPath, UUID addonUUID, String addonName) {
        this.title = title;
        this.minecraftCrashReportFile = minecraftCrashReportFile;
        this.crashReportJson = crashReportJson;
        this.addonPath = addonPath;
        this.addonUUID = addonUUID;
        this.addonName = addonName;
    }
}

