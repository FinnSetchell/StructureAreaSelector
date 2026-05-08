package com.finndog.client.mixin;

import com.finndog.client.SelectionState;
import com.finndog.client.input.SelectionInputHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MouseHandler.class)
public class MouseScrollMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void structureareaselector$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        SelectionState state = SelectionState.getInstance();
        if (state.isActive() && mc.player != null && mc.player.getMainHandItem().isEmpty()) {
            SelectionInputHandler.handleScroll(yOffset);
            ci.cancel();
        }
    }
}
