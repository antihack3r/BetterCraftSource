/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.creativetabs;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;

public class TabCreativeOwnItems {
    public static void saveItemstack(ItemStack stack) {
        JsonArray jsonArray = TabCreativeOwnItems.getJsonArray();
        JsonObject json = new JsonObject();
        json.add("id", new JsonPrimitive(Item.getIdFromItem(stack.getItem())));
        json.add("meta", new JsonPrimitive(stack.getMetadata()));
        json.add("amount", new JsonPrimitive(stack.stackSize));
        if (stack.hasTagCompound()) {
            json.add("nbt", new JsonPrimitive(stack.stackTagCompound.toString()));
        }
        jsonArray.add(json);
        Config.getInstance();
        Config.getInstance();
        Config.write(new File(Config.ROOT_DIR, "items.json"), jsonArray);
    }

    public static List<ItemStack> getItemstacks() {
        ArrayList<ItemStack> itemStackList = Lists.newArrayList();
        TabCreativeOwnItems.getJsonArray().forEach(jsonElement -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();
                ItemStack stack = new ItemStack(Item.getItemById(json.get("id").getAsInt()), json.get("amount").getAsInt(), json.get("meta").getAsInt());
                if (json.has("nbt")) {
                    stack.stackTagCompound = JsonToNBT.getTagFromJson(json.get("nbt").getAsString());
                }
                itemStackList.add(stack);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        return itemStackList;
    }

    private static final JsonArray getJsonArray() {
        Config.getInstance();
        File file = new File(Config.ROOT_DIR, "items.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Config.getInstance();
        JsonElement jsonElement = Config.read(file);
        return jsonElement.isJsonNull() ? new JsonArray() : jsonElement.getAsJsonArray();
    }
}

