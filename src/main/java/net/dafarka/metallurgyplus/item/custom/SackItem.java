package net.dafarka.metallurgyplus.item.custom;

import net.minecraft.world.item.Item;

public class SackItem extends Item {

    private final int tier;
    private final int capacity;

    public SackItem(Properties properties, int tier) {
        super(properties);

        this.tier = tier;

        int capacity = 1000 * (int) Math.pow(10, tier - 1);
        if (tier == 8) capacity = Integer.MAX_VALUE;
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}