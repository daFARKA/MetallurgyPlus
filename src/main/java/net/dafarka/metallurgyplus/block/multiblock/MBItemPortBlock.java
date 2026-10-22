package net.dafarka.metallurgyplus.block.multiblock;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBItemPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MBItemPortBlock extends MBPortBlock implements EntityBlock {
    private final Supplier<? extends BlockEntityType<MBItemPortBlockEntity>> blockEntityType;
    private final boolean inputPort;

    public MBItemPortBlock(
        Properties pProperties,
        Supplier<? extends BlockEntityType<MBItemPortBlockEntity>> blockEntityType,
        boolean inputPort
    ) {
        super(pProperties, blockEntityType);
        this.blockEntityType = blockEntityType;
        this.inputPort = inputPort;
    }

    public boolean isInputPort() {
        return inputPort;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }
}
