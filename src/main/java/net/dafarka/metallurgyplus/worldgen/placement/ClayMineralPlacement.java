package net.dafarka.metallurgyplus.worldgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class ClayMineralPlacement extends PlacementModifier {

    public static final Codec<ClayMineralPlacement> CODEC =
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("attempts").forGetter(p -> p.attempts),
            Codec.INT.fieldOf("min_y").forGetter(p -> p.minY),
            Codec.INT.fieldOf("max_y").forGetter(p -> p.maxY)
        ).apply(instance, ClayMineralPlacement::new));

    private final int attempts;
    private final int minY;
    private final int maxY;

    public ClayMineralPlacement(int attempts, int minY, int maxY) {
        this.attempts = attempts;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos origin) {
        Stream.Builder<BlockPos> positions = Stream.builder();

        for (int i = 0; i < attempts; i++) {
            int x = origin.getX() + random.nextInt(16);
            int z = origin.getZ() + random.nextInt(16);
            int y = random.nextIntBetweenInclusive(minY, maxY);

            BlockPos pos = new BlockPos(x, y, z);

            if (context.getBlockState(pos).is(Blocks.CLAY)) {
                positions.add(pos);
            }
        }

        return positions.build();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.CLAY_MINERAL.get();
    }
}