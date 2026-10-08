package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;

/**
 * Cached, derived information about a currently formed multiblock.
 */
public final class MBStructure {
    private final BlockPos minimum;
    private final BlockPos maximum;
    private final Direction controllerFacing;
    private final List<BlockPos> wallPositions;
    private final List<BlockPos> requiredInteriorPositions;

    MBStructure(
        BlockPos minimum,
        BlockPos maximum,
        Direction controllerFacing,
        List<BlockPos> wallPositions,
        List<BlockPos> requiredInteriorPositions
    ) {
        this.minimum = minimum.immutable();
        this.maximum = maximum.immutable();
        this.controllerFacing = controllerFacing;
        this.wallPositions = List.copyOf(wallPositions);
        this.requiredInteriorPositions = List.copyOf(requiredInteriorPositions);
    }

    public BlockPos minimum() {
        return minimum;
    }

    public BlockPos maximum() {
        return maximum;
    }

    public Direction controllerFacing() {
        return controllerFacing;
    }

    public int sizeX() {
        return maximum.getX() - minimum.getX() + 1;
    }

    public int sizeY() {
        return maximum.getY() - minimum.getY() + 1;
    }

    public int sizeZ() {
        return maximum.getZ() - minimum.getZ() + 1;
    }

    /**
     * All non-edge positions on the outer walls, including the controller.
     */
    public List<BlockPos> wallPositions() {
        return wallPositions;
    }

    /**
     * Positions matching the definition's required-interior-block rule.
     */
    public List<BlockPos> requiredInteriorPositions() {
        return requiredInteriorPositions;
    }
}
