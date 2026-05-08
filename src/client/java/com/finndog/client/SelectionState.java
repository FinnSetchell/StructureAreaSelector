package com.finndog.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;

@Environment(EnvType.CLIENT)
public class SelectionState {

    public enum State {
        IDLE,
        CONFIGURING,
        SELECTING,
        CORNER_1_SET,
        BOTH_SET
    }

    private static final SelectionState INSTANCE = new SelectionState();

    private State state = State.IDLE;
    private BlockPos structureBlockPos;
    private String structureName = "";
    private boolean includeEntities = true;
    private BlockPos corner1;
    private BlockPos corner2;
    private int lastSetCorner = 0;
    private long savedMessageEndTick = -1;

    private SelectionState() {}

    public static SelectionState getInstance() {
        return INSTANCE;
    }

    public State getState() { return state; }
    public BlockPos getStructureBlockPos() { return structureBlockPos; }
    public String getStructureName() { return structureName; }
    public boolean isIncludeEntities() { return includeEntities; }
    public BlockPos getCorner1() { return corner1; }
    public BlockPos getCorner2() { return corner2; }
    public int getLastSetCorner() { return lastSetCorner; }

    public boolean isActive() {
        return state == State.SELECTING || state == State.CORNER_1_SET || state == State.BOTH_SET;
    }

    public void startConfig(BlockPos pos) {
        reset();
        this.structureBlockPos = pos;
        this.state = State.CONFIGURING;
    }

    public void beginSelection(String name, boolean entities) {
        this.structureName = name;
        this.includeEntities = entities;
        this.state = State.SELECTING;
    }

    public void setCorner1(BlockPos pos) {
        this.corner1 = pos;
        this.lastSetCorner = 1;
        updateSelectionState();
    }

    public void setCorner2(BlockPos pos) {
        this.corner2 = pos;
        this.lastSetCorner = 2;
        updateSelectionState();
    }

    public void expandToInclude(BlockPos pos) {
        if (corner1 == null || corner2 == null) return;
        BlockPos min = getMinCorner();
        BlockPos max = getMaxCorner();
        corner1 = new BlockPos(
                Math.min(min.getX(), pos.getX()),
                Math.min(min.getY(), pos.getY()),
                Math.min(min.getZ(), pos.getZ())
        );
        corner2 = new BlockPos(
                Math.max(max.getX(), pos.getX()),
                Math.max(max.getY(), pos.getY()),
                Math.max(max.getZ(), pos.getZ())
        );
    }

    private void updateSelectionState() {
        if (corner1 != null && corner2 != null) {
            state = State.BOTH_SET;
        } else if (corner1 != null) {
            state = State.CORNER_1_SET;
        } else {
            state = State.SELECTING;
        }
    }

    public BlockPos getMinCorner() {
        if (corner1 == null || corner2 == null) {
            return corner1 != null ? corner1 : corner2;
        }
        return new BlockPos(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
    }

    public BlockPos getMaxCorner() {
        if (corner1 == null || corner2 == null) {
            return corner1 != null ? corner1 : corner2;
        }
        return new BlockPos(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
    }

    public Vec3i getSize() {
        if (corner1 == null || corner2 == null) return Vec3i.ZERO;
        BlockPos min = getMinCorner();
        BlockPos max = getMaxCorner();
        return new Vec3i(
                max.getX() - min.getX() + 1,
                max.getY() - min.getY() + 1,
                max.getZ() - min.getZ() + 1
        );
    }

    public boolean isValidSize() {
        Vec3i size = getSize();
        return size.getX() > 0 && size.getX() <= 48
                && size.getY() > 0 && size.getY() <= 48
                && size.getZ() > 0 && size.getZ() <= 48;
    }

    public void confirm() {
        if (state != State.BOTH_SET) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return;

        BlockPos min = getMinCorner();
        Vec3i size = getSize();
        Vec3i clampedSize = new Vec3i(
                Math.min(size.getX(), 48),
                Math.min(size.getY(), 48),
                Math.min(size.getZ(), 48)
        );

        BlockPos offset = new BlockPos(
                min.getX() - structureBlockPos.getX(),
                min.getY() - structureBlockPos.getY(),
                min.getZ() - structureBlockPos.getZ()
        );

        ServerboundSetStructureBlockPacket packet = new ServerboundSetStructureBlockPacket(
                structureBlockPos,
                StructureBlockEntity.UpdateType.SAVE_AREA,
                StructureMode.SAVE,
                structureName,
                offset,
                clampedSize,
                Mirror.NONE,
                Rotation.NONE,
                "",
                !includeEntities,
                false,
                false,
                true,
                1.0f,
                0L
        );

        mc.getConnection().send(packet);

        if (mc.level != null) {
            savedMessageEndTick = mc.level.getGameTime() + 60;
        }
        reset();
    }

    public void cancel() {
        reset();
    }

    private void reset() {
        state = State.IDLE;
        structureBlockPos = null;
        structureName = "";
        includeEntities = true;
        corner1 = null;
        corner2 = null;
        lastSetCorner = 0;
    }

    public boolean shouldShowSavedMessage() {
        if (savedMessageEndTick < 0) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;
        boolean show = mc.level.getGameTime() < savedMessageEndTick;
        if (!show) savedMessageEndTick = -1;
        return show;
    }
}
