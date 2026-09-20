package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.GemstoneCutterBlockEntity;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GemstoneCutterBlock extends MachineBlock<GemstoneCutterBlockEntity> {

    public GemstoneCutterBlock(Properties pProperties) {
        super(pProperties, ModBlockEntities.GEMSTONE_CUTTER_BE);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GemstoneCutterBlockEntity(pPos, pState);
    }
}
