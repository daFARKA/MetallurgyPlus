package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.BlockFace;
import net.dafarka.metallurgyplus.block.ItemSideMode;
import net.dafarka.metallurgyplus.block.entity.base.MachineBlockEntity;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MachineIOMenu extends BaseMenu {

    private static final BlockFace[] FACES = BlockFace.values();
    private static final int AUTO_INPUT_INDEX = FACES.length;
    private static final int AUTO_OUTPUT_INDEX = FACES.length + 1;
    private static final int DATA_COUNT = FACES.length + 2;

    private final MachineBlockEntity blockEntity;
    private final Level level;
    private final ContainerData ioData;

    public MachineIOMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(
            containerId,
            inventory,
            getMachineBlockEntity(inventory, buffer.readBlockPos()),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    public MachineIOMenu(
        int containerId,
        Inventory inventory,
        MachineBlockEntity blockEntity,
        ContainerData ioData
    ) {
        super(ModMenuTypes.MACHINE_IO_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = inventory.player.level();
        this.ioData = ioData;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addDataSlots(ioData);
    }

    private static MachineBlockEntity getMachineBlockEntity(Inventory inventory, BlockPos pos) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(pos);
        if (blockEntity instanceof MachineBlockEntity machine) {
            return machine;
        }

        throw new IllegalStateException("Machine block entity is missing at " + pos);
    }

    public static ContainerData createMenuData(MachineBlockEntity blockEntity) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                if (index >= 0 && index < FACES.length) {
                    return blockEntity.getItemSideMode(FACES[index]).ordinal();
                }
                if (index == AUTO_INPUT_INDEX) {
                    return blockEntity.isAutoInputEnabled() ? 1 : 0;
                }
                if (index == AUTO_OUTPUT_INDEX) {
                    return blockEntity.isAutoOutputEnabled() ? 1 : 0;
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                ItemSideMode[] modes = ItemSideMode.values();
                if (index >= 0 && index < FACES.length) {
                    if (value >= 0 && value < modes.length) {
                        blockEntity.setItemSideMode(FACES[index], modes[value]);
                    }
                    return;
                }
                if (index == AUTO_INPUT_INDEX) {
                    blockEntity.setAutoInputEnabled(value != 0);
                } else if (index == AUTO_OUTPUT_INDEX) {
                    blockEntity.setAutoOutputEnabled(value != 0);
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }


    public MachineBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public BlockPos getBlockEntityPos() {
        return blockEntity.getBlockPos();
    }

    public ResourceLocation getMachineTexture() {
        return new ResourceLocation(
            MetallurgyPlus.MODID,
            "textures/gui/" + blockEntity.getBlockState().getBlock()
                .builtInRegistryHolder()
                .key()
                .location()
                .getPath() + "_gui.png"
        );
    }

    public ItemSideMode getItemSideMode(BlockFace face) {
        ItemSideMode[] modes = ItemSideMode.values();
        int mode = ioData.get(face.ordinal());
        if (mode < 0 || mode >= modes.length) {
            return ItemSideMode.DISABLED;
        }
        return modes[mode];
    }

    public boolean isAutoInputEnabled() {
        return ioData.get(AUTO_INPUT_INDEX) != 0;
    }

    public boolean isAutoOutputEnabled() {
        return ioData.get(AUTO_OUTPUT_INDEX) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player,
            blockEntity.getBlockState().getBlock()
        );
    }
}
