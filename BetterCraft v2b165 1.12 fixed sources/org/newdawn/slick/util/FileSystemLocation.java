// 
// Decompiled by Procyon v0.6.0
// 

package org.newdawn.slick.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.io.File;

public class FileSystemLocation implements ResourceLocation
{
    private File root;
    
    public FileSystemLocation(final File root) {
        this.root = root;
    }
    
    @Override
    public URL getResource(final String ref) {
        try {
            File file = new File(this.root, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            if (!file.exists()) {
                return null;
            }
            return file.toURI().toURL();
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String ref) {
        try {
            File file = new File(this.root, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            return new FileInputStream(file);
        }
        catch (final IOException e) {
            return null;
        }
    }
}
