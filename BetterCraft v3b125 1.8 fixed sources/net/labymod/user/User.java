/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.labymod.main.LabyMod;
import net.labymod.user.UserManager;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.group.GroupManager;
import net.labymod.user.group.LabyGroup;
import net.minecraft.client.entity.AbstractClientPlayer;

public class User {
    private UUID uuid;
    private Map<Integer, CosmeticData> cosmetics = new HashMap<Integer, CosmeticData>();
    private List<Short> emotes = new ArrayList<Short>();
    private List<Short> stickerPacks = new ArrayList<Short>();
    protected short playingSticker = (short)-1;
    protected long stickerStartedPlaying;
    private LabyGroup group = GroupManager.DEFAULT_GROUP;
    private boolean dailyEmoteFlat;
    private String subTitle;
    private double subTitleSize;
    private boolean mojangCapeModified = false;
    private boolean mojangCapeVisible = true;
    private long nextPriorityCheck = -1L;
    private final UserTextureContainer cloakContainer;
    private final UserTextureContainer bandanaContainer;
    private final UserTextureContainer shoesContainer;
    private final UserTextureContainer kawaiiMaskContainer;
    private final UserTextureContainer coverMaskContainer;
    private final UserTextureContainer watchContainer;
    private final UserTextureContainer angelWingsContainer;
    private float maxNameTagHeight;
    private UserManager userManager;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.cloakContainer = new UserTextureContainer("../capes", this.uuid);
        this.bandanaContainer = new UserTextureContainer("bandanas", this.uuid);
        this.shoesContainer = new UserTextureContainer("shoes");
        this.kawaiiMaskContainer = new UserTextureContainer("kawaiimasks");
        this.coverMaskContainer = new UserTextureContainer("covermasks");
        this.watchContainer = new UserTextureContainer("watches");
        this.angelWingsContainer = new UserTextureContainer("angelwings");
        this.userManager = LabyMod.getInstance().getUserManager();
    }

    public boolean hasCosmeticById(int id2) {
        return this.cosmetics.containsKey(id2);
    }

    public void resetMaxNameTagHeight() {
        this.maxNameTagHeight = 0.0f;
    }

    public void applyNameTagHeight(float nameTagHeight) {
        if (nameTagHeight > this.maxNameTagHeight) {
            this.maxNameTagHeight = nameTagHeight;
        }
    }

    public void unloadCosmeticTextures() {
        this.cloakContainer.unload();
        this.bandanaContainer.unload();
        this.shoesContainer.unload();
        this.kawaiiMaskContainer.unload();
        this.coverMaskContainer.unload();
        this.watchContainer.unload();
        this.angelWingsContainer.unload();
    }

    public boolean canRenderMojangCape(AbstractClientPlayer entitylivingbaseIn) {
        if (this.nextPriorityCheck > System.currentTimeMillis()) {
            return this.mojangCapeVisible;
        }
        this.nextPriorityCheck = System.currentTimeMillis() + 500L;
        this.mojangCapeVisible = this.userManager.getCosmeticImageManager().getCloakImageHandler().canRenderMojangCape(this, entitylivingbaseIn);
        return this.mojangCapeVisible;
    }

    public boolean isFamiliar() {
        return this.userManager.getFamiliarManager().isFamiliar(this.uuid);
    }

    public boolean isStickerVisible() {
        if (this.playingSticker == -1) {
            return false;
        }
        long timePassed = System.currentTimeMillis() - this.stickerStartedPlaying;
        return timePassed < 4000L;
    }

    public void setGroup(LabyGroup group) {
        this.group = group == null ? GroupManager.DEFAULT_GROUP : group;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Map<Integer, CosmeticData> getCosmetics() {
        return this.cosmetics;
    }

    public List<Short> getEmotes() {
        return this.emotes;
    }

    public List<Short> getStickerPacks() {
        return this.stickerPacks;
    }

    public short getPlayingSticker() {
        return this.playingSticker;
    }

    public long getStickerStartedPlaying() {
        return this.stickerStartedPlaying;
    }

    public LabyGroup getGroup() {
        return this.group;
    }

    public boolean isDailyEmoteFlat() {
        return this.dailyEmoteFlat;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public double getSubTitleSize() {
        return this.subTitleSize;
    }

    public boolean isMojangCapeModified() {
        return this.mojangCapeModified;
    }

    public boolean isMojangCapeVisible() {
        return this.mojangCapeVisible;
    }

    public long getNextPriorityCheck() {
        return this.nextPriorityCheck;
    }

    public UserTextureContainer getCloakContainer() {
        return this.cloakContainer;
    }

    public UserTextureContainer getBandanaContainer() {
        return this.bandanaContainer;
    }

    public UserTextureContainer getShoesContainer() {
        return this.shoesContainer;
    }

    public UserTextureContainer getKawaiiMaskContainer() {
        return this.kawaiiMaskContainer;
    }

    public UserTextureContainer getCoverMaskContainer() {
        return this.coverMaskContainer;
    }

    public UserTextureContainer getWatchContainer() {
        return this.watchContainer;
    }

    public UserTextureContainer getAngelWingsContainer() {
        return this.angelWingsContainer;
    }

    public float getMaxNameTagHeight() {
        return this.maxNameTagHeight;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public void setCosmetics(Map<Integer, CosmeticData> cosmetics) {
        this.cosmetics = cosmetics;
    }

    public void setEmotes(List<Short> emotes) {
        this.emotes = emotes;
    }

    public void setStickerPacks(List<Short> stickerPacks) {
        this.stickerPacks = stickerPacks;
    }

    public void setPlayingSticker(short playingSticker) {
        this.playingSticker = playingSticker;
    }

    public void setStickerStartedPlaying(long stickerStartedPlaying) {
        this.stickerStartedPlaying = stickerStartedPlaying;
    }

    public void setDailyEmoteFlat(boolean dailyEmoteFlat) {
        this.dailyEmoteFlat = dailyEmoteFlat;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setSubTitleSize(double subTitleSize) {
        this.subTitleSize = subTitleSize;
    }

    public void setMojangCapeModified(boolean mojangCapeModified) {
        this.mojangCapeModified = mojangCapeModified;
    }
}

