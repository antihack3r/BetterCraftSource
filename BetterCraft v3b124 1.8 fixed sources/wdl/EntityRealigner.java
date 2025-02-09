/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import net.minecraft.entity.Entity;
import wdl.api.IEntityEditor;
import wdl.api.IWDLModDescripted;

public class EntityRealigner
implements IEntityEditor,
IWDLModDescripted {
    @Override
    public boolean isValidEnvironment(String version) {
        return true;
    }

    @Override
    public String getEnvironmentErrorMessage(String version) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Entity realigner";
    }

    @Override
    public String getMainAuthor() {
        return "Pokechu22";
    }

    @Override
    public String[] getAuthors() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Realigns entities to their serverside position to deal with entities that drift clientside (for example, boats).";
    }

    @Override
    public boolean shouldEdit(Entity e2) {
        return e2.serverPosX != 0 || e2.serverPosY != 0 || e2.serverPosZ != 0;
    }

    @Override
    public void editEntity(Entity e2) {
        System.out.println("Realigning " + e2);
        e2.posX = EntityRealigner.convertServerPos(e2.serverPosX);
        e2.posY = EntityRealigner.convertServerPos(e2.serverPosY);
        e2.posZ = EntityRealigner.convertServerPos(e2.serverPosZ);
        System.out.println("Realigned " + e2);
    }

    private static double convertServerPos(int serverPos) {
        return (double)serverPos / 32.0;
    }
}

