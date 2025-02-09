/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion
extends Item {
    private Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();
    private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();

    public ItemPotion() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List<PotionEffect> getEffects(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
            ArrayList<PotionEffect> list1 = Lists.newArrayList();
            NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);
                if (potioneffect != null) {
                    list1.add(potioneffect);
                }
                ++i2;
            }
            return list1;
        }
        List<PotionEffect> list = this.effectCache.get(stack.getMetadata());
        if (list == null) {
            list = PotionHelper.getPotionEffects(stack.getMetadata(), false);
            this.effectCache.put(stack.getMetadata(), list);
        }
        return list;
    }

    public List<PotionEffect> getEffects(int meta) {
        List<PotionEffect> list = this.effectCache.get(meta);
        if (list == null) {
            list = PotionHelper.getPotionEffects(meta, false);
            this.effectCache.put(meta, list);
        }
        return list;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        List<PotionEffect> list;
        if (!playerIn.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        if (!worldIn.isRemote && (list = this.getEffects(stack)) != null) {
            for (PotionEffect potioneffect : list) {
                playerIn.addPotionEffect(new PotionEffect(potioneffect));
            }
        }
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        if (!playerIn.capabilities.isCreativeMode) {
            if (stack.stackSize <= 0) {
                return new ItemStack(Items.glass_bottle);
            }
            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (ItemPotion.isSplash(itemStackIn.getMetadata())) {
            if (!playerIn.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, (EntityLivingBase)playerIn, itemStackIn));
            }
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    public static boolean isSplash(int meta) {
        return (meta & 0x4000) != 0;
    }

    public int getColorFromDamage(int meta) {
        return PotionHelper.getLiquidColor(meta, false);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return renderPass > 0 ? 0xFFFFFF : this.getColorFromDamage(stack.getMetadata());
    }

    public boolean isEffectInstant(int meta) {
        List<PotionEffect> list = this.getEffects(meta);
        if (list != null && !list.isEmpty()) {
            for (PotionEffect potioneffect : list) {
                if (!Potion.potionTypes[potioneffect.getPotionID()].isInstant()) continue;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        List<PotionEffect> list;
        if (stack.getMetadata() == 0) {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }
        String s2 = "";
        if (ItemPotion.isSplash(stack.getMetadata())) {
            s2 = String.valueOf(StatCollector.translateToLocal("potion.prefix.grenade").trim()) + " ";
        }
        if ((list = Items.potionitem.getEffects(stack)) != null && !list.isEmpty()) {
            String s22 = list.get(0).getEffectName();
            s22 = String.valueOf(s22) + ".postfix";
            return String.valueOf(s2) + StatCollector.translateToLocal(s22).trim();
        }
        String s1 = PotionHelper.getPotionPrefix(stack.getMetadata());
        return String.valueOf(StatCollector.translateToLocal(s1).trim()) + " " + super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.getMetadata() != 0) {
            List<PotionEffect> list = Items.potionitem.getEffects(stack);
            HashMultimap<String, AttributeModifier> multimap = HashMultimap.create();
            if (list != null && !list.isEmpty()) {
                for (PotionEffect potionEffect : list) {
                    String s1 = StatCollector.translateToLocal(potionEffect.getEffectName()).trim();
                    Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                    Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
                    if (map != null && map.size() > 0) {
                        for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                            AttributeModifier attributemodifier = entry.getValue();
                            AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potionEffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            multimap.put(entry.getKey().getAttributeUnlocalizedName(), attributemodifier1);
                        }
                    }
                    if (potionEffect.getAmplifier() > 0) {
                        s1 = String.valueOf(s1) + " " + StatCollector.translateToLocal("potion.potency." + potionEffect.getAmplifier()).trim();
                    }
                    if (potionEffect.getDuration() > 20) {
                        s1 = String.valueOf(s1) + " (" + Potion.getDurationString(potionEffect) + ")";
                    }
                    if (potion.isBadEffect()) {
                        tooltip.add((Object)((Object)EnumChatFormatting.RED) + s1);
                        continue;
                    }
                    tooltip.add((Object)((Object)EnumChatFormatting.GRAY) + s1);
                }
            } else {
                String string = StatCollector.translateToLocal("potion.empty").trim();
                tooltip.add((Object)((Object)EnumChatFormatting.GRAY) + string);
            }
            if (!multimap.isEmpty()) {
                tooltip.add("");
                tooltip.add((Object)((Object)EnumChatFormatting.DARK_PURPLE) + StatCollector.translateToLocal("potion.effects.whenDrank"));
                for (Map.Entry entry : multimap.entries()) {
                    AttributeModifier attributemodifier2 = (AttributeModifier)entry.getValue();
                    double d0 = attributemodifier2.getAmount();
                    double d1 = attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2 ? attributemodifier2.getAmount() : attributemodifier2.getAmount() * 100.0;
                    if (d0 > 0.0) {
                        tooltip.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                        continue;
                    }
                    if (!(d0 < 0.0)) continue;
                    tooltip.add((Object)((Object)EnumChatFormatting.RED) + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1 *= -1.0), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        List<PotionEffect> list = this.getEffects(stack);
        return list != null && !list.isEmpty();
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        super.getSubItems(itemIn, tab, subItems);
        if (SUB_ITEMS_CACHE.isEmpty()) {
            int i2 = 0;
            while (i2 <= 15) {
                int j2 = 0;
                while (j2 <= 1) {
                    int lvt_6_1_ = j2 == 0 ? i2 | 0x2000 : i2 | 0x4000;
                    int l2 = 0;
                    while (l2 <= 2) {
                        List<PotionEffect> list;
                        int i1 = lvt_6_1_;
                        if (l2 != 0) {
                            if (l2 == 1) {
                                i1 = lvt_6_1_ | 0x20;
                            } else if (l2 == 2) {
                                i1 = lvt_6_1_ | 0x40;
                            }
                        }
                        if ((list = PotionHelper.getPotionEffects(i1, false)) != null && !list.isEmpty()) {
                            SUB_ITEMS_CACHE.put(list, i1);
                        }
                        ++l2;
                    }
                    ++j2;
                }
                ++i2;
            }
        }
        for (int j1 : SUB_ITEMS_CACHE.values()) {
            subItems.add(new ItemStack(itemIn, 1, j1));
        }
    }
}

