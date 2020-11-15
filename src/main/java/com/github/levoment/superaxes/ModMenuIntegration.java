package com.github.levoment.superaxes;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Properties;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {

            // Get the previous screen
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(new TranslatableText("configuration.superaxes.config"));


            // Set category
            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("axebehaviour.superaxes.general"));

            // Set an option for harvesting leaves
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.superaxes.harvest_leaves"), SuperAxesMod.harvestLeaves)
                    .setDefaultValue(false)
                    .setTooltip(new TranslatableText("option.superaxes.harvest_leaves.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.harvestLeaves = newValue)
                    .build());

            // Set an option for the range
            general.addEntry(entryBuilder.startIntField (new TranslatableText("option.superaxes.range"), SuperAxesMod.range)
                    .setDefaultValue(5)
                    .setTooltip(new TranslatableText("option.superaxes.range.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.range = newValue)
                    .build());

            // Set an option for limiting the log radius search
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.superaxes.limit_log_search"), SuperAxesMod.limitSearch)
                    .setDefaultValue(false)
                    .setTooltip(new TranslatableText("option.superaxes.limit_log_search.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.limitSearch = newValue)
                    .build());

            // Set an option for the log search radius
            general.addEntry(entryBuilder.startIntField (new TranslatableText("option.superaxes.log_radius"), SuperAxesMod.logRadius)
                    .setDefaultValue(8)
                    .setTooltip(new TranslatableText("option.superaxes.log_radius.tooltip"))
                    .setSaveConsumer(newValue -> SuperAxesMod.logRadius = newValue)
                    .build());


            // The shape scale tooltip
            Text[] shapeScaleTexts = {
                    new TranslatableText("option.superaxes.shape_scale").setStyle(Style.EMPTY.withBold(true)),
                    new TranslatableText("option.superaxes.shape_scale.tooltip"),
                    new TranslatableText("option.superaxes.shape_scale.tooltip_2"),
                    new TranslatableText("option.superaxes.shape_scale.tooltip_3")};
            // Set an option for the shape scale
            general.addEntry(entryBuilder.startIntField (new TranslatableText("option.superaxes.shape_scale"), SuperAxesMod.shapeScale)
                    .setDefaultValue(1)
                    .setTooltip(shapeScaleTexts)
                    .setSaveConsumer(newValue -> SuperAxesMod.shapeScale = newValue)
                    .setMin(1)
                    .build());

            // The shape show debug lines tooltip
            Text[] showDebugLinesTexts = {
                    new TranslatableText("option.superaxes.debug_lines").setStyle(Style.EMPTY.withBold(true)),
                    new TranslatableText("option.superaxes.debug_lines.tooltip"),
                    new TranslatableText("option.superaxes.debug_lines.tooltip_2"),
                    new TranslatableText("option.superaxes.debug_lines.tooltip_3"),
                    new TranslatableText("option.superaxes.debug_lines.tooltip_4")};
            // Set an option for showing bounding lines of blocks that will be broken
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("option.superaxes.debug_lines"), SuperAxesMod.showDebugLines)
                    .setDefaultValue(false)
                    .setTooltip(showDebugLinesTexts)
                    .setSaveConsumer(newValue -> SuperAxesMod.showDebugLines = newValue)
                    .build());

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
                // Save the properties
                SuperAxesMod.saveConfig(SuperAxesMod.configFile, configProperties);
            });


            return builder.build();
        };
    }


}
