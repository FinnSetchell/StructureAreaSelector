package com.finndog.client.screen;

import com.finndog.client.SelectionState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class StructureConfigScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 96;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 26;

    private EditBox nameField;
    private Checkbox includeEntitiesCheckbox;
    private Button beginButton;

    public StructureConfigScreen() {
        super(Component.literal("Structure Saver"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2 - 20;

        this.nameField = new EditBox(this.font, cx - FIELD_WIDTH / 2, cy - SPACING, FIELD_WIDTH, FIELD_HEIGHT,
                Component.literal("Structure Name"));
        this.nameField.setMaxLength(128);
        this.nameField.setFilter(s -> s.matches("[a-z0-9/._-]*"));
        this.nameField.setHint(Component.literal("structure_name"));
        this.nameField.setResponder(this::onNameChanged);
        this.addRenderableWidget(this.nameField);

        this.includeEntitiesCheckbox = Checkbox.builder(Component.literal("Include Entities"), this.font)
                .pos(cx - FIELD_WIDTH / 2, cy + 4)
                .selected(true)
                .build();
        this.addRenderableWidget(this.includeEntitiesCheckbox);

        this.beginButton = Button.builder(Component.literal("Begin Selection"), btn -> {
            String name = this.nameField.getValue().trim();
            if (!name.isEmpty()) {
                SelectionState.getInstance().beginSelection(name, this.includeEntitiesCheckbox.selected());
                this.onClose();
            }
        }).bounds(cx - BUTTON_WIDTH - 4, cy + SPACING + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.beginButton.active = false;
        this.addRenderableWidget(this.beginButton);

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> this.onClose())
                .bounds(cx + 4, cy + SPACING + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        this.setInitialFocus(this.nameField);
    }

    private void onNameChanged(String name) {
        this.beginButton.active = !name.trim().isEmpty();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 56, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        SelectionState state = SelectionState.getInstance();
        if (state.getState() == SelectionState.State.CONFIGURING) {
            state.cancel();
        }
        super.onClose();
    }
}
