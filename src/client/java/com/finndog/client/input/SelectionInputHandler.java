package com.finndog.client.input;

import com.finndog.client.SelectionState;
import com.finndog.client.util.FacingAxisHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
public final class SelectionInputHandler {

    private SelectionInputHandler() {}

    public static void handleLeftClick(BlockPos pos) {
        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;
        state.setCorner1(pos);
    }

    public static void handleRightClick(BlockPos pos) {
        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;
        state.setCorner2(pos);
    }

    public static void handleMiddleClick(BlockHitResult hitResult) {
        SelectionState state = SelectionState.getInstance();
        if (state.getState() != SelectionState.State.BOTH_SET) return;
        state.expandToInclude(hitResult.getBlockPos());
    }

    public static void handleScroll(double scrollDelta) {
        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Direction facing = FacingAxisHelper.getFacingDirection(mc.player.getXRot(), mc.player.getYRot());
        int sign = scrollDelta > 0 ? 1 : -1;
        BlockPos offset = new BlockPos(
                facing.getStepX() * sign,
                facing.getStepY() * sign,
                facing.getStepZ() * sign
        );

        if (state.getLastSetCorner() == 2 && state.getCorner2() != null) {
            state.setCorner2(state.getCorner2().offset(offset));
        } else if (state.getLastSetCorner() == 1 && state.getCorner1() != null) {
            state.setCorner1(state.getCorner1().offset(offset));
        }
    }
}
