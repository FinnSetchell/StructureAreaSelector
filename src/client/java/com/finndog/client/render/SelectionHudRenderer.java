package com.finndog.client.render;

import com.finndog.client.SelectionState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class SelectionHudRenderer {

    private static final ResourceLocation ICON_LEFT = ResourceLocation.fromNamespaceAndPath("structureareaselector", "textures/gui/mouse/left.png");
    private static final ResourceLocation ICON_RIGHT = ResourceLocation.fromNamespaceAndPath("structureareaselector", "textures/gui/mouse/right.png");
    private static final ResourceLocation ICON_SCROLL = ResourceLocation.fromNamespaceAndPath("structureareaselector", "textures/gui/mouse/scroll.png");

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_GAP = 4;
    private static final int LINE_SPACING = 20;

    private static final int HINT_COLOR = 0xFFFFFFFF;
    private static final int VALID_COLOR = 0xFFFFFFFF;
    private static final int INVALID_COLOR = 0xFFFF4444;

    private SelectionHudRenderer() {}

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        SelectionState state = SelectionState.getInstance();

        if (state.shouldShowSavedMessage()) {
            renderStatusLine(graphics, mc, "✔ Saved!", true);
            return;
        }

        if (!state.isActive()) return;

        renderCrosshairHints(graphics, mc, state);
        if (state.getState() == SelectionState.State.CORNER_1_SET
                || state.getState() == SelectionState.State.BOTH_SET) {
            renderStatusLine(graphics, mc, state);
        }
    }

    private static void renderCrosshairHints(GuiGraphics graphics, Minecraft mc, SelectionState state) {
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;

        int y = cy + 14;

        switch (state.getState()) {
            case SELECTING -> {
                drawIconHint(graphics, mc, cx, y, ICON_LEFT, "First Corner");
            }
            case CORNER_1_SET -> {
                drawIconHint(graphics, mc, cx, y, ICON_RIGHT, "Second Corner");
                drawIconHint(graphics, mc, cx, y + LINE_SPACING, ICON_SCROLL, "Nudge");
            }
            case BOTH_SET -> {
                drawIconHint(graphics, mc, cx, y, ICON_SCROLL, "Extend");
                drawTextHint(graphics, mc, cx, y + LINE_SPACING, "[Enter] Save · [Esc] Cancel");
            }
            default -> {}
        }
    }

    private static void drawIconHint(GuiGraphics graphics, Minecraft mc, int cx, int y, ResourceLocation icon, String text) {
        int textWidth = mc.font.width(text);
        int totalWidth = ICON_SIZE + ICON_TEXT_GAP + textWidth;
        int startX = cx - totalWidth / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, startX, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        int textY = y + (ICON_SIZE - mc.font.lineHeight) / 2;
        graphics.drawString(mc.font, text, startX + ICON_SIZE + ICON_TEXT_GAP, textY, HINT_COLOR, true);
    }

    private static void drawTextHint(GuiGraphics graphics, Minecraft mc, int cx, int y, String text) {
        int textY = y + (ICON_SIZE - mc.font.lineHeight) / 2;
        graphics.drawCenteredString(mc.font, text, cx, textY, HINT_COLOR);
    }

    private static void renderStatusLine(GuiGraphics graphics, Minecraft mc, SelectionState state) {
        Vec3i size = state.getSize();
        boolean valid = state.isValidSize();
        String name = state.getStructureName();

        String sizeText = size.getX() + " × " + size.getY() + " × " + size.getZ();
        String statusText = "\"" + name + "\" | " + sizeText;
        if (!valid) {
            statusText += " ✗";
        }

        renderStatusLine(graphics, mc, statusText, valid);
    }

    private static void renderStatusLine(GuiGraphics graphics, Minecraft mc, String text, boolean valid) {
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int y = mc.getWindow().getGuiScaledHeight() - 48;
        int color = valid ? VALID_COLOR : INVALID_COLOR;

        graphics.drawCenteredString(mc.font, text, cx, y, color);
    }
}
