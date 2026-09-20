package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.ExtractorBlockEntity;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ExtractorBlock extends MachineBlock<ExtractorBlockEntity> {

    public ExtractorBlock(Properties pProperties) {
        super(pProperties, ModBlockEntities.ETRACTOR_BE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ExtractorBlockEntity(pPos, pState);
    }
}
