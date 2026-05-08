package com.finndog.client.mixin;

import com.finndog.client.SelectionState;
import com.finndog.client.input.SelectionInputHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class PickBlockMixin {

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void structureareaselector$onPickBlock(CallbackInfo ci) {
        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;
        if (state.getState() != SelectionState.State.BOTH_SET) return;

        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player != null && mc.player.getMainHandItem().isEmpty()) {
            if (mc.hitResult instanceof BlockHitResult blockHit
                    && blockHit.getType() != HitResult.Type.MISS) {
                SelectionInputHandler.handleMiddleClick(blockHit);
                ci.cancel();
            }
        }
    }
}
