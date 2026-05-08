package com.finndog.client.mixin;

import com.finndog.client.SelectionState;
import com.finndog.client.screen.StructureConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class SetScreenMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void structureareaselector$interceptStructureScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof StructureBlockEditScreen)) return;

        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player == null || !mc.player.isCreative()) return;

        SelectionState state = SelectionState.getInstance();
        if (state.getState() != SelectionState.State.IDLE) return;

        StructureBlockEntity blockEntity = ((StructureBlockEditScreenAccessor) screen).getStructure();
        BlockPos pos = blockEntity.getBlockPos();

        state.startConfig(pos);
        ci.cancel();
        mc.setScreen(new StructureConfigScreen());
    }
}
