package com.whipowill.bloodshed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bloodshed implements ModInitializer {

    public static String MOD_ID = "bloodshed";
    public static BloodshedConfig CONFIG;
    public static final Logger LOGGER = LogManager.getLogger("Bloodshed");

    public static BloodBlock BLOOD_BLOCK;
    public static Item BLOOD_ITEM;
    public static EntityType<BloodEntity> BLOOD_ENTITY;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Bloodshed mod...");

        // Load config FIRST
        CONFIG = BloodshedConfig.load();
        LOGGER.info("Config loaded: fadeTime={}, goreValue={}", CONFIG.bloodFadeTime, CONFIG.goreValue);

        // NOW initialize blocks and items that depend on CONFIG
        BLOOD_BLOCK = new BloodBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
            .noCollision()
            .nonOpaque()
            .sounds(BlockSoundGroup.SLIME)
            .velocityMultiplier(0.75f)
            .strength(0.1f));

        BLOOD_ITEM = new Item(new FabricItemSettings());

        BLOOD_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "blood_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, BloodEntity::new)
                .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                .build());

        // Register everything
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "blood"), BLOOD_ITEM);
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "blood"), BLOOD_BLOCK);

        LOGGER.info("Bloodshed mod initialized successfully!");
    }
}