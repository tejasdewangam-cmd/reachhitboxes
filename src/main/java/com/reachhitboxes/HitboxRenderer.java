package com.reachhitboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Draws entity hitboxes the same way the F3+B debug view does (depth tested,
 * so nothing is shown through walls, and invisible entities are skipped),
 * but with a configurable line width and colours.
 */
public final class HitboxRenderer {
    private HitboxRenderer() {
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }
        // Servers can turn off debug info (reduced debug info); respect that.
        if (client.hasReducedDebugInfo()) {
            return;
        }

        MatrixStack matrices = context.matrixStack();
        Vec3d cam = context.camera().getPos();
        float tickDelta = context.tickDelta();
        Entity viewer = client.getCameraEntity();
        Entity target = client.targetedEntity; // entity under the crosshair, inside reach
        double maxDistanceSq = ModConfig.maxRenderDistance * ModConfig.maxRenderDistance;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(ModConfig.lineWidth);

        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        for (Entity entity : client.world.getEntities()) {
            if (entity == viewer || entity.isInvisible() || entity.isSpectator()) {
                continue;
            }
            if (ModConfig.playersOnly && !(entity instanceof PlayerEntity)) {
                continue;
            }

            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

            double dx = x - cam.x;
            double dy = y - cam.y;
            double dz = z - cam.z;
            if (dx * dx + dy * dy + dz * dz > maxDistanceSq) {
                continue;
            }

            matrices.push();
            matrices.translate(dx, dy, dz);
            drawEntity(buffer, matrices.peek().getModel(), entity, tickDelta, entity == target);
            matrices.pop();
        }

        tessellator.draw();

        RenderSystem.lineWidth(1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void drawEntity(BufferBuilder buffer, Matrix4f matrix, Entity entity, float tickDelta, boolean targeted) {
        // Box relative to the entity position, like the vanilla debug hitbox.
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        if (ModConfig.boxEnabled) {
            int color = (targeted && ModConfig.targetEnabled) ? ModConfig.targetColor : ModConfig.boxColor;
            drawBox(buffer, matrix, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color);
        }

        if (entity instanceof LivingEntity) {
            float eye = entity.getStandingEyeHeight();

            if (ModConfig.eyeHeightEnabled) {
                drawBox(buffer, matrix, box.minX, eye - 0.01D, box.minZ, box.maxX, eye + 0.01D, box.maxZ,
                        ModConfig.eyeHeightColor);
            }

            if (ModConfig.lookVectorEnabled) {
                Vec3d look = entity.getRotationVec(tickDelta);
                line(buffer, matrix,
                        0.0F, eye, 0.0F,
                        (float) (look.x * 2.0D), (float) (eye + look.y * 2.0D), (float) (look.z * 2.0D),
                        ModConfig.lookVectorColor);
            }
        }
    }

    private static void drawBox(BufferBuilder b, Matrix4f m,
                                double minX, double minY, double minZ,
                                double maxX, double maxY, double maxZ, int color) {
        float x1 = (float) minX;
        float y1 = (float) minY;
        float z1 = (float) minZ;
        float x2 = (float) maxX;
        float y2 = (float) maxY;
        float z2 = (float) maxZ;

        // bottom
        line(b, m, x1, y1, z1, x2, y1, z1, color);
        line(b, m, x2, y1, z1, x2, y1, z2, color);
        line(b, m, x2, y1, z2, x1, y1, z2, color);
        line(b, m, x1, y1, z2, x1, y1, z1, color);
        // top
        line(b, m, x1, y2, z1, x2, y2, z1, color);
        line(b, m, x2, y2, z1, x2, y2, z2, color);
        line(b, m, x2, y2, z2, x1, y2, z2, color);
        line(b, m, x1, y2, z2, x1, y2, z1, color);
        // vertical edges
        line(b, m, x1, y1, z1, x1, y2, z1, color);
        line(b, m, x2, y1, z1, x2, y2, z1, color);
        line(b, m, x2, y1, z2, x2, y2, z2, color);
        line(b, m, x1, y1, z2, x1, y2, z2, color);
    }

    private static void line(BufferBuilder b, Matrix4f m,
                             float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        int a = (color >>> 24) & 255;
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int bl = color & 255;
        b.vertex(m, x1, y1, z1).color(r, g, bl, a).next();
        b.vertex(m, x2, y2, z2).color(r, g, bl, a).next();
    }
}
