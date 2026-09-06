package net.dafarka.metallurgyplus.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dafarka.metallurgyplus.block.custom.SackStationBlock;
import net.dafarka.metallurgyplus.block.entity.SackStationBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SackStationBlockEntityRenderer implements BlockEntityRenderer<SackStationBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SackStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SackStationBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        if (!blockEntity.getBlockState().getValue(SackStationBlock.HAS_SACK)) return;

        ItemStack sack = blockEntity.getSack();
        if (sack.isEmpty()) return;

        Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Level level = blockEntity.getLevel();
        float animationTime = (level == null ? 0L : level.getGameTime()) + partialTick;
        float bob = (float) Math.sin(animationTime * 0.15F) * 0.04F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.52D + bob, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(animationTime * 2.0F));
        poseStack.scale(0.65F, 0.65F, 0.65F);

        itemRenderer.renderStatic(sack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack,
            bufferSource, level, 0);

        poseStack.popPose();
    }
}