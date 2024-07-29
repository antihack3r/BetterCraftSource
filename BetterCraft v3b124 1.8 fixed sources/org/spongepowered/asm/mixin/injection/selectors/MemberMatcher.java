/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.selectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.spongepowered.asm.mixin.injection.selectors.ElementNode;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;
import org.spongepowered.asm.mixin.injection.selectors.MatchResult;

public final class MemberMatcher
implements ITargetSelector {
    private static final Pattern PATTERN = Pattern.compile("((owner|name|desc)\\s*=\\s*)?/(.*?)(?<!\\\\)/");
    private static final String[] PATTERN_SOURCE_NAMES = new String[]{"owner", "name", "desc"};
    private static final int SOURCE_OWNER = 0;
    private static final int SOURCE_NAME = 1;
    private static final int SOURCE_DESC = 2;
    private final Pattern[] patterns;
    private final Exception parseException;
    private final String input;

    private MemberMatcher(Pattern[] patterns, Exception parseException, String input) {
        this.patterns = patterns;
        this.parseException = parseException;
        this.input = input;
    }

    public static MemberMatcher parse(String input, ISelectorContext context) {
        Matcher matcher = PATTERN.matcher(input);
        Pattern[] patterns = new Pattern[3];
        RuntimeException parseException = null;
        while (matcher.find()) {
            int patternId;
            Pattern pattern;
            try {
                pattern = Pattern.compile(matcher.group(3));
            }
            catch (PatternSyntaxException ex2) {
                parseException = ex2;
                pattern = Pattern.compile(".*");
                ex2.printStackTrace();
            }
            int n2 = "owner".equals(matcher.group(2)) ? 0 : (patternId = "desc".equals(matcher.group(2)) ? 2 : 1);
            if (patterns[patternId] != null) {
                parseException = new InvalidSelectorException("Pattern for '" + PATTERN_SOURCE_NAMES[patternId] + "' specified multiple times: Old=/" + patterns[patternId].pattern() + "/ New=/" + pattern.pattern() + "/");
            }
            patterns[patternId] = pattern;
        }
        return new MemberMatcher(patterns, parseException, input);
    }

    @Override
    public ITargetSelector validate() throws InvalidSelectorException {
        if (this.parseException != null) {
            if (this.parseException instanceof InvalidSelectorException) {
                throw (InvalidSelectorException)this.parseException;
            }
            throw new InvalidSelectorException("Error parsing regex selector", this.parseException);
        }
        boolean validPattern = false;
        for (Pattern pattern : this.patterns) {
            validPattern |= pattern != null;
        }
        if (!validPattern) {
            throw new InvalidSelectorException("Error parsing regex selector, the input was in an unexpected format: " + this.input);
        }
        return this;
    }

    public String toString() {
        return this.input;
    }

    @Override
    public ITargetSelector next() {
        return this;
    }

    @Override
    public ITargetSelector configure(ITargetSelector.Configure request, String ... args) {
        request.checkArgs(args);
        return this;
    }

    @Override
    public ITargetSelector attach(ISelectorContext context) throws InvalidSelectorException {
        return this;
    }

    @Override
    public int getMinMatchCount() {
        return 0;
    }

    @Override
    public int getMaxMatchCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <TNode> MatchResult match(ElementNode<TNode> node) {
        return node == null ? MatchResult.NONE : this.matches(node.getOwner(), node.getName(), node.getDesc());
    }

    private MatchResult matches(String ... args) {
        MatchResult result = MatchResult.NONE;
        for (int i2 = 0; i2 < this.patterns.length; ++i2) {
            if (this.patterns[i2] == null || args[i2] == null) continue;
            result = this.patterns[i2].matcher(args[i2]).find() ? MatchResult.EXACT_MATCH : MatchResult.NONE;
        }
        return result;
    }
}

