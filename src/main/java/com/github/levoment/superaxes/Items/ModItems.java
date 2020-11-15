package com.github.levoment.superaxes.Items;


import com.github.levoment.superaxes.SuperAxesMaterialGenerator;
import com.github.levoment.superaxes.SuperAxesMod;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class ModItems {

    // Items
    public static SuperAxeItem WoodenSuperAxe = null;
    public static SuperAxeItem StoneSuperAxe = null;
    public static SuperAxeItem IronSuperAxe = null;
    public static SuperAxeItem GoldSuperAxe = null;
    public static SuperAxeItem DiamondSuperAxe = null;
    public static SuperAxeItem NetheriteSuperAxe = null;

    public static Map<Identifier, Item> mapOfIdentifiers = new HashMap<>();

    // Initialize the items
    public static void initializeItems() {
        WoodenSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.WOOD), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP));
        StoneSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.STONE), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP));
        IronSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.IRON), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP));
        GoldSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.GOLD), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP));
        DiamondSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.DIAMOND), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP));
        NetheriteSuperAxe = new SuperAxeItem(new SuperAxesMaterialGenerator(ToolMaterials.NETHERITE), new Item.Settings().group(SuperAxesMod.SUPERAXES_GROUP).fireproof());
    }

    // Create a map with the identifier of the item and the item
    public static void poplateMapOfIdentifiers() {
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "wooden_superaxe"), WoodenSuperAxe);
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "stone_superaxe"), StoneSuperAxe);
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "iron_superaxe"), IronSuperAxe);
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "gold_superaxe"), GoldSuperAxe);
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "diamond_superaxe"), DiamondSuperAxe);
        mapOfIdentifiers.put(new Identifier(SuperAxesMod.MODID, "netherite_superaxe"), NetheriteSuperAxe);
    }

    // Register the initialized items
    public static void registerItems() {
        mapOfIdentifiers.forEach((identifier, item) -> Registry.register(Registry.ITEM, identifier, item));
    }
}
