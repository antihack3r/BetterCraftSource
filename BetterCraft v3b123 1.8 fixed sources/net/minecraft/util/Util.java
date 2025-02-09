// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.FutureTask;

public class Util
{
    public static EnumOS getOSType() {
        final String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? EnumOS.WINDOWS : (s.contains("mac") ? EnumOS.OSX : (s.contains("solaris") ? EnumOS.SOLARIS : (s.contains("sunos") ? EnumOS.SOLARIS : (s.contains("linux") ? EnumOS.LINUX : (s.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN)))));
    }
    
    public static <V> V runTask(final FutureTask<V> task, final Logger logger) {
        try {
            task.run();
            return task.get();
        }
        catch (final ExecutionException executionexception) {
            logger.fatal("Error executing task", executionexception);
            if (executionexception.getCause() instanceof OutOfMemoryError) {
                final OutOfMemoryError outofmemoryerror = (OutOfMemoryError)executionexception.getCause();
                throw outofmemoryerror;
            }
        }
        catch (final InterruptedException interruptedexception) {
            logger.fatal("Error executing task", interruptedexception);
        }
        return null;
    }
    
    public enum EnumOS
    {
        LINUX("LINUX", 0), 
        SOLARIS("SOLARIS", 1), 
        WINDOWS("WINDOWS", 2), 
        OSX("OSX", 3), 
        UNKNOWN("UNKNOWN", 4);
        
        private EnumOS(final String s, final int n) {
        }
    }
}
