package com.finndog.client;

import com.finndog.client.input.SelectionInputHandler;
import com.finndog.client.render.SelectionHudRenderer;
import com.finndog.client.render.SelectionRenderer;
import com.finndog.client.screen.StructureConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
public class StructureAreaSelectorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClientSide()) return InteractionResult.PASS;

            SelectionState state = SelectionState.getInstance();

            if (state.isActive() && player.getMainHandItem().isEmpty()) {
                SelectionInputHandler.handleRightClick(hitResult.getBlockPos());
                return InteractionResult.SUCCESS;
            }

            if (player.isCreative() && player.getItemInHand(hand).is(Items.STRUCTURE_BLOCK)) {
                BlockPos placePos = calculatePlacementPos(world, hitResult);
                Minecraft.getInstance().execute(() -> {
                    if (world.getBlockState(placePos).is(Blocks.STRUCTURE_BLOCK)) {
                        state.startConfig(placePos);
                        Minecraft.getInstance().setScreen(new StructureConfigScreen());
                    }
                });
            }

            return InteractionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClientSide()) return InteractionResult.PASS;

            SelectionState state = SelectionState.getInstance();
            if (state.isActive() && player.getMainHandItem().isEmpty()) {
                SelectionInputHandler.handleLeftClick(pos);
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(SelectionRenderer::render);

        HudRenderCallback.EVENT.register(SelectionHudRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            SelectionState state = SelectionState.getInstance();
            if (state.isActive() && mc.player != null && state.getStructureBlockPos() != null) {
                double dx = mc.player.getX() - state.getStructureBlockPos().getX();
                double dy = mc.player.getY() - state.getStructureBlockPos().getY();
                double dz = mc.player.getZ() - state.getStructureBlockPos().getZ();
                if (dx * dx + dy * dy + dz * dz > 200 * 200) {
                    state.cancel();
                }
            }
        });
    }

    private static BlockPos calculatePlacementPos(Level world, BlockHitResult hitResult) {
        BlockPos clicked = hitResult.getBlockPos();
        if (world.getBlockState(clicked).canBeReplaced()) {
            return clicked;
        }
        return clicked.relative(hitResult.getDirection());
    }
}
