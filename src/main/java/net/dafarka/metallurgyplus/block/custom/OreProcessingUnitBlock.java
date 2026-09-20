package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.OreProcessingUnitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class OreProcessingUnitBlock extends MachineBlock<OreProcessingUnitBlockEntity> {

    public OreProcessingUnitBlock(Properties pProperties) {
        super(pProperties, ModBlockEntities.ORE_PROCESSING_BE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new OreProcessingUnitBlockEntity(pPos, pState);
    }
}
