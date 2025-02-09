/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.launchwrapper;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;
import org.apache.logging.log4j.Level;

public class Launch {
    private static final String DEFAULT_TWEAK = "net.minecraft.launchwrapper.VanillaTweaker";
    public static File minecraftHome;
    public static File assetsDir;
    public static Map<String, Object> blackboard;
    public static LaunchClassLoader classLoader;

    public static void main(String[] args) {
        new Launch().launch(args);
    }

    private Launch() {
        URLClassLoader ucl = (URLClassLoader)this.getClass().getClassLoader();
        classLoader = new LaunchClassLoader(ucl.getURLs());
        blackboard = new HashMap<String, Object>();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private void launch(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        ArgumentAcceptingOptionSpec<String> profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        ArgumentAcceptingOptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK, (String[])new String[0]);
        NonOptionArgumentSpec<String> nonOption = parser.nonOptions();
        OptionSet options = parser.parse(args);
        minecraftHome = options.valueOf(gameDirOption);
        assetsDir = options.valueOf(assetsDirOption);
        String profileName = options.valueOf(profileOption);
        ArrayList<String> tweakClassNames = new ArrayList<String>(options.valuesOf(tweakClassOption));
        ArrayList<String> argumentList = new ArrayList<String>();
        blackboard.put("TweakClasses", tweakClassNames);
        blackboard.put("ArgumentList", argumentList);
        HashSet<String> allTweakerNames = new HashSet<String>();
        ArrayList<Object> allTweakers = new ArrayList<Object>();
        try {
            ArrayList<ITweaker> tweakers = new ArrayList<ITweaker>(tweakClassNames.size() + 1);
            blackboard.put("Tweaks", tweakers);
            ITweaker primaryTweaker = null;
            do {
                Iterator iterator = tweakClassNames.iterator();
                while (iterator.hasNext()) {
                    String tweakName = (String)iterator.next();
                    if (allTweakerNames.contains(tweakName)) {
                        LogWrapper.log(Level.WARN, "Tweak class name %s has already been visited -- skipping", tweakName);
                        iterator.remove();
                        continue;
                    }
                    allTweakerNames.add(tweakName);
                    LogWrapper.log(Level.INFO, "Loading tweak class name %s", tweakName);
                    classLoader.addClassLoaderExclusion(tweakName.substring(0, tweakName.lastIndexOf(46)));
                    ITweaker tweaker = (ITweaker)Class.forName(tweakName, true, classLoader).newInstance();
                    tweakers.add(tweaker);
                    iterator.remove();
                    if (primaryTweaker != null) continue;
                    LogWrapper.log(Level.INFO, "Using primary tweak class name %s", tweakName);
                    primaryTweaker = tweaker;
                }
                Iterator iterator2 = tweakers.iterator();
                while (iterator2.hasNext()) {
                    ITweaker tweaker = (ITweaker)iterator2.next();
                    LogWrapper.log(Level.INFO, "Calling tweak class %s", tweaker.getClass().getName());
                    tweaker.acceptOptions(options.valuesOf(nonOption), minecraftHome, assetsDir, profileName);
                    tweaker.injectIntoClassLoader(classLoader);
                    allTweakers.add(tweaker);
                    iterator2.remove();
                }
            } while (!tweakClassNames.isEmpty());
            for (ITweaker iTweaker : allTweakers) {
                argumentList.addAll(Arrays.asList(iTweaker.getLaunchArguments()));
            }
            String string = primaryTweaker.getLaunchTarget();
            Class<?> clazz = Class.forName(string, false, classLoader);
            Method mainMethod = clazz.getMethod("main", String[].class);
            LogWrapper.info("Launching wrapped minecraft {%s}", string);
            mainMethod.invoke(null, new Object[]{argumentList.toArray(new String[argumentList.size()])});
        }
        catch (Exception e2) {
            LogWrapper.log(Level.ERROR, e2, "Unable to launch", new Object[0]);
            System.exit(1);
        }
    }
}

