package net.dafarka.metallurgyplus.screen.menu;

import net.minecraft.world.inventory.ContainerData;

public class UtilityMenu {
    private ContainerData data;

    public UtilityMenu(ContainerData data) {
        this.data = data;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledEnergy() {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        int energyBarSize = 48;

        if (maxEnergy <= 0 || energy <= 0) {
            return 0;
        }

        double scaled = ((double) energy / maxEnergy) * energyBarSize;

        return (int) Math.round(scaled);
    }
}
