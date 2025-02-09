// 
// Decompiled by Procyon v0.6.0
// 

package joptsimple;

import java.util.List;
import joptsimple.internal.Messages;
import java.util.Locale;
import java.util.Collections;

class AlternativeLongOptionSpec extends ArgumentAcceptingOptionSpec<String>
{
    AlternativeLongOptionSpec() {
        super(Collections.singletonList("W"), true, Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "description", new Object[0]));
        this.describedAs(Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, "arg.description", new Object[0]));
    }
    
    @Override
    protected void detectOptionArgument(final OptionParser parser, final ArgumentList arguments, final OptionSet detectedOptions) {
        if (!arguments.hasMore()) {
            throw new OptionMissingRequiredArgumentException(this);
        }
        arguments.treatNextAsLongOption();
    }
}
