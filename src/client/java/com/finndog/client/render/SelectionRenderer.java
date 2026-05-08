package com.finndog.client.render;

import com.finndog.client.SelectionState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class SelectionRenderer {

    private SelectionRenderer() {}

    public static void render(WorldRenderContext context) {
        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;

        BlockPos c1 = state.getCorner1();
        BlockPos c2 = state.getCorner2();
        if (c1 == null && c2 == null) return;

        MultiBufferSource consumers = context.consumers();
        if (consumers == null) return;

        PoseStack poseStack = context.matrices();
        Vec3 camPos = context.gameRenderer().getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        boolean valid = state.isValidSize();
        float cr = 1.0f;
        float cg = valid ? 1.0f : 0.3f;
        float cb = valid ? 1.0f : 0.3f;

        if (c1 != null && c2 != null) {
            BlockPos min = state.getMinCorner();
            BlockPos max = state.getMaxCorner();
            double x0 = min.getX(), y0 = min.getY(), z0 = min.getZ();
            double x1 = max.getX() + 1.0, y1 = max.getY() + 1.0, z1 = max.getZ() + 1.0;

            DebugRenderer.renderFilledBox(poseStack, consumers,
                    x0, y0, z0, x1, y1, z1,
                    cr, cg, cb, 0.15f);

            RenderSystem.lineWidth(2.0f);
            VertexConsumer lineConsumer = consumers.getBuffer(RenderType.lines());
            ShapeRenderer.renderLineBox(poseStack.last(), lineConsumer,
                    x0, y0, z0, x1, y1, z1,
                    cr, cg, cb, 1.0f);
        } else if (c1 != null) {
            RenderSystem.lineWidth(2.0f);
            VertexConsumer lineConsumer = consumers.getBuffer(RenderType.lines());
            ShapeRenderer.renderLineBox(poseStack.last(), lineConsumer,
                    c1.getX(), c1.getY(), c1.getZ(),
                    c1.getX() + 1.0, c1.getY() + 1.0, c1.getZ() + 1.0,
                    cr, cg, cb, 1.0f);
        }

        double o = 0.005;
        RenderSystem.lineWidth(3.0f);
        if (c1 != null) {
            VertexConsumer lc = consumers.getBuffer(RenderType.lines());
            ShapeRenderer.renderLineBox(poseStack.last(), lc,
                    c1.getX() - o, c1.getY() - o, c1.getZ() - o,
                    c1.getX() + 1.0 + o, c1.getY() + 1.0 + o, c1.getZ() + 1.0 + o,
                    1.0f, 0.6f, 0.0f, 1.0f);
        }
        if (c2 != null) {
            VertexConsumer lc = consumers.getBuffer(RenderType.lines());
            ShapeRenderer.renderLineBox(poseStack.last(), lc,
                    c2.getX() - o, c2.getY() - o, c2.getZ() - o,
                    c2.getX() + 1.0 + o, c2.getY() + 1.0 + o, c2.getZ() + 1.0 + o,
                    0.0f, 0.6f, 1.0f, 1.0f);
        }

        RenderSystem.lineWidth(1.0f);
        poseStack.popPose();
    }
}
