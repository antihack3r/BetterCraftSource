/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.entity;

import me.nzxtercode.bettercraft.client.commands.CommandManager;
import me.nzxtercode.bettercraft.client.events.PlayerTickEvent;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP
extends AbstractClientPlayer {
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    private float lastReportedYaw;
    private float lastReportedPitch;
    private boolean serverSneakState;
    private boolean serverSprintState;
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;
    protected int sprintToggleTimer;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;
    public float timeInPortal;
    public float prevTimeInPortal;

    public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float healAmount) {
    }

    @Override
    public void mountEntity(Entity entityIn) {
        super.mountEntity(entityIn);
        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }

    @Override
    public void onUpdate() {
        if (this.mc.thePlayer.moveForward > 0.0f) {
            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), true);
        }
        ASMEventManager.call(new PlayerTickEvent());
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
            super.onUpdate();
            if (this.isRiding()) {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            } else {
                this.onUpdateWalkingPlayer();
            }
        }
    }

    public void onUpdateWalkingPlayer() {
        boolean flag1;
        boolean flag = this.isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        if ((flag1 = this.isSneaking()) != this.serverSneakState) {
            if (flag1) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = flag1;
        }
        if (this.isCurrentViewEntity()) {
            boolean flag3;
            double d0 = this.posX - this.lastReportedPosX;
            double d1 = this.getEntityBoundingBox().minY - this.lastReportedPosY;
            double d2 = this.posZ - this.lastReportedPosZ;
            double d3 = this.rotationYaw - this.lastReportedYaw;
            double d4 = this.rotationPitch - this.lastReportedPitch;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean bl2 = flag3 = d3 != 0.0 || d4 != 0.0;
            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
                } else if (flag2) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround));
                } else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(this.onGround));
                }
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
                flag2 = false;
            }
            ++this.positionUpdateTicks;
            if (flag2) {
                this.lastReportedPosX = this.posX;
                this.lastReportedPosY = this.getEntityBoundingBox().minY;
                this.lastReportedPosZ = this.posZ;
                this.positionUpdateTicks = 0;
            }
            if (flag3) {
                this.lastReportedYaw = this.rotationYaw;
                this.lastReportedPitch = this.rotationPitch;
            }
        }
    }

    @Override
    public EntityItem dropOneItem(boolean dropAll) {
        C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(c07packetplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    @Override
    protected void joinEntityItemWithWorld(EntityItem itemIn) {
    }

    public void sendChatMessage(String message) {
        if (message.startsWith(CommandManager.getInstance().prefix)) {
            if (CommandManager.getInstance().executeCommand(message)) {
                return;
            }
            return;
        }
        this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
    }

    @Override
    public void swingItem() {
        super.swingItem();
        this.sendQueue.addToSendQueue(new C0APacketAnimation());
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    @Override
    public void closeScreen() {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }

    public void setPlayerSPHealth(float health) {
        if (this.hasValidHealth) {
            float f2 = this.getHealth() - health;
            if (f2 <= 0.0f) {
                this.setHealth(health);
                if (f2 < 0.0f) {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            } else {
                this.lastDamage = f2;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f2);
                this.maxHurtTime = 10;
                this.hurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    @Override
    public void addStat(StatBase stat, int amount) {
        if (stat != null && stat.isIndependent) {
            super.addStat(stat, amount);
        }
    }

    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    @Override
    public boolean isUser() {
        return true;
    }

    protected void sendHorseJump() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0f)));
    }

    public void sendHorseInventory() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }

    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }

    @Override
    public void addChatComponentMessage(IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    @Override
    protected boolean pushOutOfBlocks(double x2, double y2, double z2) {
        if (this.noClip) {
            return false;
        }
        BlockPos blockpos = new BlockPos(x2, y2, z2);
        double d0 = x2 - (double)blockpos.getX();
        double d1 = z2 - (double)blockpos.getZ();
        if (!this.isOpenBlockSpace(blockpos)) {
            int i2 = -1;
            double d2 = 9999.0;
            if (this.isOpenBlockSpace(blockpos.west()) && d0 < d2) {
                d2 = d0;
                i2 = 0;
            }
            if (this.isOpenBlockSpace(blockpos.east()) && 1.0 - d0 < d2) {
                d2 = 1.0 - d0;
                i2 = 1;
            }
            if (this.isOpenBlockSpace(blockpos.north()) && d1 < d2) {
                d2 = d1;
                i2 = 4;
            }
            if (this.isOpenBlockSpace(blockpos.south()) && 1.0 - d1 < d2) {
                d2 = 1.0 - d1;
                i2 = 5;
            }
            float f2 = 0.1f;
            if (i2 == 0) {
                this.motionX = -f2;
            }
            if (i2 == 1) {
                this.motionX = f2;
            }
            if (i2 == 4) {
                this.motionZ = -f2;
            }
            if (i2 == 5) {
                this.motionZ = f2;
            }
        }
        return false;
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    public void setXPStats(float currentXP, int maxXP, int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return permLevel <= 0;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }

    @Override
    public void playSound(String name, float volume, float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void openEditSign(TileEntitySign signTile) {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }

    @Override
    public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }

    @Override
    public void displayGUIBook(ItemStack bookStack) {
        Item item = bookStack.getItem();
        if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
        }
    }

    @Override
    public void displayGUIChest(IInventory chestInventory) {
        String s2;
        String string = s2 = chestInventory instanceof IInteractionObject ? ((IInteractionObject)((Object)chestInventory)).getGuiID() : "minecraft:container";
        if ("minecraft:chest".equals(s2)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else if ("minecraft:hopper".equals(s2)) {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        } else if ("minecraft:furnace".equals(s2)) {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        } else if ("minecraft:brewing_stand".equals(s2)) {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        } else if ("minecraft:beacon".equals(s2)) {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        } else if (!"minecraft:dispenser".equals(s2) && !"minecraft:dropper".equals(s2)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else {
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
        }
    }

    @Override
    public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }

    @Override
    public void displayGui(IInteractionObject guiOwner) {
        String s2 = guiOwner.getGuiID();
        if ("minecraft:crafting_table".equals(s2)) {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        } else if ("minecraft:enchanting_table".equals(s2)) {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        } else if ("minecraft:anvil".equals(s2)) {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }

    @Override
    public void displayVillagerTradeGui(IMerchant villager) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }

    @Override
    public void onCriticalHit(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }

    @Override
    public void onEnchantmentCritical(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }

    @Override
    public boolean isSneaking() {
        boolean flag;
        boolean bl2 = flag = this.movementInput != null ? this.movementInput.sneak : false;
        return flag && !this.sleeping;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5);
            this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5);
        }
    }

    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    @Override
    public void onLivingUpdate() {
        boolean flag3;
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen(null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667f;
            if (this.timeInPortal > 1.0f) {
                this.timeInPortal = 1.0f;
            }
        } else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }
        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
        float f2 = 0.8f;
        boolean flag2 = this.movementInput.moveForward >= f2;
        this.movementInput.updatePlayerMoveState();
        if (this.isUsingItem() && !this.isRiding()) {
            this.movementInput.moveStrafe *= 0.2f;
            this.movementInput.moveForward *= 0.2f;
            this.sprintToggleTimer = 0;
        }
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
        boolean bl2 = flag3 = (float)this.getFoodStats().getFoodLevel() > 6.0f || this.capabilities.allowFlying;
        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f2 && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }
        if (!this.isSprinting() && this.movementInput.moveForward >= f2 && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting() && (this.movementInput.moveForward < f2 || this.isCollidedHorizontally || !flag3)) {
            this.setSprinting(false);
        }
        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            } else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= (double)(this.capabilities.getFlySpeed() * 3.0f);
            }
            if (this.movementInput.jump) {
                this.motionY += (double)(this.capabilities.getFlySpeed() * 3.0f);
            }
        }
        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            } else if (flag) {
                ++this.horseJumpPowerCounter;
                this.horseJumpPower = this.horseJumpPowerCounter < 10 ? (float)this.horseJumpPowerCounter * 0.1f : 0.8f + 2.0f / (float)(this.horseJumpPowerCounter - 9) * 0.1f;
            }
        } else {
            this.horseJumpPower = 0.0f;
        }
        super.onLivingUpdate();
        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }
}

