// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import java.util.List;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import com.google.common.base.Function;
import net.minecraft.block.state.IBlockState;
import com.google.common.base.Predicate;
import net.minecraft.block.state.BlockStateContainer;
import com.google.common.base.Splitter;

public class ConditionPropertyValue implements ICondition
{
    private static final Splitter SPLITTER;
    private final String key;
    private final String value;
    
    static {
        SPLITTER = Splitter.on('|').omitEmptyStrings();
    }
    
    public ConditionPropertyValue(final String keyIn, final String valueIn) {
        this.key = keyIn;
        this.value = valueIn;
    }
    
    @Override
    public Predicate<IBlockState> getPredicate(final BlockStateContainer blockState) {
        final IProperty<?> iproperty = blockState.getProperty(this.key);
        if (iproperty == null) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": Definition: " + blockState + " has no property: " + this.key);
        }
        String s = this.value;
        final boolean flag = !s.isEmpty() && s.charAt(0) == '!';
        if (flag) {
            s = s.substring(1);
        }
        final List<String> list = ConditionPropertyValue.SPLITTER.splitToList(s);
        if (list.isEmpty()) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": has an empty value: " + this.value);
        }
        Predicate<IBlockState> predicate;
        if (list.size() == 1) {
            predicate = this.makePredicate(iproperty, s);
        }
        else {
            predicate = Predicates.or(Iterables.transform((Iterable<String>)list, (Function<? super String, ? extends Predicate<? super IBlockState>>)new Function<String, Predicate<IBlockState>>() {
                @Nullable
                @Override
                public Predicate<IBlockState> apply(@Nullable final String p_apply_1_) {
                    return ConditionPropertyValue.this.makePredicate(iproperty, p_apply_1_);
                }
            }));
        }
        return flag ? Predicates.not(predicate) : predicate;
    }
    
    private Predicate<IBlockState> makePredicate(final IProperty<?> property, final String valueIn) {
        final Optional<?> optional = property.parseValue(valueIn);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": has an unknown value: " + this.value);
        }
        return new Predicate<IBlockState>() {
            @Override
            public boolean apply(@Nullable final IBlockState p_apply_1_) {
                return p_apply_1_ != null && p_apply_1_.getValue(property).equals(optional.get());
            }
        };
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
    }
}
