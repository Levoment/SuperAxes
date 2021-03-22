# SuperAxes 

![This is the alt-attribute.][modIcon]

<br>

![Java CI with Gradle](https://github.com/Levoment/SuperAxes/workflows/Java%20CI%20with%20Gradle/badge.svg)

---

![AdvancementMade]

Adds super axes that help chop down trees for easier wood 
resource gathering. 
Sneaking will make the tool work like a normal axe. 
If Harvest leaves is enabled, only leaves that have a vanilla mechanic 
to measure whether they are set to decay or not will be harvested. 
On all versions after snapshot version 20w20b, the enchantment Silk Touch 
works when harvesting leaves.

## Dependencies

![FabricAPI]

## Recipes

![CraftingRecipes]

**Netherite SuperAxe Recipe (Smithing Table)**

![NetheriteSuperAxeRecipe]

## [🔗 Items' Properties ↗️]( https://github.com/Levoment/SuperAxes/wiki/SuperAxes-properties)

## Configuration Screen

The configuration screen allows selecting whether to harvest 
the leaves of the tree that is being chopped. 
In order to see the configuration menu, the mod
[Mod Menu (Fabric)](https://www.curseforge.com/minecraft/mc-mods/modmenu) 
is required.

When harvesting leaves is enabled, 
the **Leaves scanning range** decides the radius that will be 
used to check leaves for harvesting. 
A greater number will make it slower on forests that have 
trees connected by leaves since it will scan a greater radius to 
check if a leaf should be harvested or not.

![ConfigurationScreen]

Starting on version **2.0.7** of the mod:

If enabled, the **Show Debug Lines** option allows the 
player to Sneak+Use (Shift+Right Click) on a log to 
outline the logs that would be harvested if the player mines that log. 
To remove the outline, the player can Sneak+Use (Shift+Right Click) 
again on the log.

![DebugLines]

Starting on version **2.0.9** of the mod:

If enabled, the **Show Debug Highlight** option allows the 
player to Sneak+Use (Shift+Right Click) on a log to highlight the 
logs that would be harvested if the player mines that log. 
To remove the highlight, the player can Sneak+Use (Shift+Right Click) 
again on the log.

**Notes**:

If you change the item in the main hand to another item or throw the 
SuperAxe while the logs of the tree are being broken, 
the breaking process will stop.


[modIcon]: src/main/resources/assets/superaxes/icon.png
[FabricAPI]: https://i.imgur.com/Ol1Tcf8.png
[AdvancementMade]: screenshots/Advancement.gif
[CraftingRecipes]: screenshots/Crafting-Recipes.png
[NetheriteSuperAxeRecipe]: screenshots/Netherite-Super-Axe-Recipe.png
[ConfigurationScreen]: screenshots/Configuration-Screen.png
[DebugLines]: screenshots/Outline.png
[DebugHighlight]: screenshots/Highlight.png