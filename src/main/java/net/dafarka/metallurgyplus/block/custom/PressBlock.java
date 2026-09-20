package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.PressBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PressBlock extends MachineBlock<PressBlockEntity> {

    public PressBlock(Properties pProperties) {
        super(pProperties, ModBlockEntities.PRESS_BE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PressBlockEntity(pPos, pState);
    }
}
