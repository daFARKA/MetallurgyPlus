package net.dafarka.metallurgyplus.block.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dafarka.metallurgyplus.screen.OreProcessingUnitMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static net.dafarka.metallurgyplus.MetallurgyPlus.MODID;

public class OreProcessingUnitBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(19);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    private List<Item> inputs = new ArrayList<>();
    private List<Integer> inputAmounts = new ArrayList<>();
    private List<List<Item>> outputs = new ArrayList<>();
    private List<List<Integer>> outputAmounts = new ArrayList<>();

    private Logger logger = LogManager.getLogger(MODID);

    public OreProcessingUnitBlockEntity(BlockPos pPos,
                                        BlockState pBlockState) {
        super(ModBlockEntities.ORE_PROCESSING_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> OreProcessingUnitBlockEntity.this.progress;
                    case 1 -> OreProcessingUnitBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> OreProcessingUnitBlockEntity.this.progress = pValue;
                    case 1 -> OreProcessingUnitBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        initializeInputsAndOutputs();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.ore_processing_unit");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OreProcessingUnitMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("ore_processing_unit.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("ore_processing_unit.progress");
    }

    /**
     * Gets an item from ForgeRegistries using the name.
     *
     * The respecting item is given by a priority system. First ModItems are returned,
     * if no ModItem exists with that name return a vanilla minecraft item with that name
     * if no vanilla item exists with that name return air. As it is very harmless.
     *
     * @param name the name of the item to return
     *
     * @return an Item with the given name, air otherwise.
     *
     * */
    private Item getItem(String name) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, name));
        if (item == ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "air"))) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", name));
        }
        return item;
    }

    private void initializeInputsAndOutputs() {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream("F:\\Mods\\MetallurgyPlus\\src\\main\\resources\\assets\\metallurgyplus\\recipes\\ore_processing_unit.json")))) {
            Map<String, Object> jsonMap = gson.fromJson(bufferedReader, mapType);

            for (int i = 0; i < jsonMap.size(); i++) {
                Map<String, Map<String, Object>> materials = (Map<String,  Map<String, Object>>) jsonMap.get("recipe" + i);
                Map<String, Object> material = materials.get("0");


                inputs.add(getItem((String) material.get("item")));
                inputAmounts.add(((Double) material.get("count")).intValue());

                List<Item> tempItems = new ArrayList<>();
                List<Integer> tempAmounts = new ArrayList<>();
                for (int j = 1; j < materials.size(); j++) {
                    material = materials.get("" + j);
                    tempItems.add(getItem((String) material.get("item")));
                    tempAmounts.add(((Double) material.get("count")).intValue());
                }
                outputs.add(tempItems);
                outputAmounts.add(tempAmounts);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!inputs.isEmpty()) {
            /*System.out.println(inputs);
            System.out.println(inputAmounts);
            System.out.println(outputs);
            System.out.println(outputAmounts);*/
            Item currentInput = this.itemHandler.getStackInSlot(0).getItem();
            int currentIndex = inputs.indexOf(currentInput);
            if (currentIndex != -1) {
                int inAmount = inputAmounts.get(currentIndex);
                List<Integer> outAmounts = outputAmounts.get(currentIndex);
                if (hasRecipe(currentInput, inAmount, outputs.get(currentIndex), outAmounts)) {
                    increaseCraftingProgress();
                    setChanged(pLevel, pPos, pState);
                    if (hasProgressFinished()) {
                        craftItem(outputs.get(currentIndex), inAmount, outAmounts);
                        resetProgress();
                    }
                } else {
                    resetProgress();
                }
            } else {
                resetProgress();
            }
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe(Item in, int inCount, List<Item> out, List<Integer> outCount) {
        int i = 0;
        for (Item item : out) {
            if (getFirstAvailableSlot(item, outCount.get(i)) == -1) {
                return false;
            }
            i++;
        }

        return (this.itemHandler.getStackInSlot(0).getItem() == in) &&
            (this.itemHandler.getStackInSlot(0).getCount() >= inCount);

    }

    /**
     * Gets the first empty slot.
     *
     * Goes through all slots and returns the first empty slot.
     *
     * @return the i-th slot which is empty or -1 if no slot is empty.
     *
     * */
    private int getFirstEmptySlot() {
        for (int i = 1; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the first availabe slot for that (output) item and the respecting amount.
     *
     * @param item the (output) item
     * @param amount the amount the item is producing
     *
     * @return the i-th slot which has that item and the amount can fit or the return value of getFirstEmptySlot(),
     *          -1 if the item cannot be outputted at all.
     *
     * */
    private int getFirstAvailableSlot(Item item, int amount) {
        for (int i = 1; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.getStackInSlot(i).is(item) && (this.itemHandler.getStackInSlot(i).getCount() + amount <= this.itemHandler.getStackInSlot(i).getMaxStackSize())) {
                return i;
            }
        }
        return getFirstEmptySlot();
    }

    private void craftItem(List<Item> items, int inAmount, List<Integer> outAmounts) {
        this.itemHandler.extractItem(0, inAmount, false);
        int i = 0;
        for (Item item : items) {
            int outAmount = outAmounts.get(i);
            int slotNumber = getFirstAvailableSlot(item, outAmount);
            if (slotNumber > 0) {
                ItemStack result = new ItemStack(item, outAmount);
                this.itemHandler.setStackInSlot(slotNumber, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(slotNumber).getCount() + result.getCount()));
            }
            i++;
        }


    }
}
