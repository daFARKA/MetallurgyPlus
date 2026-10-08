package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Rules for an axis-aligned rectangular-prism multiblock.
 */
public final class MBStructureDefinition {
    private final int minimumSize;
    private final int maximumSize;
    private final Predicate<BlockState> edgeMatcher;
    private final Predicate<BlockState> wallMatcher;
    private final Predicate<BlockState> interiorMatcher;
    private final Predicate<BlockState> controllerMatcher;
    private final Predicate<BlockState> requiredInteriorMatcher;
    private final int minimumRequiredInteriorBlocks;

    private MBStructureDefinition(Builder builder) {
        this.minimumSize = builder.minimumSize;
        this.maximumSize = builder.maximumSize;
        this.edgeMatcher = Objects.requireNonNull(builder.edgeMatcher, "An edge matcher is required");
        this.wallMatcher = Objects.requireNonNull(builder.wallMatcher, "A wall matcher is required");
        this.interiorMatcher = builder.interiorMatcher;
        this.controllerMatcher = Objects.requireNonNull(builder.controllerMatcher, "A controller matcher is required");
        this.requiredInteriorMatcher = builder.requiredInteriorMatcher;
        this.minimumRequiredInteriorBlocks = builder.minimumRequiredInteriorBlocks;
    }

    public static Builder rectangularPrism(int minimumSize, int maximumSize) {
        return new Builder(minimumSize, maximumSize);
    }

    int minimumSize() {
        return minimumSize;
    }

    int maximumSize() {
        return maximumSize;
    }

    Predicate<BlockState> edgeMatcher() {
        return edgeMatcher;
    }

    Predicate<BlockState> wallMatcher() {
        return wallMatcher;
    }

    Predicate<BlockState> interiorMatcher() {
        return interiorMatcher;
    }

    Predicate<BlockState> controllerMatcher() {
        return controllerMatcher;
    }

    Predicate<BlockState> requiredInteriorMatcher() {
        return requiredInteriorMatcher;
    }

    int minimumRequiredInteriorBlocks() {
        return minimumRequiredInteriorBlocks;
    }

    public static final class Builder {
        private final int minimumSize;
        private final int maximumSize;
        private Predicate<BlockState> edgeMatcher;
        private Predicate<BlockState> wallMatcher;
        private Predicate<BlockState> interiorMatcher = state -> true;
        private Predicate<BlockState> controllerMatcher;
        private Predicate<BlockState> requiredInteriorMatcher = state -> false;
        private int minimumRequiredInteriorBlocks;

        private Builder(int minimumSize, int maximumSize) {
            if (minimumSize < 2 || maximumSize < minimumSize) {
                throw new IllegalArgumentException("Prism sizes must satisfy 2 <= minimum <= maximum");
            }

            this.minimumSize = minimumSize;
            this.maximumSize = maximumSize;
        }

        public Builder edges(Predicate<BlockState> matcher) {
            this.edgeMatcher = Objects.requireNonNull(matcher);
            return this;
        }

        public Builder walls(Predicate<BlockState> matcher) {
            this.wallMatcher = Objects.requireNonNull(matcher);
            return this;
        }

        public Builder interior(Predicate<BlockState> matcher) {
            this.interiorMatcher = Objects.requireNonNull(matcher);
            return this;
        }

        public Builder controller(Predicate<BlockState> matcher) {
            this.controllerMatcher = Objects.requireNonNull(matcher);
            return this;
        }

        public Builder requireInterior(Predicate<BlockState> matcher, int minimumCount) {
            if (minimumCount < 0) {
                throw new IllegalArgumentException("Minimum required interior block count cannot be negative");
            }

            this.requiredInteriorMatcher = Objects.requireNonNull(matcher);
            this.minimumRequiredInteriorBlocks = minimumCount;
            return this;
        }

        public MBStructureDefinition build() {
            return new MBStructureDefinition(this);
        }
    }
}
