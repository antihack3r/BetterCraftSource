/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.loader;

import java.beans.ConstructorProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.labymod.addons.resourcepacks24.asm.ResourcepackMethods;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;

public class MinecraftRepositoryHandler {
    private PackRepositoryLoader loader;

    public void unloadUnselectedPacks() {
        Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().forEach(new Consumer<ResourcePackRepository.Entry>(){

            @Override
            public void accept(ResourcePackRepository.Entry entry) {
                boolean isSelected = false;
                for (LocalPackElement packEntry : MinecraftRepositoryHandler.this.loader.getRepository().getSelected()) {
                    if (!packEntry.getPackMeta().getFileName().equals(entry.getResourcePackName())) continue;
                    isSelected = true;
                }
                if (!isSelected) {
                    entry.closeResourcePack();
                }
            }
        });
    }

    public void loadSelectedPacks() {
        List<String> mcSettingList = Minecraft.getMinecraft().gameSettings.resourcePacks;
        block0: for (String mcPackName : mcSettingList) {
            for (LocalPackElement element : this.loader.getRepository().getRepository()) {
                File file = element.getPackMeta().file;
                String path = element.getPackMeta().getFileName();
                if (!this.loader.getDirectory().equals(file.getParentFile())) {
                    path = String.valueOf(file.getParentFile().getName()) + "/" + path;
                }
                if (!path.equals(mcPackName)) continue;
                this.loader.getRepository().getSelected().add(element);
                continue block0;
            }
        }
    }

    public void saveResourceList() {
        List<String> mcSettingList = Minecraft.getMinecraft().gameSettings.resourcePacks;
        mcSettingList.clear();
        for (LocalPackElement packEntry : this.loader.getRepository().getSelected()) {
            File file = packEntry.getPackMeta().file;
            if (this.loader.getDirectory().equals(file.getParentFile())) {
                mcSettingList.add(file.getName());
                continue;
            }
            mcSettingList.add(String.valueOf(file.getParentFile().getName()) + "/" + file.getName());
        }
        Minecraft.getMinecraft().gameSettings.resourcePacks = new ArrayList<String>(mcSettingList);
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    public void refreshResources() {
        try {
            ResourcePackRepository mcRepository = Minecraft.getMinecraft().getResourcePackRepository();
            mcRepository.updateRepositoryEntriesAll();
            ArrayList<ResourcePackRepository.Entry> entries = new ArrayList<ResourcePackRepository.Entry>();
            for (LocalPackElement element : this.loader.getRepository().getSelected()) {
                ResourcePackRepository.Entry target;
                if (!this.loader.isSelected(element) || (target = this.getEntryByPackElement(mcRepository, element)) == null) continue;
                entries.add(target);
            }
            mcRepository.setRepositories(entries);
            Minecraft.getMinecraft().refreshResources();
        }
        catch (Throwable error) {
            error.printStackTrace();
        }
    }

    public ResourcePackRepository.Entry getEntryByPackElement(ResourcePackRepository mcRepository, LocalPackElement element) {
        for (ResourcePackRepository.Entry entry : mcRepository.getRepositoryEntriesAll()) {
            String pathName = ResourcepackMethods.modifyName(element.getPackMeta().getFileName(), element.getPackMeta().file);
            if (!pathName.equals(entry.getResourcePackName())) continue;
            return entry;
        }
        return null;
    }

    @ConstructorProperties(value={"loader"})
    public MinecraftRepositoryHandler(PackRepositoryLoader loader) {
        this.loader = loader;
    }
}

