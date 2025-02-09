/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import me.nzxtercode.bettercraft.client.commands.CommandManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.ChatComponentText;

public class GiveCommand
extends Command {
    public GiveCommand() {
        super("give", new String[0]);
    }

    @Override
    public void run(String alias, String[] args) {
        if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
            if (args.length == 0) {
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give item <name/id>"));
                GuiNewChat guiNewChat2 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat2.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give skull <player>"));
                GuiNewChat guiNewChat3 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat3.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give cmdblock"));
                GuiNewChat guiNewChat4 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat4.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give dragon_egg"));
                GuiNewChat guiNewChat5 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat5.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give mob_spawner"));
                GuiNewChat guiNewChat6 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat6.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give opsword"));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("opsword")) {
                    Item item = Item.getItemById(276);
                    ItemStack itemStack = new ItemStack(item, 1);
                    itemStack.setStackDisplayName("OP Sword");
                    itemStack.setItemDamage(30000);
                    itemStack.addEnchantment(Enchantment.sharpness, 30000);
                    itemStack.addEnchantment(Enchantment.fireAspect, 30000);
                    itemStack.addEnchantment(Enchantment.unbreaking, 30000);
                    itemStack.addEnchantment(Enchantment.lure, 30000);
                    itemStack.addEnchantment(Enchantment.looting, 30000);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(5, itemStack));
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    BetterCraft.getInstance();
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("item.", "") + " \u00a77in head slot"));
                } else if (args[0].equalsIgnoreCase("cmdblock") || args[0].equalsIgnoreCase("137")) {
                    Block item = Blocks.command_block;
                    ItemStack itemStack = new ItemStack(item, 1);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(5, itemStack));
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    BetterCraft.getInstance();
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("tile.", "") + " \u00a77in head slot"));
                } else if (args[0].equalsIgnoreCase("dragon_egg") || args[0].equalsIgnoreCase("122")) {
                    Block item = Blocks.dragon_egg;
                    ItemStack itemStack = new ItemStack(item, 1);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(5, itemStack));
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    BetterCraft.getInstance();
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("tile.", "") + " \u00a77in head slot"));
                } else if (args[0].equalsIgnoreCase("mob_spawner") || args[0].equalsIgnoreCase("spawner") || args[0].equalsIgnoreCase("52")) {
                    Block item = Blocks.mob_spawner;
                    ItemStack itemStack = new ItemStack(item, 1);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(4, itemStack));
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    BetterCraft.getInstance();
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("tile.", "") + " \u00a77in head slot"));
                }
            } else if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("item")) {
                    Item item = Item.getByNameOrId(args[1]);
                    ItemStack itemStack = new ItemStack(item, 1);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(36, itemStack));
                    try {
                        GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                        BetterCraft.getInstance();
                        guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("tile.", "").replace("item.", "") + " \u00a77in head slot"));
                    }
                    catch (Exception e2) {
                        GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                        BetterCraft.getInstance();
                        guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Item not exist"));
                    }
                } else if (args[0].equalsIgnoreCase("skull")) {
                    Item item = Items.skull;
                    ItemStack itemStack = new ItemStack(item, 1, 3);
                    NBTTagCompound entityTag = new NBTTagCompound();
                    entityTag.setString("SkullOwner", args[1]);
                    itemStack.setTagCompound(entityTag);
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(36, itemStack));
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    BetterCraft.getInstance();
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "You got a \u00a7f" + item.getUnlocalizedName().replace("item.", "") + " \u00a77in head slot"));
                }
            }
        }
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

