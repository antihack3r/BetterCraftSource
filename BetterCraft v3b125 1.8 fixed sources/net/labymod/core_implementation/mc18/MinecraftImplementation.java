/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import com.google.common.base.Objects;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.BlockPosition;
import net.labymod.core.ChatComponent;
import net.labymod.core.LabyModCore;
import net.labymod.core.MinecraftAdapter;
import net.labymod.core.Vec3i;
import net.labymod.core_implementation.mc18.gui.GuiIngameCustom;
import net.labymod.core_implementation.mc18.gui.ModGuiMainMenu;
import net.labymod.core_implementation.mc18.listener.ServerIncomingPacketListener;
import net.labymod.core_implementation.mc18.render.RenderManagerCustom;
import net.labymod.core_implementation.mc18.util.PacketBufOld;
import net.labymod.core_implementation.mc18.util.StringMessage;
import net.labymod.core_implementation.mc18.util.prediction.JumpPrediction;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.utils.manager.SignManager;
import net.labymod.utils.manager.TagManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MinecraftImplementation
implements MinecraftAdapter {
    @Override
    public void init(LabyMod labymod) {
        new ServerIncomingPacketListener(labymod).register();
    }

    @Override
    public EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public WorldClient getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public ChatComponent getChatComponent(Object chatComponent) {
        if (chatComponent == null) {
            return null;
        }
        return new ChatComponent(((IChatComponent)chatComponent).getUnformattedText(), ((IChatComponent)chatComponent).getFormattedText(), IChatComponent.Serializer.componentToJson((IChatComponent)chatComponent));
    }

    @Override
    public String getBiome() {
        return this.getPlayer() == null || this.getWorld() == null || this.getPlayer().getPosition() == null ? "?" : this.getWorld().getBiomeGenForCoords((BlockPos)this.getPlayer().getPosition()).biomeName;
    }

    @Override
    public void displayMessageInChat(String text) {
        IChatComponent[] iChatComponentArray = new StringMessage(text, false).getOutput();
        int n2 = iChatComponentArray.length;
        int n3 = 0;
        while (n3 < n2) {
            IChatComponent line = iChatComponentArray[n3];
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(line);
            ++n3;
        }
    }

    @Override
    public void displayMessageInChatURL(String text, String url) {
        this.displayMessageInChatCustomAction(text, ClickEvent.Action.OPEN_URL.ordinal(), url);
    }

    @Override
    public void displayMessageInChatCustomAction(String text, int actionId, String command) {
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.values()[actionId], command);
        IChatComponent[] iChatComponentArray = new StringMessage(text, false).getOutput();
        int n2 = iChatComponentArray.length;
        int n3 = 0;
        while (n3 < n2) {
            IChatComponent line = iChatComponentArray[n3];
            line.getChatStyle().setChatClickEvent(clickEvent);
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(line);
            ++n3;
        }
    }

    @Override
    public boolean isBlocking(EntityPlayer player) {
        return player.isUsingItem() && player.getItemInUse().getItem().getItemUseAction(player.getItemInUse()) == EnumAction.BLOCK;
    }

    @Override
    public void playSound(ResourceLocation resourceLocation, float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(resourceLocation, pitch));
    }

    @Override
    public RenderManager getCustomRenderManager() {
        return new RenderManagerCustom(Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public GuiIngame getCustomIngameGui() {
        return new GuiIngameCustom(Minecraft.getMinecraft());
    }

    @Override
    public GuiScreen getCustomMainMenu() {
        return new ModGuiMainMenu();
    }

    @Override
    public int getItemInUseMaxCount() {
        return this.getPlayer().getItemInUseDuration();
    }

    @Override
    public boolean isHandActive() {
        return this.getPlayer().isUsingItem();
    }

    @Override
    public ItemStack getItemInUse() {
        return this.getPlayer().getItemInUse();
    }

    @Override
    public void updateOnlineServers(ServerSelectionList serverSelectionList, ServerList serverList) {
        serverSelectionList.func_148195_a(serverList);
    }

    @Override
    public NetHandlerPlayClient getConnection() {
        return Minecraft.getMinecraft().getNetHandler();
    }

    @Override
    public Potion getInvisiblityPotion() {
        return Potion.invisibility;
    }

    @Override
    public Entity getRidingEntity(AbstractClientPlayer player) {
        return player.ridingEntity;
    }

    @Override
    public ItemStack getItem(InventoryPlayer inventoryPlayer, int index) {
        return inventoryPlayer.mainInventory[index];
    }

    @Override
    public int getAnimationsToGo(ItemStack itemStack) {
        return itemStack.animationsToGo;
    }

    @Override
    public BlockPosition getPosition(Object blockPosition) {
        BlockPos pos = (BlockPos)blockPosition;
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public String getClickEventValue(int x2, int y2) {
        IChatComponent iChatClickHover = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(x2, y2);
        if (iChatClickHover == null || iChatClickHover.getChatStyle() == null || iChatClickHover.getChatStyle().getChatClickEvent() == null) {
            return null;
        }
        String value = iChatClickHover.getChatStyle().getChatClickEvent().getValue();
        return value;
    }

    @Override
    public Item getItemBow() {
        return Items.bow;
    }

    @Override
    public List<?> splitText(Object textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean p_178908_3_, boolean forceTextColor) {
        return GuiUtilRenderComponents.splitText((IChatComponent)textComponent, maxTextLenght, fontRendererIn, p_178908_3_, forceTextColor);
    }

    @Override
    public ItemStack getMainHandItem() {
        return this.getPlayer() == null ? null : this.getPlayer().getHeldItem();
    }

    @Override
    public ItemStack getOffHandItem() {
        return null;
    }

    @Override
    public int getStackSize(ItemStack itemStack) {
        return itemStack.stackSize;
    }

    @Override
    public String getPotionDurationString(PotionEffect potion) {
        return Potion.getDurationString(potion);
    }

    @Override
    public Potion getPotion(PotionEffect potionEffect) {
        return Potion.potionTypes[potionEffect.getPotionID()];
    }

    @Override
    public Item getTargetBlockItem() {
        IBlockState blockstate;
        BlockPos current;
        WorldClient world = LabyModCore.getMinecraft().getWorld();
        if (world != null && Minecraft.getMinecraft().objectMouseOver != null && (current = Minecraft.getMinecraft().objectMouseOver.getBlockPos()) != null && (blockstate = world.getBlockState(current)) != null) {
            return blockstate.getBlock().getItem(world, current);
        }
        return null;
    }

    @Override
    public Material getLavaMaterial() {
        return Material.lava;
    }

    @Override
    public boolean isRightArmPoseBow(ModelCosmetics modelCosmetics) {
        return modelCosmetics.heldItemRight == 3;
    }

    @Override
    public boolean isAimedBow(ModelCosmetics modelCosmetics) {
        return modelCosmetics.aimedBow;
    }

    @Override
    public PotionEffect getPotionEffect(Potion potion, int duration, int amplifier) {
        return new PotionEffect(potion.getId(), duration, amplifier);
    }

    @Override
    public boolean isSelected(ServerSelectionList serverSelectionList, int index) {
        return serverSelectionList.func_148193_k() == index;
    }

    @Override
    public void sendPluginMessage(String channelName, PacketBuffer packetBuffer) {
        if (this.getConnection() != null) {
            if (!(channelName.equals("CCP") || channelName.equals("LMC") || channelName.equals("SHADOW"))) {
                Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[OUT] " + channelName);
            }
            this.getConnection().addToSendQueue(new C17PacketCustomPayload(channelName, packetBuffer));
        }
    }

    @Override
    public void updateServerList(ServerSelectionList serverSelectionList, ServerList serverList) {
        serverSelectionList.func_148195_a(serverList);
    }

    @Override
    public void writeUniqueIdToBuffer(PacketBuffer buffer, UUID uuid) {
        buffer.writeUuid(uuid);
    }

    @Override
    public String readStringFromBuffer(PacketBuffer buffer) {
        return buffer.readStringFromBuffer(buffer.readVarIntFromBuffer());
    }

    @Override
    public ScoreObjective getDummyScoreObjective() {
        ScoreObjective dummyScoreObjective = new ScoreObjective(new Scoreboard(), "Displayname", new IScoreObjectiveCriteria(){

            @Override
            public boolean isReadOnly() {
                return true;
            }

            @Override
            public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
                return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
            }

            @Override
            public String getName() {
                return "NAME";
            }

            @Override
            public int setScore(List<EntityPlayer> p_96635_1_) {
                return 1;
            }
        });
        dummyScoreObjective.getScoreboard().setObjectiveInDisplaySlot(1, dummyScoreObjective);
        int i2 = 1;
        while (i2 < 4) {
            dummyScoreObjective.getScoreboard().getValueFromObjective("Player " + i2, dummyScoreObjective);
            ++i2;
        }
        return dummyScoreObjective;
    }

    @Override
    public Object getTaggedChatComponent(Object textComponent) {
        IChatComponent chatComponent = (IChatComponent)textComponent;
        try {
            String json = IChatComponent.Serializer.componentToJson(chatComponent);
            String editedJson = TagManager.getTaggedMessage(json);
            if (editedJson != null) {
                chatComponent = IChatComponent.Serializer.jsonToComponent(editedJson);
                chatComponent.appendSibling(new ChatComponentText(" \u270e").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return chatComponent;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    @Override
    public void setButtonXPosition(GuiButton button, int x2) {
        button.xPosition = x2;
    }

    @Override
    public void setButtonYPosition(GuiButton button, int y2) {
        button.yPosition = y2;
    }

    @Override
    public void setTextFieldXPosition(GuiTextField field, int x2) {
        field.xPosition = x2;
    }

    @Override
    public void setTextFieldYPosition(GuiTextField field, int y2) {
        field.yPosition = y2;
    }

    @Override
    public int getXPosition(GuiButton button) {
        return button.xPosition;
    }

    @Override
    public int getYPosition(GuiButton button) {
        return button.yPosition;
    }

    @Override
    public int getXPosition(GuiTextField field) {
        return field.xPosition;
    }

    @Override
    public int getYPosition(GuiTextField field) {
        return field.yPosition;
    }

    @Override
    public int getTeamColorIndex(ScorePlayerTeam team) {
        return team.getChatFormat().getColorIndex();
    }

    @Override
    public void drawButton(GuiButton button, int mouseX, int mouseY) {
        button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public TileEntitySignRenderer getCustomSignRenderer() {
        return new TileEntitySignRenderer(){

            @Override
            public void renderTileEntityAt(TileEntitySign te2, double x2, double y2, double z2, float partialTicks, int destroyStage) {
                SignManager.render(te2);
                super.renderTileEntityAt(te2, x2, y2, z2, partialTicks, destroyStage);
            }
        };
    }

    @Override
    public String vectoString(Vec3i vec3i) {
        return Objects.toStringHelper(vec3i).add("x", vec3i.getX()).add("y", vec3i.getY()).add("z", vec3i.getZ()).toString();
    }

    @Override
    public PacketBuf createPacketBuf(ByteBuf byteBuf) {
        return new PacketBufOld(byteBuf);
    }

    @Override
    public int getSelectedServerInSelectionList(ServerSelectionList serverSelectionList) {
        return serverSelectionList.func_148193_k();
    }

    @Override
    public boolean hasInGameFocus() {
        return Minecraft.getMinecraft().inGameHasFocus;
    }

    @Override
    public boolean isCurrentScreenNull() {
        return Minecraft.getMinecraft().currentScreen == null;
    }

    @Override
    public boolean isMinecraftChatOpen() {
        return Minecraft.getMinecraft().currentScreen instanceof GuiChat;
    }

    @Override
    public long getLastAttackTime() {
        return Minecraft.getMinecraft().thePlayer.getLastAttackerTime();
    }

    @Override
    public EntityLivingBase getLastAttackedEntity() {
        return Minecraft.getMinecraft().thePlayer.getLastAttacker();
    }

    @Override
    public void handleBlockBuild() {
        if (LabyMod.getSettings().oldBlockbuild) {
            EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
            Minecraft mc2 = Minecraft.getMinecraft();
            if (player != null && player.isUsingItem() && mc2.gameSettings.keyBindAttack.isKeyDown() && mc2.objectMouseOver != null && mc2.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = mc2.objectMouseOver.getBlockPos();
                mc2.effectRenderer.addBlockHitEffects(blockpos, mc2.objectMouseOver.sideHit);
                Boolean blockBuildAllowedByServer = LabyMod.getInstance().getServerManager().getPermissionMap().get((Object)Permissions.Permission.BLOCKBUILD);
                if (blockBuildAllowedByServer == null) {
                    blockBuildAllowedByServer = false;
                }
                LabyMod.getInstance().getClientTickListener().setCancelSwingAnimation(blockBuildAllowedByServer == false);
                player.swingItem();
                LabyMod.getInstance().getClientTickListener().setCancelSwingAnimation(false);
            }
        }
    }

    @Override
    public double calculateEyeMovement(Entity entityIn, Entity targetEntity) {
        Vec3 eyePositionClient = targetEntity.getPositionEyes(0.0f);
        Vec3 eyePositionEntity = entityIn.getPositionEyes(0.0f);
        Vec3 eyeLookVector = entityIn.getLook(0.0f);
        eyeLookVector = new Vec3(eyeLookVector.xCoord, 0.0, eyeLookVector.zCoord);
        Vec3 eyeVector = new Vec3(eyePositionEntity.xCoord - eyePositionClient.xCoord, 0.0, eyePositionEntity.zCoord - eyePositionClient.zCoord).normalize().rotateYaw(1.5707964f);
        return eyeLookVector.dotProduct(eyeVector);
    }

    @Override
    public void setSecondLayerBit(AbstractClientPlayer player, int index, byte value) {
        byte mask = player.getDataWatcher().getWatchableObjectByte(10);
        if ((mask >> index & 1) != value) {
            mask = (byte)(mask & 0xFFFFFFBF);
            player.getDataWatcher().updateObject(10, mask);
        }
    }

    @Override
    public void sendClientSettings(String langIn, int viewIn, EntityPlayer.EnumChatVisibility chatVisibilityIn, boolean enableColorsIn, int modelPartFlagsIn) {
        if (!LabyMod.getInstance().isInGame()) {
            return;
        }
        NetHandlerPlayClient client = LabyModCore.getMinecraft().getPlayer().sendQueue;
        if (client != null) {
            client.addToSendQueue(new C15PacketClientSettings(langIn, viewIn, chatVisibilityIn, enableColorsIn, modelPartFlagsIn));
        }
    }

    @Override
    public UUID isEmotePacket(Object packet) {
        WorldClient world;
        S1CPacketEntityMetadata metaPacket;
        block7: {
            block6: {
                if (!(packet instanceof S1CPacketEntityMetadata)) {
                    return null;
                }
                try {
                    metaPacket = (S1CPacketEntityMetadata)packet;
                    Byte value = null;
                    for (DataWatcher.WatchableObject watchableObject : metaPacket.func_149376_c()) {
                        if (watchableObject.getDataValueId() != 10 || !(watchableObject.getObject() instanceof Byte)) continue;
                        value = (byte)((Byte)watchableObject.getObject());
                    }
                    if (value != null && value < 0) break block6;
                    return null;
                }
                catch (Exception error) {
                    error.printStackTrace();
                    return null;
                }
            }
            world = Minecraft.getMinecraft().theWorld;
            if (world != null) break block7;
            return null;
        }
        Entity entity = ((World)world).getEntityByID(metaPacket.getEntityId());
        return entity == null || entity.getUniqueID().equals(LabyMod.getInstance().getPlayerUUID()) || !(entity instanceof EntityPlayer) ? null : entity.getUniqueID();
    }

    @Override
    public boolean isJumpPredicted() {
        EntityPlayerSP player = this.getPlayer();
        return player != null && JumpPrediction.isJumpPredicted(player);
    }

    @Override
    public int getGameMode(UUID uuid) {
        NetHandlerPlayClient netHandler = LabyModCore.getMinecraft().getPlayer().sendQueue;
        if (netHandler == null) {
            return -1;
        }
        NetworkPlayerInfo info = netHandler.getPlayerInfo(uuid);
        if (info == null) {
            return -1;
        }
        return info.getGameType().getID();
    }

    @Override
    public Entity getEntityMouseOver() {
        MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (objectMouseOver != null && objectMouseOver.entityHit != null) {
            return objectMouseOver.entityHit;
        }
        return null;
    }

    @Override
    public boolean isElytraFlying(Entity entity) {
        return false;
    }

    @Override
    public boolean isWearingElytra(Entity entity) {
        return false;
    }

    @Override
    public Entity getRenderViewEntity() {
        return Minecraft.getMinecraft().getRenderViewEntity();
    }

    @Override
    public boolean isAchievementGui(GuiScreen screen) {
        return screen instanceof GuiAchievements;
    }

    @Override
    public String getBossBarMessage() {
        return BossStatus.bossName;
    }

    @Override
    public void setUseLeftHand(boolean value) {
    }

    @Override
    public boolean isUsingLeftHand() {
        return false;
    }

    @Override
    public PropertyMap getPropertyMap() {
        return Minecraft.getMinecraft().getProfileProperties();
    }
}

