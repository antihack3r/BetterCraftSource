/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {
    private static final Random enchantmentRand = new Random();
    private static final ModifierDamage enchantmentModifierDamage = new ModifierDamage();
    private static final ModifierLiving enchantmentModifierLiving = new ModifierLiving();
    private static final HurtIterator ENCHANTMENT_ITERATOR_HURT = new HurtIterator();
    private static final DamageIterator ENCHANTMENT_ITERATOR_DAMAGE = new DamageIterator();

    public static int getEnchantmentLevel(int enchID, ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        NBTTagList nbttaglist = stack.getEnchantmentTagList();
        if (nbttaglist == null) {
            return 0;
        }
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            short j2 = nbttaglist.getCompoundTagAt(i2).getShort("id");
            short k2 = nbttaglist.getCompoundTagAt(i2).getShort("lvl");
            if (j2 == enchID) {
                return k2;
            }
            ++i2;
        }
        return 0;
    }

    public static Map<Integer, Integer> getEnchantments(ItemStack stack) {
        NBTTagList nbttaglist;
        LinkedHashMap<Integer, Integer> map = Maps.newLinkedHashMap();
        NBTTagList nBTTagList = nbttaglist = stack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(stack) : stack.getEnchantmentTagList();
        if (nbttaglist != null) {
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                short j2 = nbttaglist.getCompoundTagAt(i2).getShort("id");
                short k2 = nbttaglist.getCompoundTagAt(i2).getShort("lvl");
                map.put(Integer.valueOf(j2), Integer.valueOf(k2));
                ++i2;
            }
        }
        return map;
    }

    public static void setEnchantments(Map<Integer, Integer> enchMap, ItemStack stack) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i2 : enchMap.keySet()) {
            Enchantment enchantment = Enchantment.getEnchantmentById(i2);
            if (enchantment == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short)i2);
            nbttagcompound.setShort("lvl", (short)enchMap.get(i2).intValue());
            nbttaglist.appendTag(nbttagcompound);
            if (stack.getItem() != Items.enchanted_book) continue;
            Items.enchanted_book.addEnchantment(stack, new EnchantmentData(enchantment, enchMap.get(i2)));
        }
        if (nbttaglist.tagCount() > 0) {
            if (stack.getItem() != Items.enchanted_book) {
                stack.setTagInfo("ench", nbttaglist);
            }
        } else if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag("ench");
        }
    }

    public static int getMaxEnchantmentLevel(int enchID, ItemStack[] stacks) {
        if (stacks == null) {
            return 0;
        }
        int i2 = 0;
        ItemStack[] itemStackArray = stacks;
        int n2 = stacks.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemstack = itemStackArray[n3];
            int j2 = EnchantmentHelper.getEnchantmentLevel(enchID, itemstack);
            if (j2 > i2) {
                i2 = j2;
            }
            ++n3;
        }
        return i2;
    }

    private static void applyEnchantmentModifier(IModifier modifier, ItemStack stack) {
        NBTTagList nbttaglist;
        if (stack != null && (nbttaglist = stack.getEnchantmentTagList()) != null) {
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                short j2 = nbttaglist.getCompoundTagAt(i2).getShort("id");
                short k2 = nbttaglist.getCompoundTagAt(i2).getShort("lvl");
                if (Enchantment.getEnchantmentById(j2) != null) {
                    modifier.calculateModifier(Enchantment.getEnchantmentById(j2), k2);
                }
                ++i2;
            }
        }
    }

    private static void applyEnchantmentModifierArray(IModifier modifier, ItemStack[] stacks) {
        ItemStack[] itemStackArray = stacks;
        int n2 = stacks.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemstack = itemStackArray[n3];
            EnchantmentHelper.applyEnchantmentModifier(modifier, itemstack);
            ++n3;
        }
    }

    public static int getEnchantmentModifierDamage(ItemStack[] stacks, DamageSource source) {
        EnchantmentHelper.enchantmentModifierDamage.damageModifier = 0;
        EnchantmentHelper.enchantmentModifierDamage.source = source;
        EnchantmentHelper.applyEnchantmentModifierArray(enchantmentModifierDamage, stacks);
        if (EnchantmentHelper.enchantmentModifierDamage.damageModifier > 25) {
            EnchantmentHelper.enchantmentModifierDamage.damageModifier = 25;
        } else if (EnchantmentHelper.enchantmentModifierDamage.damageModifier < 0) {
            EnchantmentHelper.enchantmentModifierDamage.damageModifier = 0;
        }
        return (EnchantmentHelper.enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((EnchantmentHelper.enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    public static float getModifierForCreature(ItemStack p_152377_0_, EnumCreatureAttribute p_152377_1_) {
        EnchantmentHelper.enchantmentModifierLiving.livingModifier = 0.0f;
        EnchantmentHelper.enchantmentModifierLiving.entityLiving = p_152377_1_;
        EnchantmentHelper.applyEnchantmentModifier(enchantmentModifierLiving, p_152377_0_);
        return EnchantmentHelper.enchantmentModifierLiving.livingModifier;
    }

    public static void applyThornEnchantments(EntityLivingBase p_151384_0_, Entity p_151384_1_) {
        EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT.attacker = p_151384_1_;
        EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT.user = p_151384_0_;
        if (p_151384_0_ != null) {
            EnchantmentHelper.applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_HURT, p_151384_0_.getInventory());
        }
        if (p_151384_1_ instanceof EntityPlayer) {
            EnchantmentHelper.applyEnchantmentModifier(ENCHANTMENT_ITERATOR_HURT, p_151384_0_.getHeldItem());
        }
    }

    public static void applyArthropodEnchantments(EntityLivingBase p_151385_0_, Entity p_151385_1_) {
        EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE.user = p_151385_0_;
        EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE.target = p_151385_1_;
        if (p_151385_0_ != null) {
            EnchantmentHelper.applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_DAMAGE, p_151385_0_.getInventory());
        }
        if (p_151385_0_ instanceof EntityPlayer) {
            EnchantmentHelper.applyEnchantmentModifier(ENCHANTMENT_ITERATOR_DAMAGE, p_151385_0_.getHeldItem());
        }
    }

    public static int getKnockbackModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, player.getHeldItem());
    }

    public static int getFireAspectModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, player.getHeldItem());
    }

    public static int getRespiration(Entity player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.respiration.effectId, player.getInventory());
    }

    public static int getDepthStriderModifier(Entity player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, player.getInventory());
    }

    public static int getEfficiencyModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, player.getHeldItem());
    }

    public static boolean getSilkTouchModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, player.getHeldItem()) > 0;
    }

    public static int getFortuneModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem());
    }

    public static int getLuckOfSeaModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, player.getHeldItem());
    }

    public static int getLureModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.lure.effectId, player.getHeldItem());
    }

    public static int getLootingModifier(EntityLivingBase player) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, player.getHeldItem());
    }

    public static boolean getAquaAffinityModifier(EntityLivingBase player) {
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, player.getInventory()) > 0;
    }

    public static ItemStack getEnchantedItem(Enchantment p_92099_0_, EntityLivingBase p_92099_1_) {
        ItemStack[] itemStackArray = p_92099_1_.getInventory();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemstack = itemStackArray[n3];
            if (itemstack != null && EnchantmentHelper.getEnchantmentLevel(p_92099_0_.effectId, itemstack) > 0) {
                return itemstack;
            }
            ++n3;
        }
        return null;
    }

    public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
        Item item = stack.getItem();
        int i2 = item.getItemEnchantability();
        if (i2 <= 0) {
            return 0;
        }
        if (power > 15) {
            power = 15;
        }
        int j2 = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
        return enchantNum == 0 ? Math.max(j2 / 3, 1) : (enchantNum == 1 ? j2 * 2 / 3 + 1 : Math.max(j2, power * 2));
    }

    public static ItemStack addRandomEnchantment(Random p_77504_0_, ItemStack p_77504_1_, int p_77504_2_) {
        boolean flag;
        List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_);
        boolean bl2 = flag = p_77504_1_.getItem() == Items.book;
        if (flag) {
            p_77504_1_.setItem(Items.enchanted_book);
        }
        if (list != null) {
            for (EnchantmentData enchantmentdata : list) {
                if (flag) {
                    Items.enchanted_book.addEnchantment(p_77504_1_, enchantmentdata);
                    continue;
                }
                p_77504_1_.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
            }
        }
        return p_77504_1_;
    }

    public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int p_77513_2_) {
        EnchantmentData enchantmentdata;
        float f2;
        Item item = itemStackIn.getItem();
        int i2 = item.getItemEnchantability();
        if (i2 <= 0) {
            return null;
        }
        i2 /= 2;
        int j2 = (i2 = 1 + randomIn.nextInt((i2 >> 1) + 1) + randomIn.nextInt((i2 >> 1) + 1)) + p_77513_2_;
        int k2 = (int)((float)j2 * (1.0f + (f2 = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0f) * 0.15f)) + 0.5f);
        if (k2 < 1) {
            k2 = 1;
        }
        ArrayList<EnchantmentData> list = null;
        Map<Integer, EnchantmentData> map = EnchantmentHelper.mapEnchantmentData(k2, itemStackIn);
        if (map != null && !map.isEmpty() && (enchantmentdata = WeightedRandom.getRandomItem(randomIn, map.values())) != null) {
            list = Lists.newArrayList();
            list.add(enchantmentdata);
            int l2 = k2;
            while (randomIn.nextInt(50) <= l2) {
                Iterator<Integer> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer integer = iterator.next();
                    boolean flag = true;
                    for (EnchantmentData enchantmentdata1 : list) {
                        if (enchantmentdata1.enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(integer))) continue;
                        flag = false;
                        break;
                    }
                    if (flag) continue;
                    iterator.remove();
                }
                if (!map.isEmpty()) {
                    EnchantmentData enchantmentdata2 = WeightedRandom.getRandomItem(randomIn, map.values());
                    list.add(enchantmentdata2);
                }
                l2 >>= 1;
            }
        }
        return list;
    }

    public static Map<Integer, EnchantmentData> mapEnchantmentData(int p_77505_0_, ItemStack p_77505_1_) {
        Item item = p_77505_1_.getItem();
        HashMap<Integer, EnchantmentData> map = null;
        boolean flag = p_77505_1_.getItem() == Items.book;
        Enchantment[] enchantmentArray = Enchantment.enchantmentsBookList;
        int n2 = Enchantment.enchantmentsBookList.length;
        int n3 = 0;
        while (n3 < n2) {
            Enchantment enchantment = enchantmentArray[n3];
            if (enchantment != null && (enchantment.type.canEnchantItem(item) || flag)) {
                int i2 = enchantment.getMinLevel();
                while (i2 <= enchantment.getMaxLevel()) {
                    if (p_77505_0_ >= enchantment.getMinEnchantability(i2) && p_77505_0_ <= enchantment.getMaxEnchantability(i2)) {
                        if (map == null) {
                            map = Maps.newHashMap();
                        }
                        map.put(enchantment.effectId, new EnchantmentData(enchantment, i2));
                    }
                    ++i2;
                }
            }
            ++n3;
        }
        return map;
    }

    static final class DamageIterator
    implements IModifier {
        public EntityLivingBase user;
        public Entity target;

        private DamageIterator() {
        }

        @Override
        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
            enchantmentIn.onEntityDamaged(this.user, this.target, enchantmentLevel);
        }
    }

    static final class HurtIterator
    implements IModifier {
        public EntityLivingBase user;
        public Entity attacker;

        private HurtIterator() {
        }

        @Override
        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
            enchantmentIn.onUserHurt(this.user, this.attacker, enchantmentLevel);
        }
    }

    static interface IModifier {
        public void calculateModifier(Enchantment var1, int var2);
    }

    static final class ModifierDamage
    implements IModifier {
        public int damageModifier;
        public DamageSource source;

        private ModifierDamage() {
        }

        @Override
        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
            this.damageModifier += enchantmentIn.calcModifierDamage(enchantmentLevel, this.source);
        }
    }

    static final class ModifierLiving
    implements IModifier {
        public float livingModifier;
        public EnumCreatureAttribute entityLiving;

        private ModifierLiving() {
        }

        @Override
        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel) {
            this.livingModifier += enchantmentIn.calcDamageByCreature(enchantmentLevel, this.entityLiving);
        }
    }
}

