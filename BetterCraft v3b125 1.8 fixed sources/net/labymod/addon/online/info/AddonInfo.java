/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon.online.info;

import java.io.File;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.LabyModOFAddon;
import net.labymod.addon.online.AddonDownloader;
import net.labymod.addon.online.CallbackAddonDownloadProcess;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.AddonElement;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;

public class AddonInfo {
    protected UUID uuid;
    protected String name;
    protected int version;
    protected String author;
    protected String description;
    protected int category;
    protected AddonElement addonElement;
    protected String offlineIcon = null;

    public AddonInfo(UUID uuid, String name, int version, String author, String description, int category) {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description.replaceAll("\r", "");
        this.category = category;
        LabyModAddon installedAddon = AddonLoader.getInstalledAddonByInfo(this);
        this.addonElement = new AddonElement(this, installedAddon, new Consumer<AddonActionState>(){

            @Override
            public void accept(AddonActionState state) {
                AddonInfo.this.addonElement.setLastActionState(state);
                if (state == AddonActionState.INSTALL && AddonInfo.this instanceof OnlineAddonInfo) {
                    final OnlineAddonInfo onlineAddonInfo = (OnlineAddonInfo)AddonInfo.this;
                    new AddonDownloader(onlineAddonInfo, new CallbackAddonDownloadProcess(){

                        @Override
                        public void success(File file) {
                            if (onlineAddonInfo.isIncludeInJar()) {
                                LabyModOFAddon.INSTALL = true;
                                LabyModOFAddon.downloadOFHandler();
                                return;
                            }
                            if (!onlineAddonInfo.restart) {
                                (this).AddonInfo.this.addonElement.setLastActionState(AddonActionState.UNINSTALL_REVOKE);
                                AddonLoader.resolveJarFile(file, this.getClass().getClassLoader());
                                LabyModAddon loadedAddon = AddonLoader.enableAddon((this).AddonInfo.this.uuid, LabyMod.getInstance().getLabyModAPI());
                                if (loadedAddon == null) {
                                    (this).AddonInfo.this.addonElement.setLastActionState(AddonActionState.ERROR);
                                } else {
                                    (this).AddonInfo.this.addonElement.setInstalledAddon(loadedAddon);
                                }
                                if (loadedAddon != null) {
                                    (this).AddonInfo.this.addonElement.getSubSettings().clear();
                                    (this).AddonInfo.this.addonElement.getSubSettings().addAll(loadedAddon.getSubSettings());
                                }
                            } else {
                                AddonLoader.getFiles().put((this).AddonInfo.this.uuid, file);
                            }
                        }

                        @Override
                        public void progress(double percent) {
                            (this).AddonInfo.this.addonElement.setInstallProgress(percent);
                        }

                        @Override
                        public void failed(String message) {
                        }
                    }).start();
                }
                if (state == AddonActionState.UNINSTALL && AddonInfo.this.addonElement.isAddonInstalled()) {
                    AddonInfo.this.addonElement.getInstalledAddon().about.deleted = true;
                    if (AddonInfo.this instanceof OnlineAddonInfo && ((OnlineAddonInfo)AddonInfo.this).isIncludeInJar()) {
                        LabyModOFAddon.downloadOFHandler();
                    }
                }
                if (state == AddonActionState.UNINSTALL_REVOKE && AddonInfo.this.addonElement.isAddonInstalled()) {
                    AddonInfo.this.addonElement.getInstalledAddon().about.deleted = false;
                }
                if (state == AddonActionState.INSTALL_REVOKE) {
                    if (AddonInfo.this instanceof OnlineAddonInfo && ((OnlineAddonInfo)AddonInfo.this).isIncludeInJar()) {
                        LabyModOFAddon.INSTALL = false;
                    } else {
                        File file = AddonLoader.getFiles().get(AddonInfo.this.uuid);
                        if (file != null && file.exists() && file.delete()) {
                            Debug.log(Debug.EnumDebugMode.ADDON, "Successfully deleted addon " + AddonInfo.this.name);
                        } else {
                            Debug.log(Debug.EnumDebugMode.ADDON, "Error while deleting addon " + AddonInfo.this.name);
                        }
                    }
                }
            }
        });
        if (installedAddon != null) {
            this.addonElement.getSubSettings().addAll(installedAddon.getSubSettings());
        }
    }

    public String getDownloadURL() {
        return String.format("http://dl.labymod.net/latest/?file=%s&a=1", this.uuid);
    }

    public String getImageURL() {
        return this instanceof OnlineAddonInfo ? String.format("http://dl.labymod.net/latest/addons/%s/icon.png", this.uuid) : this.offlineIcon;
    }

    public File getFileDestination() {
        return new File(AddonLoader.getAddonsDirectory(), String.valueOf(this.name) + ".jar");
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCategory() {
        return this.category;
    }

    public AddonElement getAddonElement() {
        return this.addonElement;
    }

    public String getOfflineIcon() {
        return this.offlineIcon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfflineIcon(String offlineIcon) {
        this.offlineIcon = offlineIcon;
    }

    public static enum AddonActionState {
        INSTALL,
        INSTALL_REVOKE,
        UNINSTALL,
        UNINSTALL_REVOKE,
        ERROR;

    }
}

