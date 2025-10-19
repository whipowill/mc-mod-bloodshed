package com.whipowill.bloodshed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BloodshedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bloodshed.json");
    
    public boolean enabled = true;
    public int bloodFadeTime = 30;
    public float goreValue = 1.0f;
    public List<String> noBleedEntities = Arrays.asList(
        "minecraft:skeleton",
        "minecraft:stray",
        "minecraft:wither_skeleton",
        "minecraft:blaze",
        "minecraft:magma_cube",
        "minecraft:slime",
        "minecraft:iron_golem",
        "minecraft:snow_golem"
    );
    
    public static BloodshedConfig load() {
        BloodshedConfig config = new BloodshedConfig();
        
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, BloodshedConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config.save();
        return config;
    }
    
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean shouldEntityBleed(String entityId) {
        return !noBleedEntities.contains(entityId);
    }
}
