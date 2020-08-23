package com.github.levoment.superaxes;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

public class SuperAxesMaterialGenerator implements ToolMaterial {

    // Variables for the SuperAxe material
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final float axeAttackDamage;
    private final float axeAttackSpeed;
    private final Lazy<Ingredient> repairIngredient;

    public SuperAxesMaterialGenerator(ToolMaterial toolMaterial) {
        // Set all super axe material characteristics
        this.miningLevel = toolMaterial.getMiningLevel();
        this.itemDurability = toolMaterial.getDurability() * 7;
        if (toolMaterial.getMiningSpeedMultiplier() <= 2.0f) this.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
        else this.miningSpeed = toolMaterial.getMiningSpeedMultiplier() / 2.0f;
        this.attackDamage = toolMaterial.getAttackDamage() * 3.0f;
        this.enchantability = toolMaterial.getEnchantability() * 2;
        this.axeAttackDamage = 5.0f;
        float calculatedAxeAttackSpeed = -(float)Math.round(toolMaterial.getMiningSpeedMultiplier() / 4.0f) - 1.5f;
        if (calculatedAxeAttackSpeed < -4.0f) this.axeAttackSpeed = -3.5f;
        else if(calculatedAxeAttackSpeed < -2.0f) this.axeAttackSpeed = -3.2f;
        else this.axeAttackSpeed = calculatedAxeAttackSpeed;
        this.repairIngredient = new Lazy<Ingredient>(() -> toolMaterial.getRepairIngredient());
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public float getAxeAttackDamage() {
        return this.axeAttackDamage;
    }

    public float getAxeAttackSpeed() {
        return this.axeAttackSpeed;
    }
}
