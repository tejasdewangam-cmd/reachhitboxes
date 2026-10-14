package com.reachhitboxes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side mod: draws thick, coloured hitboxes around entities, and the
 * hitbox of the entity under your crosshair (inside your reach) changes colour.
 */
public class ReachHitboxesClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean active = true;

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachhitboxes.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.reachhitboxes"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                active = !active;
                if (client.player != null) {
                    client.player.sendMessage(new LiteralText("Reach Hitboxes: " + (active ? "ON" : "OFF")), true);
                }
            }
        });

        WorldRenderEvents.LAST.register(context -> {
            if (active) {
                HitboxRenderer.render(context);
            }
        });
    }
}
