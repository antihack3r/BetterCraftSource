// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.command;

import java.util.Collections;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class RecipeCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "recipe";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.recipe.usage";
    }
    
    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.recipe.usage", new Object[0]);
        }
        final boolean flag = "give".equalsIgnoreCase(args[0]);
        final boolean flag2 = "take".equalsIgnoreCase(args[0]);
        if (!flag && !flag2) {
            throw new WrongUsageException("commands.recipe.usage", new Object[0]);
        }
        for (final EntityPlayerMP entityplayermp : CommandBase.func_193513_a(server, sender, args[1])) {
            if ("*".equals(args[2])) {
                if (flag) {
                    entityplayermp.func_192021_a(this.func_192556_d());
                    CommandBase.notifyCommandListener(sender, this, "commands.recipe.give.success.all", entityplayermp.getName());
                }
                else {
                    entityplayermp.func_192022_b(this.func_192556_d());
                    CommandBase.notifyCommandListener(sender, this, "commands.recipe.take.success.all", entityplayermp.getName());
                }
            }
            else {
                final IRecipe irecipe = CraftingManager.func_193373_a(new ResourceLocation(args[2]));
                if (irecipe == null) {
                    throw new CommandException("commands.recipe.unknownrecipe", new Object[] { args[2] });
                }
                if (irecipe.func_192399_d()) {
                    throw new CommandException("commands.recipe.unsupported", new Object[] { args[2] });
                }
                final List<IRecipe> list = Lists.newArrayList(irecipe);
                if (flag == entityplayermp.func_192037_E().func_193830_f(irecipe)) {
                    final String s = flag ? "commands.recipe.alreadyHave" : "commands.recipe.dontHave";
                    throw new CommandException(s, new Object[] { entityplayermp.getName(), irecipe.getRecipeOutput().getDisplayName() });
                }
                if (flag) {
                    entityplayermp.func_192021_a(list);
                    CommandBase.notifyCommandListener(sender, this, "commands.recipe.give.success.one", entityplayermp.getName(), irecipe.getRecipeOutput().getDisplayName());
                }
                else {
                    entityplayermp.func_192022_b(list);
                    CommandBase.notifyCommandListener(sender, this, "commands.recipe.take.success.one", irecipe.getRecipeOutput().getDisplayName(), entityplayermp.getName());
                }
            }
        }
    }
    
    private List<IRecipe> func_192556_d() {
        return (List<IRecipe>)Lists.newArrayList((Iterable<?>)CraftingManager.field_193380_a);
    }
    
    @Override
    public List<String> getTabCompletionOptions(final MinecraftServer server, final ICommandSender sender, final String[] args, @Nullable final BlockPos pos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "give", "take");
        }
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
        return (args.length == 3) ? CommandBase.getListOfStringsMatchingLastWord(args, CraftingManager.field_193380_a.getKeys()) : Collections.emptyList();
    }
}
