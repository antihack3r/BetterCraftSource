// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.resources;

import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import java.io.Closeable;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.texture.TextureManager;
import java.util.Comparator;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.util.Locale;
import java.io.InputStream;
import java.io.FileInputStream;
import com.google.common.util.concurrent.FutureCallback;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.HttpUtil;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Futures;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.resources.data.PackMetadataSection;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.client.settings.GameSettings;
import org.apache.logging.log4j.LogManager;
import java.util.List;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.resources.data.MetadataSerializer;
import java.io.File;
import net.minecraft.util.ResourceLocation;
import java.util.regex.Pattern;
import java.io.FileFilter;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository
{
    private static final Logger LOGGER;
    private static final FileFilter RESOURCE_PACK_FILTER;
    private static final Pattern SHA1;
    private static final ResourceLocation field_191400_f;
    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File dirServerResourcepacks;
    public final MetadataSerializer rprMetadataSerializer;
    private IResourcePack resourcePackInstance;
    private final ReentrantLock lock;
    private ListenableFuture<Object> downloadingPacks;
    private List<Entry> repositoryEntriesAll;
    public final List<Entry> repositoryEntries;
    
    static {
        LOGGER = LogManager.getLogger();
        RESOURCE_PACK_FILTER = new FileFilter() {
            @Override
            public boolean accept(final File p_accept_1_) {
                final boolean flag = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
                final boolean flag2 = p_accept_1_.isDirectory() && new File(p_accept_1_, "pack.mcmeta").isFile();
                return flag || flag2;
            }
        };
        SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
        field_191400_f = new ResourceLocation("textures/misc/unknown_pack.png");
    }
    
    public ResourcePackRepository(final File dirResourcepacksIn, final File dirServerResourcepacksIn, final IResourcePack rprDefaultResourcePackIn, final MetadataSerializer rprMetadataSerializerIn, final GameSettings settings) {
        this.lock = new ReentrantLock();
        this.repositoryEntriesAll = (List<Entry>)Lists.newArrayList();
        this.repositoryEntries = (List<Entry>)Lists.newArrayList();
        this.dirResourcepacks = dirResourcepacksIn;
        this.dirServerResourcepacks = dirServerResourcepacksIn;
        this.rprDefaultResourcePack = rprDefaultResourcePackIn;
        this.rprMetadataSerializer = rprMetadataSerializerIn;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        final Iterator<String> iterator = settings.resourcePacks.iterator();
        while (iterator.hasNext()) {
            final String s = iterator.next();
            for (final Entry resourcepackrepository$entry : this.repositoryEntriesAll) {
                if (resourcepackrepository$entry.getResourcePackName().equals(s)) {
                    if (resourcepackrepository$entry.getPackFormat() == 3 || settings.incompatibleResourcePacks.contains(resourcepackrepository$entry.getResourcePackName())) {
                        this.repositoryEntries.add(resourcepackrepository$entry);
                        break;
                    }
                    iterator.remove();
                    ResourcePackRepository.LOGGER.warn("Removed selected resource pack {} because it's no longer compatible", resourcepackrepository$entry.getResourcePackName());
                }
            }
        }
    }
    
    public static Map<String, String> getDownloadHeaders() {
        final HashMap<Object, Object> hashMap;
        final Map<String, String> map = (Map<String, String>)(hashMap = Maps.newHashMap());
        final String s = "X-Minecraft-Username";
        Minecraft.getMinecraft();
        hashMap.put(s, Minecraft.getSession().getUsername());
        final Map<String, String> map2 = map;
        final String s2 = "X-Minecraft-UUID";
        Minecraft.getMinecraft();
        map2.put(s2, Minecraft.getSession().getPlayerID());
        map.put("X-Minecraft-Version", "1.12.2");
        return map;
    }
    
    private void fixDirResourcepacks() {
        if (this.dirResourcepacks.exists()) {
            if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs())) {
                ResourcePackRepository.LOGGER.warn("Unable to recreate resourcepack folder, it exists but is not a directory: {}", this.dirResourcepacks);
            }
        }
        else if (!this.dirResourcepacks.mkdirs()) {
            ResourcePackRepository.LOGGER.warn("Unable to create resourcepack folder: {}", this.dirResourcepacks);
        }
    }
    
    private List<File> getResourcePackFiles() {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(ResourcePackRepository.RESOURCE_PACK_FILTER)) : Collections.emptyList();
    }
    
    private IResourcePack func_191399_b(final File p_191399_1_) {
        IResourcePack iresourcepack;
        if (p_191399_1_.isDirectory()) {
            iresourcepack = new FolderResourcePack(p_191399_1_);
        }
        else {
            iresourcepack = new FileResourcePack(p_191399_1_);
        }
        try {
            final PackMetadataSection packmetadatasection = iresourcepack.getPackMetadata(this.rprMetadataSerializer, "pack");
            if (packmetadatasection != null && packmetadatasection.getPackFormat() == 2) {
                return new LegacyV2Adapter(iresourcepack);
            }
        }
        catch (final Exception ex) {}
        return iresourcepack;
    }
    
    public void updateRepositoryEntriesAll() {
        final List<Entry> list = (List<Entry>)Lists.newArrayList();
        for (final File file1 : this.getResourcePackFiles()) {
            final Entry resourcepackrepository$entry = new Entry(file1, (Entry)null);
            if (this.repositoryEntriesAll.contains(resourcepackrepository$entry)) {
                final int i = this.repositoryEntriesAll.indexOf(resourcepackrepository$entry);
                if (i <= -1 || i >= this.repositoryEntriesAll.size()) {
                    continue;
                }
                list.add(this.repositoryEntriesAll.get(i));
            }
            else {
                try {
                    resourcepackrepository$entry.updateResourcePack();
                    list.add(resourcepackrepository$entry);
                }
                catch (final Exception var61) {
                    list.remove(resourcepackrepository$entry);
                }
            }
        }
        this.repositoryEntriesAll.removeAll(list);
        for (final Entry resourcepackrepository$entry2 : this.repositoryEntriesAll) {
            resourcepackrepository$entry2.closeResourcePack();
        }
        this.repositoryEntriesAll = list;
    }
    
    @Nullable
    public Entry getResourcePackEntry() {
        if (this.resourcePackInstance != null) {
            final Entry resourcepackrepository$entry = new Entry(this.resourcePackInstance, (Entry)null);
            try {
                resourcepackrepository$entry.updateResourcePack();
                return resourcepackrepository$entry;
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    public List<Entry> getRepositoryEntriesAll() {
        return (List<Entry>)ImmutableList.copyOf((Collection<?>)this.repositoryEntriesAll);
    }
    
    public List<Entry> getRepositoryEntries() {
        return (List<Entry>)ImmutableList.copyOf((Collection<?>)this.repositoryEntries);
    }
    
    public void setRepositories(final List<Entry> repositories) {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(repositories);
    }
    
    public File getDirResourcepacks() {
        return this.dirResourcepacks;
    }
    
    public ListenableFuture<Object> downloadResourcePack(final String url, final String hash) {
        final String s = DigestUtils.sha1Hex(url);
        final String s2 = ResourcePackRepository.SHA1.matcher(hash).matches() ? hash : "";
        final File file1 = new File(this.dirServerResourcepacks, s);
        this.lock.lock();
        try {
            this.clearResourcePack();
            if (file1.exists()) {
                if (this.checkHash(s2, file1)) {
                    final ListenableFuture listenablefuture3;
                    final ListenableFuture listenablefuture2 = listenablefuture3 = this.setResourcePackInstance(file1);
                    return listenablefuture3;
                }
                ResourcePackRepository.LOGGER.warn("Deleting file {}", file1);
                FileUtils.deleteQuietly(file1);
            }
            this.deleteOldServerResourcesPacks();
            final GuiScreenWorking guiscreenworking = new GuiScreenWorking();
            final Map<String, String> map = getDownloadHeaders();
            final Minecraft minecraft = Minecraft.getMinecraft();
            Futures.getUnchecked(minecraft.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    minecraft.displayGuiScreen(guiscreenworking);
                }
            }));
            final SettableFuture<Object> settablefuture = SettableFuture.create();
            Futures.addCallback(this.downloadingPacks = HttpUtil.downloadResourcePack(file1, url, map, 52428800, guiscreenworking, minecraft.getProxy()), new FutureCallback<Object>() {
                @Override
                public void onSuccess(@Nullable final Object p_onSuccess_1_) {
                    if (ResourcePackRepository.this.checkHash(s2, file1)) {
                        ResourcePackRepository.this.setResourcePackInstance(file1);
                        settablefuture.set(null);
                    }
                    else {
                        ResourcePackRepository.LOGGER.warn("Deleting file {}", file1);
                        FileUtils.deleteQuietly(file1);
                    }
                }
                
                @Override
                public void onFailure(final Throwable p_onFailure_1_) {
                    FileUtils.deleteQuietly(file1);
                    settablefuture.setException(p_onFailure_1_);
                }
            });
            final ListenableFuture listenablefuture5;
            final ListenableFuture listenablefuture4 = listenablefuture5 = this.downloadingPacks;
            return listenablefuture5;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private boolean checkHash(final String p_190113_1_, final File p_190113_2_) {
        try {
            final String s = DigestUtils.sha1Hex(new FileInputStream(p_190113_2_));
            if (p_190113_1_.isEmpty()) {
                ResourcePackRepository.LOGGER.info("Found file {} without verification hash", p_190113_2_);
                return true;
            }
            if (s.toLowerCase(Locale.ROOT).equals(p_190113_1_.toLowerCase(Locale.ROOT))) {
                ResourcePackRepository.LOGGER.info("Found file {} matching requested hash {}", p_190113_2_, p_190113_1_);
                return true;
            }
            ResourcePackRepository.LOGGER.warn("File {} had wrong hash (expected {}, found {}).", p_190113_2_, p_190113_1_, s);
        }
        catch (final IOException ioexception1) {
            ResourcePackRepository.LOGGER.warn("File {} couldn't be hashed.", p_190113_2_, ioexception1);
        }
        return false;
    }
    
    private boolean validatePack(final File p_190112_1_) {
        final Entry resourcepackrepository$entry = new Entry(p_190112_1_, (Entry)null);
        try {
            resourcepackrepository$entry.updateResourcePack();
            return true;
        }
        catch (final Exception exception) {
            ResourcePackRepository.LOGGER.warn("Server resourcepack is invalid, ignoring it", exception);
            return false;
        }
    }
    
    private void deleteOldServerResourcesPacks() {
        try {
            final List<File> list = (List<File>)Lists.newArrayList((Iterable<?>)FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, null));
            Collections.sort(list, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;
            for (final File file1 : list) {
                if (i++ >= 10) {
                    ResourcePackRepository.LOGGER.info("Deleting old server resource pack {}", file1.getName());
                    FileUtils.deleteQuietly(file1);
                }
            }
        }
        catch (final IllegalArgumentException illegalargumentexception1) {
            ResourcePackRepository.LOGGER.error("Error while deleting old server resource pack : {}", illegalargumentexception1.getMessage());
        }
    }
    
    public ListenableFuture<Object> setResourcePackInstance(final File resourceFile) {
        if (!this.validatePack(resourceFile)) {
            return Futures.immediateFailedFuture(new RuntimeException("Invalid resourcepack"));
        }
        this.resourcePackInstance = new FileResourcePack(resourceFile);
        return Minecraft.getMinecraft().scheduleResourcesRefresh();
    }
    
    @Nullable
    public IResourcePack getResourcePackInstance() {
        return this.resourcePackInstance;
    }
    
    public void clearResourcePack() {
        this.lock.lock();
        try {
            if (this.downloadingPacks != null) {
                this.downloadingPacks.cancel(true);
            }
            this.downloadingPacks = null;
            if (this.resourcePackInstance != null) {
                this.resourcePackInstance = null;
                Minecraft.getMinecraft().scheduleResourcesRefresh();
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public class Entry
    {
        private final IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private ResourceLocation locationTexturePackIcon;
        
        private Entry(final ResourcePackRepository resourcePackRepository, final File resourcePackFileIn) {
            this(resourcePackRepository, resourcePackRepository.func_191399_b(resourcePackFileIn));
        }
        
        private Entry(final IResourcePack reResourcePackIn) {
            this.reResourcePack = reResourcePackIn;
        }
        
        public void updateResourcePack() throws IOException {
            this.rePackMetadataSection = this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");
            this.closeResourcePack();
        }
        
        public void bindTexturePackIcon(final TextureManager textureManagerIn) {
            BufferedImage bufferedimage = null;
            if (this.locationTexturePackIcon == null) {
                try {
                    bufferedimage = this.reResourcePack.getPackImage();
                }
                catch (final IOException ex) {}
                if (bufferedimage == null) {
                    try {
                        bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(ResourcePackRepository.field_191400_f).getInputStream());
                    }
                    catch (final IOException ioexception) {
                        throw new Error("Couldn't bind resource pack icon", ioexception);
                    }
                }
            }
            if (this.locationTexturePackIcon == null) {
                this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(bufferedimage));
            }
            textureManagerIn.bindTexture(this.locationTexturePackIcon);
        }
        
        public void closeResourcePack() {
            if (this.reResourcePack instanceof Closeable) {
                IOUtils.closeQuietly((Closeable)this.reResourcePack);
            }
        }
        
        public IResourcePack getResourcePack() {
            return this.reResourcePack;
        }
        
        public String getResourcePackName() {
            return this.reResourcePack.getPackName();
        }
        
        public String getTexturePackDescription() {
            return (this.rePackMetadataSection == null) ? (TextFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)") : this.rePackMetadataSection.getPackDescription().getFormattedText();
        }
        
        public int getPackFormat() {
            return (this.rePackMetadataSection == null) ? 0 : this.rePackMetadataSection.getPackFormat();
        }
        
        @Override
        public boolean equals(final Object p_equals_1_) {
            return this == p_equals_1_ || (p_equals_1_ instanceof Entry && this.toString().equals(p_equals_1_.toString()));
        }
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
        
        @Override
        public String toString() {
            return String.format("%s:%s", this.reResourcePack.getPackName(), (this.reResourcePack instanceof FolderResourcePack) ? "folder" : "zip");
        }
    }
}
