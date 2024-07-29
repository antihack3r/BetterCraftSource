/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;
import net.labymod.core.BlockPosition;
import net.labymod.core.ChatComponent;
import net.labymod.core.Vec3i;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.main.LabyMod;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;

public interface MinecraftAdapter {
    public void init(LabyMod var1);

    public EntityPlayerSP getPlayer();

    public WorldClient getWorld();

    public ChatComponent getChatComponent(Object var1);

    public String getBiome();

    public void displayMessageInChat(String var1);

    public void displayMessageInChatURL(String var1, String var2);

    public void displayMessageInChatCustomAction(String var1, int var2, String var3);

    public boolean isBlocking(EntityPlayer var1);

    public void playSound(ResourceLocation var1, float var2);

    public RenderManager getCustomRenderManager();

    public GuiIngame getCustomIngameGui();

    public GuiScreen getCustomMainMenu();

    public int getItemInUseMaxCount();

    public boolean isHandActive();

    public ItemStack getItemInUse();

    public void updateOnlineServers(ServerSelectionList var1, ServerList var2);

    public NetHandlerPlayClient getConnection();

    public Potion getInvisiblityPotion();

    public Entity getRidingEntity(AbstractClientPlayer var1);

    public ItemStack getItem(InventoryPlayer var1, int var2);

    public int getAnimationsToGo(ItemStack var1);

    public BlockPosition getPosition(Object var1);

    public String getClickEventValue(int var1, int var2);

    public Item getItemBow();

    public List<?> splitText(Object var1, int var2, FontRenderer var3, boolean var4, boolean var5);

    public ItemStack getMainHandItem();

    public ItemStack getOffHandItem();

    public int getStackSize(ItemStack var1);

    public String getPotionDurationString(PotionEffect var1);

    public Potion getPotion(PotionEffect var1);

    public Item getTargetBlockItem();

    public Material getLavaMaterial();

    public boolean isRightArmPoseBow(ModelCosmetics var1);

    public boolean isAimedBow(ModelCosmetics var1);

    public PotionEffect getPotionEffect(Potion var1, int var2, int var3);

    public boolean isSelected(ServerSelectionList var1, int var2);

    public int getSelectedServerInSelectionList(ServerSelectionList var1);

    public void sendPluginMessage(String var1, PacketBuffer var2);

    public void updateServerList(ServerSelectionList var1, ServerList var2);

    public void writeUniqueIdToBuffer(PacketBuffer var1, UUID var2);

    public String readStringFromBuffer(PacketBuffer var1);

    public ScoreObjective getDummyScoreObjective();

    public Object getTaggedChatComponent(Object var1);

    public FontRenderer getFontRenderer();

    public void setButtonXPosition(GuiButton var1, int var2);

    public void setButtonYPosition(GuiButton var1, int var2);

    public void setTextFieldXPosition(GuiTextField var1, int var2);

    public void setTextFieldYPosition(GuiTextField var1, int var2);

    public int getXPosition(GuiButton var1);

    public int getYPosition(GuiButton var1);

    public int getXPosition(GuiTextField var1);

    public int getYPosition(GuiTextField var1);

    public int getTeamColorIndex(ScorePlayerTeam var1);

    public void drawButton(GuiButton var1, int var2, int var3);

    public TileEntitySignRenderer getCustomSignRenderer();

    public String vectoString(Vec3i var1);

    public PacketBuf createPacketBuf(ByteBuf var1);

    public boolean hasInGameFocus();

    public boolean isCurrentScreenNull();

    public boolean isMinecraftChatOpen();

    public long getLastAttackTime();

    public EntityLivingBase getLastAttackedEntity();

    public void handleBlockBuild();

    public double calculateEyeMovement(Entity var1, Entity var2);

    public void setSecondLayerBit(AbstractClientPlayer var1, int var2, byte var3);

    public void sendClientSettings(String var1, int var2, EntityPlayer.EnumChatVisibility var3, boolean var4, int var5);

    public UUID isEmotePacket(Object var1);

    public boolean isJumpPredicted();

    public int getGameMode(UUID var1);

    public Entity getEntityMouseOver();

    public boolean isElytraFlying(Entity var1);

    public boolean isWearingElytra(Entity var1);

    public Entity getRenderViewEntity();

    public boolean isAchievementGui(GuiScreen var1);

    public String getBossBarMessage();

    public void setUseLeftHand(boolean var1);

    public boolean isUsingLeftHand();

    public PropertyMap getPropertyMap();
}

