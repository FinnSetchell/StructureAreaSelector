package com.finndog.client.render;

import com.finndog.client.SelectionState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Vec3i;

@Environment(EnvType.CLIENT)
public final class SelectionHudRenderer {

    private static final int HINT_COLOR = 0xFFFFFFFF;
    private static final int RED_SQUARE = 0xFFFF3333;
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
                drawHintLine(graphics, mc, cx, y, "[Left Click] First Point");
            }
            case CORNER_1_SET -> {
                drawHintLine(graphics, mc, cx, y, "[Right Click] Second Point");
                drawHintLine(graphics, mc, cx, y + 12, "[Scroll] Nudge");
            }
            case BOTH_SET -> {
                drawHintLine(graphics, mc, cx, y, "[Enter] Save");
                drawHintLine(graphics, mc, cx, y + 12, "[Middle Click] Extend | [Esc] Cancel");
            }
            default -> {}
        }
    }

    private static void drawHintLine(GuiGraphics graphics, Minecraft mc, int cx, int y, String text) {
        int textWidth = mc.font.width(text);
        int totalWidth = 10 + textWidth;
        int startX = cx - totalWidth / 2;

        // Red square icon (8x8)
        graphics.fill(startX, y, startX + 8, y + 8, RED_SQUARE);

        // Text with drop shadow
        graphics.drawString(mc.font, text, startX + 10, y, HINT_COLOR, true);
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
