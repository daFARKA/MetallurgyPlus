package net.dafarka.metallurgyplus.block.custom;

import net.dafarka.metallurgyplus.block.entity.MachineBlockEntity;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MachineBlock extends BaseBlock {

    private final RecipeType<? extends MachineRecipe> recipeType;
    protected final int inputSlots;
    protected final int outputSlots;
    protected final int[][] inputPositions;
    protected final int[][] outputPositions;

    protected final int[] progressPosition;


    public MachineBlock(
        Properties properties,
        RecipeType<? extends MachineRecipe> recipeType,
        int[][] inputPositions,
        int[][] outputPositions,
        int[] progressPosition
    ) {
        super(properties);

        this.recipeType = recipeType;
        this.inputSlots = inputPositions.length;
        this.outputSlots = outputPositions.length;
        this.inputPositions = inputPositions;
        this.outputPositions = outputPositions;
        this.progressPosition = progressPosition;
    }

    public RecipeType<? extends MachineRecipe> getRecipeType() {
        return recipeType;
    }

    public int getInputSlots() {
        return inputSlots;
    }

    public int getOutputSlots() {
        return outputSlots;
    }

    public int[][] getInputPositions() {
        return inputPositions;
    }

    public int[][] getOutputPositions() {
        return outputPositions;
    }

    public int[] getProgressPosition() {
        return progressPosition;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof MachineBlockEntity machine) {
                machine.drops();
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof MachineBlockEntity machine) {
                NetworkHooks.openScreen((ServerPlayer) player, machine, pos);
            } else {
                throw new IllegalStateException("Machine block entity is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <BE extends BlockEntity> BlockEntityTicker<BE> getTicker(Level level, BlockState state, BlockEntityType<BE> type) {
        if (level.isClientSide()) {
            return null;
        }

        return (level1, pos, state1, blockEntity) ->
            ((MachineBlockEntity) blockEntity).tick(level1, pos, state1);
    }
}