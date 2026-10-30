package net.dafarka.metallurgyplus.item.tool;

public class ModularToolStats {
    public int baseDurability;
    public float miningSpeed;
    public float durabilityModifier;

    public ModularToolStats(int baseDurability, float miningSpeed, float durabilityModifier) {
        this.baseDurability = baseDurability;
        this.miningSpeed = miningSpeed;
        this.durabilityModifier = durabilityModifier;
    }
}
