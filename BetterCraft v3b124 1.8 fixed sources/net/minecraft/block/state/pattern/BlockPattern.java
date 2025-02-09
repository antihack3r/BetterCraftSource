/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
    private final Predicate<BlockWorldState>[][][] blockMatches;
    private final int fingerLength;
    private final int thumbLength;
    private final int palmLength;

    public BlockPattern(Predicate<BlockWorldState>[][][] predicatesIn) {
        this.blockMatches = predicatesIn;
        this.fingerLength = predicatesIn.length;
        if (this.fingerLength > 0) {
            this.thumbLength = predicatesIn[0].length;
            this.palmLength = this.thumbLength > 0 ? predicatesIn[0][0].length : 0;
        } else {
            this.thumbLength = 0;
            this.palmLength = 0;
        }
    }

    public int getThumbLength() {
        return this.thumbLength;
    }

    public int getPalmLength() {
        return this.palmLength;
    }

    private PatternHelper checkPatternAt(BlockPos pos, EnumFacing finger, EnumFacing thumb, LoadingCache<BlockPos, BlockWorldState> lcache) {
        int i2 = 0;
        while (i2 < this.palmLength) {
            int j2 = 0;
            while (j2 < this.thumbLength) {
                int k2 = 0;
                while (k2 < this.fingerLength) {
                    if (!this.blockMatches[k2][j2][i2].apply(lcache.getUnchecked(BlockPattern.translateOffset(pos, finger, thumb, i2, j2, k2)))) {
                        return null;
                    }
                    ++k2;
                }
                ++j2;
            }
            ++i2;
        }
        return new PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
    }

    public PatternHelper match(World worldIn, BlockPos pos) {
        LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.func_181627_a(worldIn, false);
        int i2 = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);
        for (BlockPos blockpos : BlockPos.getAllInBox(pos, pos.add(i2 - 1, i2 - 1, i2 - 1))) {
            EnumFacing[] enumFacingArray = EnumFacing.values();
            int n2 = enumFacingArray.length;
            int n3 = 0;
            while (n3 < n2) {
                EnumFacing enumfacing = enumFacingArray[n3];
                EnumFacing[] enumFacingArray2 = EnumFacing.values();
                int n4 = enumFacingArray2.length;
                int n5 = 0;
                while (n5 < n4) {
                    PatternHelper blockpattern$patternhelper;
                    EnumFacing enumfacing1 = enumFacingArray2[n5];
                    if (enumfacing1 != enumfacing && enumfacing1 != enumfacing.getOpposite() && (blockpattern$patternhelper = this.checkPatternAt(blockpos, enumfacing, enumfacing1, loadingcache)) != null) {
                        return blockpattern$patternhelper;
                    }
                    ++n5;
                }
                ++n3;
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, BlockWorldState> func_181627_a(World p_181627_0_, boolean p_181627_1_) {
        return CacheBuilder.newBuilder().build(new CacheLoader(p_181627_0_, p_181627_1_));
    }

    protected static BlockPos translateOffset(BlockPos pos, EnumFacing finger, EnumFacing thumb, int palmOffset, int thumbOffset, int fingerOffset) {
        if (finger != thumb && finger != thumb.getOpposite()) {
            Vec3i vec3i = new Vec3i(finger.getFrontOffsetX(), finger.getFrontOffsetY(), finger.getFrontOffsetZ());
            Vec3i vec3i1 = new Vec3i(thumb.getFrontOffsetX(), thumb.getFrontOffsetY(), thumb.getFrontOffsetZ());
            Vec3i vec3i2 = vec3i.crossProduct(vec3i1);
            return pos.add(vec3i1.getX() * -thumbOffset + vec3i2.getX() * palmOffset + vec3i.getX() * fingerOffset, vec3i1.getY() * -thumbOffset + vec3i2.getY() * palmOffset + vec3i.getY() * fingerOffset, vec3i1.getZ() * -thumbOffset + vec3i2.getZ() * palmOffset + vec3i.getZ() * fingerOffset);
        }
        throw new IllegalArgumentException("Invalid forwards & up combination");
    }

    static class CacheLoader
    extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
        private final World world;
        private final boolean field_181626_b;

        public CacheLoader(World worldIn, boolean p_i46460_2_) {
            this.world = worldIn;
            this.field_181626_b = p_i46460_2_;
        }

        @Override
        public BlockWorldState load(BlockPos p_load_1_) throws Exception {
            return new BlockWorldState(this.world, p_load_1_, this.field_181626_b);
        }
    }

    public static class PatternHelper {
        private final BlockPos pos;
        private final EnumFacing finger;
        private final EnumFacing thumb;
        private final LoadingCache<BlockPos, BlockWorldState> lcache;
        private final int field_181120_e;
        private final int field_181121_f;
        private final int field_181122_g;

        public PatternHelper(BlockPos posIn, EnumFacing fingerIn, EnumFacing thumbIn, LoadingCache<BlockPos, BlockWorldState> lcacheIn, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
            this.pos = posIn;
            this.finger = fingerIn;
            this.thumb = thumbIn;
            this.lcache = lcacheIn;
            this.field_181120_e = p_i46378_5_;
            this.field_181121_f = p_i46378_6_;
            this.field_181122_g = p_i46378_7_;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public EnumFacing getFinger() {
            return this.finger;
        }

        public EnumFacing getThumb() {
            return this.thumb;
        }

        public int func_181118_d() {
            return this.field_181120_e;
        }

        public int func_181119_e() {
            return this.field_181121_f;
        }

        public BlockWorldState translateOffset(int palmOffset, int thumbOffset, int fingerOffset) {
            return this.lcache.getUnchecked(BlockPattern.translateOffset(this.pos, this.getFinger(), this.getThumb(), palmOffset, thumbOffset, fingerOffset));
        }

        public String toString() {
            return Objects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
        }
    }
}

