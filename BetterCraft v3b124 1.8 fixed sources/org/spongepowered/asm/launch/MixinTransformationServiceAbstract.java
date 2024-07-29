/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.IEnvironment
 *  cpw.mods.modlauncher.api.ITransformationService
 *  cpw.mods.modlauncher.api.ITransformationService$OptionResult
 *  cpw.mods.modlauncher.api.ITransformer
 *  cpw.mods.modlauncher.api.IncompatibleEnvironmentException
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService
 */
package org.spongepowered.asm.launch;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionSpecBuilder;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.MixinInitialisationError;
import org.spongepowered.asm.launch.MixinLaunchPluginLegacy;

public abstract class MixinTransformationServiceAbstract
implements ITransformationService {
    private ArgumentAcceptingOptionSpec<String> mixinsArgument;
    private List<String> commandLineMixins = new ArrayList<String>();
    private MixinLaunchPluginLegacy plugin;

    public String name() {
        return "mixin";
    }

    public void arguments(BiFunction<String, String, OptionSpecBuilder> argumentBuilder) {
        this.mixinsArgument = argumentBuilder.apply("config", "a mixin config to load").withRequiredArg().ofType(String.class);
    }

    public void argumentValues(ITransformationService.OptionResult option) {
        this.commandLineMixins.addAll(option.values(this.mixinsArgument));
    }

    public void onLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException {
    }

    public void initialize(IEnvironment environment) {
        Optional plugin = environment.findLaunchPlugin("mixin");
        if (!plugin.isPresent()) {
            throw new MixinInitialisationError("Mixin Launch Plugin Service could not be located");
        }
        ILaunchPluginService launchPlugin = (ILaunchPluginService)plugin.get();
        if (!(launchPlugin instanceof MixinLaunchPluginLegacy)) {
            throw new MixinInitialisationError("Mixin Launch Plugin Service is present but not compatible");
        }
        this.plugin = (MixinLaunchPluginLegacy)launchPlugin;
        MixinBootstrap.start();
        this.plugin.init(environment, this.commandLineMixins);
    }

    public List<Map.Entry<String, Path>> runScan(IEnvironment environment) {
        return Collections.emptyList();
    }

    public List<ITransformer> transformers() {
        return ImmutableList.of();
    }
}

