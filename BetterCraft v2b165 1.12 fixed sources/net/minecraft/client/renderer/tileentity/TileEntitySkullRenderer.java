// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.tileentity.TileEntity;
import java.util.UUID;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.renderer.GlStateManager;
import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.model.ModelDragonHead;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntitySkull;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull>
{
    private static final ResourceLocation SKELETON_TEXTURES;
    private static final ResourceLocation WITHER_SKELETON_TEXTURES;
    private static final ResourceLocation ZOMBIE_TEXTURES;
    private static final ResourceLocation CREEPER_TEXTURES;
    private static final ResourceLocation DRAGON_TEXTURES;
    private final ModelDragonHead dragonHead;
    public static TileEntitySkullRenderer instance;
    private final ModelSkeletonHead skeletonHead;
    private final ModelSkeletonHead humanoidHead;
    
    static {
        SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
        WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
        ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
        CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
        DRAGON_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    }
    
    public TileEntitySkullRenderer() {
        this.dragonHead = new ModelDragonHead(0.0f);
        this.skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
        this.humanoidHead = new ModelHumanoidHead();
    }
    
    @Override
    public void func_192841_a(final TileEntitySkull p_192841_1_, final double p_192841_2_, final double p_192841_4_, final double p_192841_6_, final float p_192841_8_, final int p_192841_9_, final float p_192841_10_) {
        final EnumFacing enumfacing = EnumFacing.getFront(p_192841_1_.getBlockMetadata() & 0x7);
        final float f = p_192841_1_.getAnimationProgress(p_192841_8_);
        this.renderSkull((float)p_192841_2_, (float)p_192841_4_, (float)p_192841_6_, enumfacing, p_192841_1_.getSkullRotation() * 360 / 16.0f, p_192841_1_.getSkullType(), p_192841_1_.getPlayerProfile(), p_192841_9_, f);
    }
    
    @Override
    public void setRendererDispatcher(final TileEntityRendererDispatcher rendererDispatcherIn) {
        super.setRendererDispatcher(rendererDispatcherIn);
        TileEntitySkullRenderer.instance = this;
    }
    
    public void renderSkull(final float x, final float y, final float z, final EnumFacing facing, float p_188190_5_, final int skullType, @Nullable final GameProfile profile, final int destroyStage, final float animateTicks) {
        ModelBase modelbase = this.skeletonHead;
        if (destroyStage >= 0) {
            this.bindTexture(TileEntitySkullRenderer.DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 2.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            switch (skullType) {
                default: {
                    this.bindTexture(TileEntitySkullRenderer.SKELETON_TEXTURES);
                    break;
                }
                case 1: {
                    this.bindTexture(TileEntitySkullRenderer.WITHER_SKELETON_TEXTURES);
                    break;
                }
                case 2: {
                    this.bindTexture(TileEntitySkullRenderer.ZOMBIE_TEXTURES);
                    modelbase = this.humanoidHead;
                    break;
                }
                case 3: {
                    modelbase = this.humanoidHead;
                    ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
                    if (profile != null) {
                        final Minecraft minecraft = Minecraft.getMinecraft();
                        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
                        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                        }
                        else {
                            final UUID uuid = EntityPlayer.getUUID(profile);
                            resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                        }
                    }
                    this.bindTexture(resourcelocation);
                    break;
                }
                case 4: {
                    this.bindTexture(TileEntitySkullRenderer.CREEPER_TEXTURES);
                    break;
                }
                case 5: {
                    this.bindTexture(TileEntitySkullRenderer.DRAGON_TEXTURES);
                    modelbase = this.dragonHead;
                    break;
                }
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if (facing == EnumFacing.UP) {
            GlStateManager.translate(x + 0.5f, y, z + 0.5f);
        }
        else {
            switch (facing) {
                case NORTH: {
                    GlStateManager.translate(x + 0.5f, y + 0.25f, z + 0.74f);
                    break;
                }
                case SOUTH: {
                    GlStateManager.translate(x + 0.5f, y + 0.25f, z + 0.26f);
                    p_188190_5_ = 180.0f;
                    break;
                }
                case WEST: {
                    GlStateManager.translate(x + 0.74f, y + 0.25f, z + 0.5f);
                    p_188190_5_ = 270.0f;
                    break;
                }
                default: {
                    GlStateManager.translate(x + 0.26f, y + 0.25f, z + 0.5f);
                    p_188190_5_ = 90.0f;
                    break;
                }
            }
        }
        final float f = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlpha();
        if (skullType == 3) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
        modelbase.render(null, animateTicks, 0.0f, 0.0f, p_188190_5_, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
