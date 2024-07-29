/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

public class BlockPosM
extends BlockPos {
    private int mx;
    private int my;
    private int mz;
    private int level;
    private BlockPosM[] facings;
    private boolean needsUpdate;

    public BlockPosM(int x2, int y2, int z2) {
        this(x2, y2, z2, 0);
    }

    public BlockPosM(double xIn, double yIn, double zIn) {
        this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    public BlockPosM(int x2, int y2, int z2, int level) {
        super(0, 0, 0);
        this.mx = x2;
        this.my = y2;
        this.mz = z2;
        this.level = level;
    }

    @Override
    public int getX() {
        return this.mx;
    }

    @Override
    public int getY() {
        return this.my;
    }

    @Override
    public int getZ() {
        return this.mz;
    }

    public void setXyz(int x2, int y2, int z2) {
        this.mx = x2;
        this.my = y2;
        this.mz = z2;
        this.needsUpdate = true;
    }

    public void setXyz(double xIn, double yIn, double zIn) {
        this.setXyz(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    public BlockPosM set(Vec3i vec) {
        this.setXyz(vec.getX(), vec.getY(), vec.getZ());
        return this;
    }

    public BlockPosM set(int xIn, int yIn, int zIn) {
        this.setXyz(xIn, yIn, zIn);
        return this;
    }

    public BlockPos offsetMutable(EnumFacing facing) {
        return this.offset(facing);
    }

    @Override
    public BlockPos offset(EnumFacing facing) {
        int i2;
        BlockPosM blockposm;
        if (this.level <= 0) {
            return super.offset(facing, 1);
        }
        if (this.facings == null) {
            this.facings = new BlockPosM[EnumFacing.VALUES.length];
        }
        if (this.needsUpdate) {
            this.update();
        }
        if ((blockposm = this.facings[i2 = facing.getIndex()]) == null) {
            int j2 = this.mx + facing.getFrontOffsetX();
            int k2 = this.my + facing.getFrontOffsetY();
            int l2 = this.mz + facing.getFrontOffsetZ();
            this.facings[i2] = blockposm = new BlockPosM(j2, k2, l2, this.level - 1);
        }
        return blockposm;
    }

    @Override
    public BlockPos offset(EnumFacing facing, int n2) {
        return n2 == 1 ? this.offset(facing) : super.offset(facing, n2);
    }

    private void update() {
        int i2 = 0;
        while (i2 < 6) {
            BlockPosM blockposm = this.facings[i2];
            if (blockposm != null) {
                EnumFacing enumfacing = EnumFacing.VALUES[i2];
                int j2 = this.mx + enumfacing.getFrontOffsetX();
                int k2 = this.my + enumfacing.getFrontOffsetY();
                int l2 = this.mz + enumfacing.getFrontOffsetZ();
                blockposm.setXyz(j2, k2, l2);
            }
            ++i2;
        }
        this.needsUpdate = false;
    }

    public BlockPos toImmutable() {
        return new BlockPos(this.mx, this.my, this.mz);
    }

    public static Iterable getAllInBoxMutable(BlockPos from, BlockPos to2) {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to2.getX()), Math.min(from.getY(), to2.getY()), Math.min(from.getZ(), to2.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to2.getX()), Math.max(from.getY(), to2.getY()), Math.max(from.getZ(), to2.getZ()));
        return new Iterable(){

            public Iterator iterator() {
                return new AbstractIterator(){
                    private BlockPosM theBlockPosM = null;

                    protected BlockPosM computeNext0() {
                        if (this.theBlockPosM == null) {
                            this.theBlockPosM = new BlockPosM(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 3);
                            return this.theBlockPosM;
                        }
                        if (this.theBlockPosM.equals(blockpos1)) {
                            return (BlockPosM)this.endOfData();
                        }
                        int i2 = this.theBlockPosM.getX();
                        int j2 = this.theBlockPosM.getY();
                        int k2 = this.theBlockPosM.getZ();
                        if (i2 < blockpos1.getX()) {
                            ++i2;
                        } else if (j2 < blockpos1.getY()) {
                            i2 = blockpos.getX();
                            ++j2;
                        } else if (k2 < blockpos1.getZ()) {
                            i2 = blockpos.getX();
                            j2 = blockpos.getY();
                            ++k2;
                        }
                        this.theBlockPosM.setXyz(i2, j2, k2);
                        return this.theBlockPosM;
                    }

                    protected Object computeNext() {
                        return this.computeNext0();
                    }
                };
            }
        };
    }
}

