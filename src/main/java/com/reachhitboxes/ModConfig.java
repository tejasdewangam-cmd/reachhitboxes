package com.reachhitboxes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Settings file: .minecraft/config/reachhitboxes.properties
 * It is created with default values the first time the game starts.
 */
public final class ModConfig {
    public static float lineWidth = 3.0F;
    public static double maxRenderDistance = 48.0D;
    public static boolean playersOnly = false;

    public static boolean boxEnabled = true;
    public static int boxColor = 0xFFFFFFFF;

    public static boolean targetEnabled = true;
    public static int targetColor = 0xFFFF0000;

    public static boolean eyeHeightEnabled = true;
    public static int eyeHeightColor = 0xFFFF0000;

    public static boolean lookVectorEnabled = true;
    public static int lookVectorColor = 0xFF0000FF;

    private static final String DEFAULT_FILE =
            "# Reach Hitboxes settings (restart the game after changing)\n"
            + "\n"
            + "# Thickness of the hitbox lines (vanilla is about 2.5)\n"
            + "lineWidth=3.0\n"
            + "\n"
            + "# Hitboxes are only drawn for entities closer than this (blocks)\n"
            + "maxRenderDistance=48\n"
            + "\n"
            + "# true = only draw hitboxes of players, false = all entities\n"
            + "playersOnly=false\n"
            + "\n"
            + "# Main hitbox box\n"
            + "boxEnabled=true\n"
            + "boxColor=#FFFFFF\n"
            + "\n"
            + "# The hitbox of the entity under your crosshair (inside your reach) uses this colour\n"
            + "targetEnabled=true\n"
            + "targetColor=#FF0000\n"
            + "\n"
            + "# Thin box at eye height\n"
            + "eyeHeightEnabled=true\n"
            + "eyeHeightColor=#FF0000\n"
            + "\n"
            + "# Line showing where the entity is looking\n"
            + "lookVectorEnabled=true\n"
            + "lookVectorColor=#0000FF\n";

    private ModConfig() {
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("reachhitboxes.properties");
        try {
            if (!Files.exists(path)) {
                Files.write(path, DEFAULT_FILE.getBytes(StandardCharsets.UTF_8));
            }
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            }

            lineWidth = (float) readDouble(props, "lineWidth", lineWidth, 1.0D, 10.0D);
            maxRenderDistance = readDouble(props, "maxRenderDistance", maxRenderDistance, 4.0D, 256.0D);
            playersOnly = readBoolean(props, "playersOnly", playersOnly);

            boxEnabled = readBoolean(props, "boxEnabled", boxEnabled);
            boxColor = readColor(props, "boxColor", boxColor);

            targetEnabled = readBoolean(props, "targetEnabled", targetEnabled);
            targetColor = readColor(props, "targetColor", targetColor);

            eyeHeightEnabled = readBoolean(props, "eyeHeightEnabled", eyeHeightEnabled);
            eyeHeightColor = readColor(props, "eyeHeightColor", eyeHeightColor);

            lookVectorEnabled = readBoolean(props, "lookVectorEnabled", lookVectorEnabled);
            lookVectorColor = readColor(props, "lookVectorColor", lookVectorColor);
        } catch (IOException | RuntimeException e) {
            // Keep the default values if the file cannot be read.
            System.err.println("[reachhitboxes] Could not read config, using defaults: " + e);
        }
    }

    private static boolean readBoolean(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static double readDouble(Properties props, String key, double fallback, double min, double max) {
        try {
            double value = Double.parseDouble(props.getProperty(key, String.valueOf(fallback)).trim());
            return Math.max(min, Math.min(max, value));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int readColor(Properties props, String key, int fallback) {
        try {
            return 0xFF000000 | (Integer.decode(props.getProperty(key, "").trim()) & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
