package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Rules for an axis-aligned rectangular-prism multiblock. The footprint is
 * bounded independently from the vertical height.
 */
public final class MBStructureDefinition {
    private final int minimumBaseSize;
    private final int maximumBaseSize;
    private final int minimumHeight;
    private final int maximumHeight;
    private final Predicate<BlockState> edgeMatcher;
    private final Predicate<BlockState> wallMatcher;
    private final Predicate<BlockState> interiorMatcher;
    private final Predicate<BlockState> controllerMatcher;
    private final Predicate<BlockState> requiredInteriorMatcher;
    private final int minimumRequiredInteriorBlocks;

    private MBStructureDefinition(Builder builder) {
        this.minimumBaseSize = builder.minimumBaseSize;
        this.maximumBaseSize = builder.maximumBaseSize;
        this.minimumHeight = builder.minimumHeight;
        this.maximumHeight = builder.maximumHeight;
        this.edgeMatcher = Objects.requireNonNull(builder.edgeMatcher, "An edge matcher is required");
        this.wallMatcher = Objects.requireNonNull(builder.wallMatcher, "A wall matcher is required");
        this.interiorMatcher = builder.interiorMatcher;
        this.controllerMatcher = Objects.requireNonNull(builder.controllerMatcher, "A controller matcher is required");
        this.requiredInteriorMatcher = builder.requiredInteriorMatcher;
        this.minimumRequiredInteriorBlocks = builder.minimumRequiredInteriorBlocks;
    }

    /** Creates a prism with the same size range for its footprint and height. */
    public static Builder rectangularPrism(int minimumSize, int maximumSize) {
        return rectangularPrism(minimumSize, maximumSize, minimumSize, maximumSize);
    }

    /**
     * Creates a prism whose horizontal footprint and vertical height have
     * independent size ranges. The base bounds apply to both horizontal axes.
     */
    public static Builder rectangularPrism(
        int minimumBaseSize,
        int maximumBaseSize,
        int minimumHeight,
        int maximumHeight
    ) {
        return new Builder(minimumBaseSize, maximumBaseSize, minimumHeight, maximumHeight);
    }

    int minimumBaseSize() {
        return minimumBaseSize;
    }

    int maximumBaseSize() {
        return maximumBaseSize;
    }

    int minimumHeight() {
        return minimumHeight;
    }

    int maximumHeight() {
        return maximumHeight;
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
        private final int minimumBaseSize;
        private final int maximumBaseSize;
        private final int minimumHeight;
        private final int maximumHeight;
        private Predicate<BlockState> edgeMatcher;
        private Predicate<BlockState> wallMatcher;
        private Predicate<BlockState> interiorMatcher = state -> true;
        private Predicate<BlockState> controllerMatcher;
        private Predicate<BlockState> requiredInteriorMatcher = state -> false;
        private int minimumRequiredInteriorBlocks;

        private Builder(int minimumBaseSize, int maximumBaseSize, int minimumHeight, int maximumHeight) {
            if (minimumBaseSize < 2 || maximumBaseSize < minimumBaseSize) {
                throw new IllegalArgumentException("Base sizes must satisfy 2 <= minimum <= maximum");
            }
            if (minimumHeight < 2 || maximumHeight < minimumHeight) {
                throw new IllegalArgumentException("Height must satisfy 2 <= minimum <= maximum");
            }

            this.minimumBaseSize = minimumBaseSize;
            this.maximumBaseSize = maximumBaseSize;
            this.minimumHeight = minimumHeight;
            this.maximumHeight = maximumHeight;
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
