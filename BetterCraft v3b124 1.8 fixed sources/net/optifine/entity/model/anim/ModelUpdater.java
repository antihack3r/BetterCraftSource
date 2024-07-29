/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.ModelVariableUpdater;

public class ModelUpdater {
    private ModelVariableUpdater[] modelVariableUpdaters;

    public ModelUpdater(ModelVariableUpdater[] modelVariableUpdaters) {
        this.modelVariableUpdaters = modelVariableUpdaters;
    }

    public void update() {
        int i2 = 0;
        while (i2 < this.modelVariableUpdaters.length) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i2];
            modelvariableupdater.update();
            ++i2;
        }
    }

    public boolean initialize(IModelResolver mr2) {
        int i2 = 0;
        while (i2 < this.modelVariableUpdaters.length) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i2];
            if (!modelvariableupdater.initialize(mr2)) {
                return false;
            }
            ++i2;
        }
        return true;
    }
}

