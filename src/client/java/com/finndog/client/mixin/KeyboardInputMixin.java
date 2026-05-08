package com.finndog.client.mixin;

import com.finndog.client.ModKeyBindings;
import com.finndog.client.SelectionState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public class KeyboardInputMixin {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void structureareaselector$onKeyPress(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) return;
        if (Minecraft.getInstance().screen != null) return;

        SelectionState state = SelectionState.getInstance();
        if (!state.isActive()) return;

        if (keyEvent.key() == GLFW.GLFW_KEY_ESCAPE || ModKeyBindings.CANCEL.matches(keyEvent)) {
            state.cancel();
            ci.cancel();
        } else if (keyEvent.key() == GLFW.GLFW_KEY_ENTER || keyEvent.key() == GLFW.GLFW_KEY_KP_ENTER
                || ModKeyBindings.CONFIRM.matches(keyEvent)) {
            if (state.getState() == SelectionState.State.BOTH_SET && state.isValidSize()) {
                state.confirm();
            }
            ci.cancel();
        }
    }
}
