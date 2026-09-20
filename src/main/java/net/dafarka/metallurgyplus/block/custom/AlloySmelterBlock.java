package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.AlloySmelterBlockEntity;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterBlock extends MachineBlock<AlloySmelterBlockEntity> {

    public AlloySmelterBlock(Properties pProperties) {
        super(pProperties, ModBlockEntities.ALLOY_SMELTER_BE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AlloySmelterBlockEntity(pPos, pState);
    }
}
