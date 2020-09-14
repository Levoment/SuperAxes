package com.github.levoment.superaxes;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
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

            // Save config
            builder.setSavingRunnable(() -> {
                // Create a Property
                Properties configProperties = new Properties();
                // Set the property
                configProperties.setProperty("harvestLeaves", String.valueOf(SuperAxesMod.harvestLeaves));
                configProperties.setProperty("range", String.valueOf(SuperAxesMod.range));
                configProperties.setProperty("limitSearch", String.valueOf(SuperAxesMod.limitSearch));
                configProperties.setProperty("logRadius", String.valueOf(SuperAxesMod.logRadius));
                // Save the properties
                SuperAxesMod.saveConfig(SuperAxesMod.configFile, configProperties);
            });


            return builder.build();
        };
    }


}
