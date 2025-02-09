// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;

public class BlockStatePaletteLinear implements IBlockStatePalette
{
    private final IBlockState[] states;
    private final IBlockStatePaletteResizer resizeHandler;
    private final int bits;
    private int arraySize;
    
    public BlockStatePaletteLinear(final int p_i47088_1_, final IBlockStatePaletteResizer p_i47088_2_) {
        this.states = new IBlockState[1 << p_i47088_1_];
        this.bits = p_i47088_1_;
        this.resizeHandler = p_i47088_2_;
    }
    
    @Override
    public int idFor(final IBlockState state) {
        for (int i = 0; i < this.arraySize; ++i) {
            if (this.states[i] == state) {
                return i;
            }
        }
        final int j = this.arraySize;
        if (j < this.states.length) {
            this.states[j] = state;
            ++this.arraySize;
            return j;
        }
        return this.resizeHandler.onResize(this.bits + 1, state);
    }
    
    @Nullable
    @Override
    public IBlockState getBlockState(final int indexKey) {
        return (indexKey >= 0 && indexKey < this.arraySize) ? this.states[indexKey] : null;
    }
    
    @Override
    public void read(final PacketBuffer buf) {
        this.arraySize = buf.readVarIntFromBuffer();
        for (int i = 0; i < this.arraySize; ++i) {
            this.states[i] = Block.BLOCK_STATE_IDS.getByValue(buf.readVarIntFromBuffer());
        }
    }
    
    @Override
    public void write(final PacketBuffer buf) {
        buf.writeVarIntToBuffer(this.arraySize);
        for (int i = 0; i < this.arraySize; ++i) {
            buf.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.states[i]));
        }
    }
    
    @Override
    public int getSerializedState() {
        int i = PacketBuffer.getVarIntSize(this.arraySize);
        for (int j = 0; j < this.arraySize; ++j) {
            i += PacketBuffer.getVarIntSize(Block.BLOCK_STATE_IDS.get(this.states[j]));
        }
        return i;
    }
}
