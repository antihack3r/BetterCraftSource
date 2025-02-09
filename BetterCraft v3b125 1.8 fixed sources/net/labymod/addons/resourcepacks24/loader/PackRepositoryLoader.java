/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.loader;

import com.google.common.io.Files;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.labymod.addons.resourcepacks24.gui.elements.FolderElement;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.loader.MinecraftRepositoryHandler;
import net.labymod.addons.resourcepacks24.loader.model.MCPack;
import net.labymod.addons.resourcepacks24.loader.model.PackMeta;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.apache.commons.io.IOUtils;

public class PackRepositoryLoader {
    private static final Gson GSON = new Gson();
    public static final LocalPackElement DEFAULT_MINECRAFT = new LocalPackElement(new PackMeta(null, new MCPack(1, "The default look of Minecraft"), "Default"));
    private File directory;
    private Repository repository = new Repository();
    private MinecraftRepositoryHandler minecraftRepositoryHandler = new MinecraftRepositoryHandler(this);

    public PackRepositoryLoader(File directory) {
        this.directory = directory;
    }

    public void loadAsync() {
        this.loadAsync(null);
    }

    public void loadAsync(final Consumer<Repository> consumer) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                PackRepositoryLoader.this.load(PackRepositoryLoader.this.directory, false);
                PackRepositoryLoader.this.minecraftRepositoryHandler.loadSelectedPacks();
                if (consumer != null) {
                    consumer.accept(PackRepositoryLoader.this.repository);
                }
            }
        }).start();
    }

    private void load(File directory, boolean inSubFolder) {
        File[] list = directory.listFiles();
        if (list == null) {
            return;
        }
        if (inSubFolder) {
            this.repository.getSubFolders().add(new FolderElement(directory));
        } else {
            this.repository.clear();
        }
        File[] fileArray = list;
        int n2 = list.length;
        int n3 = 0;
        while (n3 < n2) {
            File file = fileArray[n3];
            String fileName = file.getName();
            try {
                ZipFile zipFile;
                ZipEntry mcMetaEntry;
                if (file.isDirectory()) {
                    File fileMcMeta = new File(file, "pack.mcmeta");
                    File filePng = new File(file, "pack.png");
                    if (fileMcMeta.exists()) {
                        this.loadPack(file, fileName, new FileInputStream(fileMcMeta), filePng.exists() ? new FileInputStream(filePng) : null);
                    } else if (!inSubFolder) {
                        this.load(file, true);
                    }
                } else if (fileName.endsWith(".zip") && (mcMetaEntry = (zipFile = new ZipFile(file)).getEntry("pack.mcmeta")) != null) {
                    ZipEntry pngEntry = zipFile.getEntry("pack.png");
                    String name = fileName.substring(0, fileName.lastIndexOf(46));
                    this.loadPack(file, name, zipFile.getInputStream(mcMetaEntry), pngEntry == null ? null : zipFile.getInputStream(pngEntry));
                    zipFile.close();
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }
            ++n3;
        }
    }

    private void loadPack(File file, String name, InputStream mcMetaInput, InputStream pngInput) throws Exception {
        PackMeta modelPackMeta;
        final BufferedImage bufferedImage = pngInput == null ? null : ImageIO.read(pngInput);
        String json = IOUtils.toString(mcMetaInput, StandardCharsets.UTF_8);
        try {
            modelPackMeta = GSON.fromJson(json, PackMeta.class);
        }
        catch (Exception e2) {
            modelPackMeta = new PackMeta(new MCPack(1, String.valueOf(ModColor.cl('c')) + e2.getMessage()));
        }
        final PackMeta packMeta = modelPackMeta;
        packMeta.displayName = name;
        packMeta.file = file;
        if (bufferedImage != null) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    packMeta.icon = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("texturepackicon", new DynamicTexture(bufferedImage));
                }
            });
        }
        LocalPackElement packEntry = new LocalPackElement(packMeta);
        this.repository.getRepository().add(packEntry);
        if (this.directory.equals(file.getParentFile())) {
            this.repository.getRootPacks().add(packEntry);
        }
    }

    public boolean selectPack(LocalPackElement packEntry) {
        return !this.isSelected(packEntry) && this.repository.getSelected().add(packEntry);
    }

    public boolean unselectPack(LocalPackElement packEntry) {
        return this.repository.getSelected().remove(packEntry);
    }

    public boolean isSelected(LocalPackElement packEntry) {
        return this.repository.getSelected().contains(packEntry);
    }

    public void swapSelection(LocalPackElement first, LocalPackElement second) {
        boolean secondSelected;
        if (first == second) {
            return;
        }
        boolean firstSelected = this.isSelected(first);
        if (firstSelected == (secondSelected = this.isSelected(second))) {
            if (firstSelected) {
                this.unselectPack(first);
                this.unselectPack(second);
            } else {
                this.selectPack(first);
                this.selectPack(second);
            }
            return;
        }
        LocalPackElement currentSelected = firstSelected ? first : second;
        LocalPackElement currentUnselected = firstSelected ? second : first;
        int index = this.getSelectedIndexOf(currentSelected);
        this.repository.getSelected().add(index, currentUnselected);
        this.unselectPack(currentSelected);
    }

    public void swap(LocalPackElement packEntry, int offset) {
        int target = this.getSelectedIndexOf(packEntry);
        Collections.swap(this.repository.getSelected(), target, target + offset);
    }

    public void swap(LocalPackElement packEntry, LocalPackElement targetEntry) {
        if (packEntry != targetEntry) {
            Collections.swap(this.repository.getSelected(), this.getSelectedIndexOf(packEntry), this.getSelectedIndexOf(targetEntry));
        }
    }

    private int getSelectedIndexOf(LocalPackElement packEntry) {
        int i2 = 0;
        while (i2 < this.repository.getSelected().size()) {
            if (this.repository.getSelected().get(i2).equals(packEntry)) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    public boolean movePack(LocalPackElement packEntry, FolderElement folder) {
        File newPackFile;
        if (folder == null ? packEntry.getPackMeta().file.getParentFile().equals(this.directory) : folder.contains(packEntry)) {
            return true;
        }
        this.minecraftRepositoryHandler.unloadUnselectedPacks();
        File packFile = packEntry.getPackMeta().file;
        File file = newPackFile = folder == null ? new File(this.directory, packFile.getName()) : new File(folder.getDirectory(), packFile.getName());
        if (!packFile.isDirectory()) {
            try {
                Files.move(packFile, newPackFile);
                packEntry.getPackMeta().file = newPackFile;
                if (folder == null) {
                    this.repository.rootPacks.add(packEntry);
                } else {
                    this.repository.rootPacks.remove(packEntry);
                }
                return true;
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public File getDirectory() {
        return this.directory;
    }

    public Repository getRepository() {
        return this.repository;
    }

    public MinecraftRepositoryHandler getMinecraftRepositoryHandler() {
        return this.minecraftRepositoryHandler;
    }

    public static class Repository {
        private List<LocalPackElement> repository = new ArrayList<LocalPackElement>();
        private List<FolderElement> subFolders = new ArrayList<FolderElement>();
        private List<LocalPackElement> rootPacks = new ArrayList<LocalPackElement>();
        private List<LocalPackElement> selected = new ArrayList<LocalPackElement>();

        public void clear() {
            this.repository.clear();
            this.subFolders.clear();
            this.rootPacks.clear();
            this.selected.clear();
        }

        public Repository clone() {
            return new Repository(new ArrayList<LocalPackElement>(this.repository), new ArrayList<FolderElement>(this.subFolders), new ArrayList<LocalPackElement>(this.rootPacks), new ArrayList<LocalPackElement>(this.selected));
        }

        public List<LocalPackElement> getRepository() {
            return this.repository;
        }

        public List<FolderElement> getSubFolders() {
            return this.subFolders;
        }

        public List<LocalPackElement> getRootPacks() {
            return this.rootPacks;
        }

        public List<LocalPackElement> getSelected() {
            return this.selected;
        }

        @ConstructorProperties(value={"repository", "subFolders", "rootPacks", "selected"})
        public Repository(List<LocalPackElement> repository, List<FolderElement> subFolders, List<LocalPackElement> rootPacks, List<LocalPackElement> selected) {
            this.repository = repository;
            this.subFolders = subFolders;
            this.rootPacks = rootPacks;
            this.selected = selected;
        }

        public Repository() {
        }
    }
}

