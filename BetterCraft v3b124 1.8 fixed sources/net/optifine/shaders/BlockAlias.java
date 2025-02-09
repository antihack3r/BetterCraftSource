/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.src.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias {
    private int blockAliasId;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int blockAliasId, MatchBlock[] matchBlocks) {
        this.blockAliasId = blockAliasId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockAliasId() {
        return this.blockAliasId;
    }

    public boolean matches(int id2, int metadata) {
        int i2 = 0;
        while (i2 < this.matchBlocks.length) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.matches(id2, metadata)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public int[] getMatchBlockIds() {
        HashSet<Integer> set = new HashSet<Integer>();
        int i2 = 0;
        while (i2 < this.matchBlocks.length) {
            MatchBlock matchblock = this.matchBlocks[i2];
            int j2 = matchblock.getBlockId();
            set.add(j2);
            ++i2;
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = Config.toPrimitive(ainteger);
        return aint;
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId) {
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        int i2 = 0;
        while (i2 < this.matchBlocks.length) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.getBlockId() == matchBlockId) {
                list.add(matchblock);
            }
            ++i2;
        }
        MatchBlock[] amatchblock = list.toArray(new MatchBlock[list.size()]);
        return amatchblock;
    }

    public String toString() {
        return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
    }
}

