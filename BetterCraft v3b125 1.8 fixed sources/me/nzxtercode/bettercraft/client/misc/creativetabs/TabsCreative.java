/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.creativetabs;

import java.util.List;
import me.nzxtercode.bettercraft.client.misc.creativetabs.impl.TabCustom;
import me.nzxtercode.bettercraft.client.misc.creativetabs.impl.TabExploits;
import me.nzxtercode.bettercraft.client.misc.creativetabs.impl.TabMisc;
import me.nzxtercode.bettercraft.client.misc.creativetabs.impl.TabOwn;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class TabsCreative {
    public static TabsCreative[] creativeTabArray = new TabsCreative[6];
    public static final TabsCreative tabMisc = new TabMisc(0, "misc"){

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.anvil);
        }
    };
    public static final TabsCreative tabCustom = new TabCustom(1, "custom"){

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.chest);
        }
    };
    public static final TabsCreative tabExploits = new TabExploits(2, "exploits"){

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.command_block);
        }
    };
    public static final TabsCreative tabOwn = new TabOwn(3, "own"){

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.barrier);
        }
    };
    public static final TabsCreative tabAllSearch = new TabsCreative(4, "search"){

        @Override
        public Item getTabIconItem() {
            return Items.compass;
        }
    }.setBackgroundImageName("item_search.png");
    public static final TabsCreative tabInventory = new TabsCreative(5, "inventory"){

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.chest);
        }
    }.setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;
    private boolean drawTitle = true;
    private EnumEnchantmentType[] enchantmentTypes;
    private ItemStack iconItemStack;

    public TabsCreative(int index, String label) {
        if (index >= creativeTabArray.length) {
            TabsCreative[] tmp = new TabsCreative[index + 1];
            int x2 = 0;
            while (x2 < creativeTabArray.length) {
                tmp[x2] = creativeTabArray[x2];
                ++x2;
            }
            creativeTabArray = tmp;
        }
        this.tabIndex = index;
        this.tabLabel = label;
        TabsCreative.creativeTabArray[index] = this;
    }

    public int getTabIndex() {
        return this.tabIndex;
    }

    public String getTabLabel() {
        return this.tabLabel;
    }

    public String getTranslatedTabLabel() {
        return "itemGroup." + this.getTabLabel();
    }

    public ItemStack getIconItemStack() {
        if (this.iconItemStack == null) {
            this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
        }
        return this.iconItemStack;
    }

    public abstract Item getTabIconItem();

    public int getIconItemDamage() {
        return 0;
    }

    public String getBackgroundImageName() {
        return this.theTexture;
    }

    public TabsCreative setBackgroundImageName(String texture) {
        this.theTexture = texture;
        return this;
    }

    public boolean drawInForegroundOfTab() {
        return this.drawTitle;
    }

    public TabsCreative setNoTitle() {
        this.drawTitle = false;
        return this;
    }

    public boolean shouldHidePlayerInventory() {
        return this.hasScrollbar;
    }

    public TabsCreative setNoScrollbar() {
        this.hasScrollbar = false;
        return this;
    }

    public int getTabColumn() {
        if (this.tabIndex > 11) {
            return (this.tabIndex - 12) % 10 % 5;
        }
        return this.tabIndex % 6;
    }

    public boolean isTabInFirstRow() {
        if (this.tabIndex > 11) {
            return (this.tabIndex - 12) % 10 < 5;
        }
        return this.tabIndex < 6;
    }

    public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
        return this.enchantmentTypes;
    }

    public TabsCreative setRelevantEnchantmentTypes(EnumEnchantmentType ... types) {
        this.enchantmentTypes = types;
        return this;
    }

    public boolean hasRelevantEnchantmentType(EnumEnchantmentType enchantmentType) {
        if (this.enchantmentTypes == null) {
            return false;
        }
        EnumEnchantmentType[] enumEnchantmentTypeArray = this.enchantmentTypes;
        int n2 = this.enchantmentTypes.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumEnchantmentType enumenchantmenttype = enumEnchantmentTypeArray[n3];
            if (enumenchantmenttype == enchantmentType) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public void displayAllReleventItems(List<ItemStack> p_78018_1_) {
        for (Item item : Item.itemRegistry) {
            if (item == null || item.getTabCreative() != this) continue;
            item.getSubItems(item, this, p_78018_1_);
        }
        if (this.getRelevantEnchantmentTypes() != null) {
            this.addEnchantmentBooksToList(p_78018_1_, this.getRelevantEnchantmentTypes());
        }
    }

    public void addEnchantmentBooksToList(List<ItemStack> itemList, EnumEnchantmentType ... enchantmentType) {
        Enchantment[] enchantmentArray = Enchantment.enchantmentsBookList;
        int n2 = Enchantment.enchantmentsBookList.length;
        int n3 = 0;
        while (n3 < n2) {
            Enchantment enchantment = enchantmentArray[n3];
            if (enchantment != null && enchantment.type != null) {
                boolean flag = false;
                int i2 = 0;
                while (i2 < enchantmentType.length && !flag) {
                    if (enchantment.type == enchantmentType[i2]) {
                        flag = true;
                    }
                    ++i2;
                }
                if (flag) {
                    itemList.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
                }
            }
            ++n3;
        }
    }

    public int getTabPage() {
        if (this.tabIndex > 11) {
            return (this.tabIndex - 12) / 10 + 1;
        }
        return 0;
    }

    public static int getNextID() {
        return creativeTabArray.length;
    }

    public boolean hasSearchBar() {
        return this.tabIndex == TabsCreative.tabAllSearch.tabIndex;
    }

    public int getSearchbarWidth() {
        return 89;
    }
}

