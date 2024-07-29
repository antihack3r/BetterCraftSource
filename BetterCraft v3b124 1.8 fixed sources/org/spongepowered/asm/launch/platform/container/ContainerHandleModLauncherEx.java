/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.jarhandling.SecureJar
 */
package org.spongepowered.asm.launch.platform.container;

import cpw.mods.jarhandling.SecureJar;
import java.nio.file.Path;
import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;

public class ContainerHandleModLauncherEx
extends ContainerHandleModLauncher {
    public ContainerHandleModLauncherEx(String name) {
        super(name);
    }

    @Override
    public void addResource(Object resource) {
        if (resource instanceof SecureJar) {
            this.add(new SecureJarResource((SecureJar)resource));
        } else {
            super.addResource(resource);
        }
    }

    static class SecureJarResource
    extends ContainerHandleURI {
        private SecureJar jar;

        public SecureJarResource(SecureJar resource) {
            super(resource.getPrimaryPath().toUri());
            this.jar = resource;
        }

        public String getName() {
            return this.jar.name();
        }

        public Path getPath() {
            return this.jar.getPrimaryPath();
        }

        @Override
        public String toString() {
            return String.format("SecureJarResource(%s)", this.getName());
        }
    }
}

