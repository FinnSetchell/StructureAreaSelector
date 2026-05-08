package com.finndog.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeyBindings {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            ResourceLocation.fromNamespaceAndPath("structureareaselector", "keybinds")
    );

    public static final KeyMapping CANCEL = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.structureareaselector.cancel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY)
    );

    public static final KeyMapping CONFIRM = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.structureareaselector.confirm", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY)
    );

    private ModKeyBindings() {}

    public static void register() {}
}
