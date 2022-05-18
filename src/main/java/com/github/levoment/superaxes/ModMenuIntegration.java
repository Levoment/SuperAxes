package com.github.levoment.superaxes;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.Properties;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {

            // Get the previous screen
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(Text.translatable("configuration.superaxes.config"));


            // Set category
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("axebehaviour.superaxes.general"));

            // Set an option for harvesting leaves
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.superaxes.harvest_leaves"), SuperAxesMod.harvestLeaves)
                    .setDefaultValue(false)
                    .setTooltip(Text.translatable("option.superaxes.harvest_leaves.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.harvestLeaves = newValue)
                    .build());

            // Set an option for the range
            general.addEntry(entryBuilder.startIntField (Text.translatable("option.superaxes.range"), SuperAxesMod.range)
                    .setDefaultValue(5)
                    .setTooltip(Text.translatable("option.superaxes.range.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.range = newValue)
                    .build());



            // Set an option for limiting the log radius search
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.superaxes.limit_log_search"), SuperAxesMod.limitSearch)
                    .setDefaultValue(false)
                    .setTooltip(Text.translatable("option.superaxes.limit_log_search.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.limitSearch = newValue)
                    .build());

            // Set an option for the log search radius
            general.addEntry(entryBuilder.startIntField (Text.translatable("option.superaxes.log_radius"), SuperAxesMod.logRadius)
                    .setDefaultValue(8)
                    .setTooltip(Text.translatable("option.superaxes.log_radius.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.logRadius = newValue)
                    .build());


            // The shape scale tooltip
            Text[] shapeScaleTexts = {
                    Text.translatable("option.superaxes.shape_scale").getWithStyle(Style.EMPTY.withBold(true)).get(0),
                    // Text.translatable("option.superaxes.shape_scale").setStyle(Style.EMPTY.withBold(true)),
                    Text.translatable("option.superaxes.shape_scale.tooltip"),
                    Text.translatable("option.superaxes.shape_scale.tooltip_2"),
                    Text.translatable("option.superaxes.shape_scale.tooltip_3")};
            // Set an option for the shape scale
            general.addEntry(entryBuilder.startIntField (Text.translatable("option.superaxes.shape_scale"), SuperAxesMod.shapeScale)
                    .setDefaultValue(1)
                    .setTooltip(shapeScaleTexts)
                    .setSaveConsumer(newValue -> SuperAxesMod.shapeScale = newValue)
                    .setMin(1)
                    .build());

            // The shape show debug lines tooltip
            Text[] showDebugLinesTexts = {
                    Text.translatable("option.superaxes.debug_lines").getWithStyle(Style.EMPTY.withBold(true)).get(0),
//                    Text.translatable("option.superaxes.debug_lines").setStyle(Style.EMPTY.withBold(true)),
                    Text.translatable("option.superaxes.debug_lines.tooltip"),
                    Text.translatable("option.superaxes.debug_lines.tooltip_2"),
                    Text.translatable("option.superaxes.debug_lines.tooltip_3"),
                    Text.translatable("option.superaxes.debug_lines.tooltip_4")};
            // Set an option for showing bounding lines of blocks that will be broken
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.superaxes.debug_lines"), SuperAxesMod.showDebugLines)
                    .setDefaultValue(false)
                    .setTooltip(showDebugLinesTexts)
                    .setSaveConsumer(newValue -> SuperAxesMod.showDebugLines = newValue)
                    .build());

            // The shape show debug highlight tooltip
//            Text[] showDebugHighlightText = {
//                    Text.translatable("option.superaxes.debug_highlight").setStyle(Style.EMPTY.withBold(true)),
//                    Text.translatable("option.superaxes.debug_highlight.tooltip"),
//                    Text.translatable("option.superaxes.debug_highlight.tooltip_2"),
//                    Text.translatable("option.superaxes.debug_highlight.tooltip_3"),
//                    Text.translatable("option.superaxes.debug_highlight.tooltip_4")};
//            // Set an option for showing bounding box of blocks that will be broken
//            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.superaxes.debug_highlight"), SuperAxesMod.showDebugHighlight)
//                    .setDefaultValue(false)
//                    .setTooltip(showDebugHighlightText)
//                    .setSaveConsumer(newValue -> SuperAxesMod.showDebugHighlight = newValue)
//                    .build());

            // Save config
            builder.setSavingRunnable(() -> {
                // Create a Property
                Properties configProperties = new Properties();
                // Set the property
                configProperties.setProperty("harvestLeaves", String.valueOf(SuperAxesMod.harvestLeaves));
                configProperties.setProperty("range", String.valueOf(SuperAxesMod.range));
                configProperties.setProperty("limitSearch", String.valueOf(SuperAxesMod.limitSearch));
                configProperties.setProperty("logRadius", String.valueOf(SuperAxesMod.logRadius));
                configProperties.setProperty("shapeScale", String.valueOf(SuperAxesMod.shapeScale));
                configProperties.setProperty("showDebugLines", String.valueOf(SuperAxesMod.showDebugLines));
                // configProperties.setProperty("showDebugHighlight", String.valueOf(SuperAxesMod.showDebugHighlight));
                // Save the properties
                SuperAxesMod.saveConfig(SuperAxesMod.configFile, configProperties);
            });


            return builder.build();
        };
    }


}
