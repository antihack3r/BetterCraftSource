// 
// Decompiled by Procyon v0.6.0
// 

package org.objectweb.asm.tree;

import org.objectweb.asm.ModuleVisitor;
import java.util.List;

public class ModuleOpenNode
{
    public String packaze;
    public int access;
    public List<String> modules;
    
    public ModuleOpenNode(final String packaze, final int access, final List<String> modules) {
        this.packaze = packaze;
        this.access = access;
        this.modules = modules;
    }
    
    public void accept(final ModuleVisitor moduleVisitor) {
        moduleVisitor.visitOpen(this.packaze, this.access, (String[])((this.modules == null) ? null : ((String[])this.modules.toArray(new String[0]))));
    }
}
