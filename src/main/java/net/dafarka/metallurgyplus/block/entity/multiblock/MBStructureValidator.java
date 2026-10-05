package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class MBStructureValidator {
    private MBStructureValidator() {
    }

    static Optional<MBStructure> find(LevelReader level, BlockPos controllerPos, Direction facing, MBStructureDefinition definition) {
        if (facing.getAxis() == Direction.Axis.Y || !level.hasChunkAt(controllerPos)) {
            return Optional.empty();
        }

        Direction lateral = facing.getClockWise();
        int negativeWidth = findEdgeDistance(level, controllerPos, lateral.getOpposite(), definition);
        int positiveWidth = findEdgeDistance(level, controllerPos, lateral, definition);
        int below = findEdgeDistance(level, controllerPos, Direction.DOWN, definition);
        int above = findEdgeDistance(level, controllerPos, Direction.UP, definition);

        if (negativeWidth < 1 || positiveWidth < 1 || below < 1 || above < 1) {
            return Optional.empty();
        }

        int width = negativeWidth + positiveWidth + 1;
        int height = below + above + 1;
        if (!isAllowedSize(width, definition) || !isAllowedSize(height, definition)) {
            return Optional.empty();
        }

        for (int depth = definition.minimumSize(); depth <= definition.maximumSize(); depth++) {
            BlockPos[] corners = getCorners(controllerPos, lateral, negativeWidth, positiveWidth, below, above, facing.getOpposite(), depth - 1);
            BlockPos minimum = minimum(corners);
            BlockPos maximum = maximum(corners);

            Optional<MBStructure> structure = validateCandidate(level, controllerPos, facing, minimum, maximum, definition);
            if (structure.isPresent()) {
                return structure;
            }
        }

        return Optional.empty();
    }

    private static int findEdgeDistance(LevelReader level, BlockPos origin, Direction direction, MBStructureDefinition definition) {
        for (int distance = 1; distance < definition.maximumSize() - 1; distance++) {
            BlockPos pos = origin.relative(direction, distance);
            if (!level.hasChunkAt(pos)) {
                return -1;
            }

            if (definition.edgeMatcher().test(level.getBlockState(pos))) {
                return distance;
            }
        }

        return -1;
    }

    private static boolean isAllowedSize(int size, MBStructureDefinition definition) {
        return size >= definition.minimumSize() && size <= definition.maximumSize();
    }

    private static BlockPos[] getCorners(
        BlockPos controllerPos,
        Direction lateral,
        int negativeWidth,
        int positiveWidth,
        int below,
        int above,
        Direction inward,
        int depth
    ) {
        BlockPos[] corners = new BlockPos[8];
        int index = 0;

        for (int lateralOffset : new int[]{-negativeWidth, positiveWidth}) {
            for (int verticalOffset : new int[]{-below, above}) {
                for (int depthOffset : new int[]{0, depth}) {
                    corners[index++] = controllerPos
                        .relative(lateral, lateralOffset)
                        .relative(Direction.UP, verticalOffset)
                        .relative(inward, depthOffset);
                }
            }
        }

        return corners;
    }

    private static BlockPos minimum(BlockPos[] positions) {
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;
        for (BlockPos pos : positions) {
            x = Math.min(x, pos.getX());
            y = Math.min(y, pos.getY());
            z = Math.min(z, pos.getZ());
        }
        return new BlockPos(x, y, z);
    }

    private static BlockPos maximum(BlockPos[] positions) {
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
        for (BlockPos pos : positions) {
            x = Math.max(x, pos.getX());
            y = Math.max(y, pos.getY());
            z = Math.max(z, pos.getZ());
        }
        return new BlockPos(x, y, z);
    }

    private static Optional<MBStructure> validateCandidate(
        LevelReader level,
        BlockPos controllerPos,
        Direction facing,
        BlockPos minimum,
        BlockPos maximum,
        MBStructureDefinition definition
    ) {
        int sizeX = maximum.getX() - minimum.getX() + 1;
        int sizeY = maximum.getY() - minimum.getY() + 1;
        int sizeZ = maximum.getZ() - minimum.getZ() + 1;
        if (!isAllowedSize(sizeX, definition) || !isAllowedSize(sizeY, definition) || !isAllowedSize(sizeZ, definition)) {
            return Optional.empty();
        }

        List<BlockPos> wallPositions = new ArrayList<>();
        List<BlockPos> requiredInteriorPositions = new ArrayList<>();
        int controllerCount = 0;

        for (int y = minimum.getY(); y <= maximum.getY(); y++) {
            for (int z = minimum.getZ(); z <= maximum.getZ(); z++) {
                for (int x = minimum.getX(); x <= maximum.getX(); x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.hasChunkAt(pos)) {
                        return Optional.empty();
                    }

                    BlockState state = level.getBlockState(pos);
                    boolean xBoundary = x == minimum.getX() || x == maximum.getX();
                    boolean yBoundary = y == minimum.getY() || y == maximum.getY();
                    boolean zBoundary = z == minimum.getZ() || z == maximum.getZ();
                    int boundaryCount = (xBoundary ? 1 : 0) + (yBoundary ? 1 : 0) + (zBoundary ? 1 : 0);
                    boolean isController = definition.controllerMatcher().test(state);

                    if (isController) {
                        controllerCount++;
                        if (!pos.equals(controllerPos) || boundaryCount != 1) {
                            return Optional.empty();
                        }
                    }

                    if (boundaryCount >= 2) {
                        if (!definition.edgeMatcher().test(state)) {
                            return Optional.empty();
                        }
                    } else if (boundaryCount == 1) {
                        if (!definition.wallMatcher().test(state)) {
                            return Optional.empty();
                        }
                        wallPositions.add(pos.immutable());
                    } else {
                        if (!definition.interiorMatcher().test(state)) {
                            return Optional.empty();
                        }
                        if (definition.requiredInteriorMatcher().test(state)) {
                            requiredInteriorPositions.add(pos.immutable());
                        }
                    }
                }
            }
        }

        if (controllerCount != 1) {
            return Optional.empty();
        }
        if (requiredInteriorPositions.size() < definition.minimumRequiredInteriorBlocks()) {
            return Optional.empty();
        }

        return Optional.of(new MBStructure(minimum, maximum, facing, wallPositions, requiredInteriorPositions));
    }
}
