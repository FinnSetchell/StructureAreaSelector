package com.finndog.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;

@Environment(EnvType.CLIENT)
public final class FacingAxisHelper {

    private FacingAxisHelper() {}

    /**
     * Maps player pitch/yaw to the cardinal direction they are facing.
     * Used for scroll-nudge: scroll moves the corner along this axis.
     */
    public static Direction getFacingDirection(float pitch, float yaw) {
        if (Math.abs(pitch) > 45) {
            // Looking up (pitch < 0) → UP, looking down (pitch > 0) → DOWN
            return pitch > 0 ? Direction.DOWN : Direction.UP;
        }
        float normalized = ((yaw % 360) + 360) % 360;
        if (normalized >= 315 || normalized < 45) {
            return Direction.SOUTH;
        } else if (normalized < 135) {
            return Direction.WEST;
        } else if (normalized < 225) {
            return Direction.NORTH;
        } else {
            return Direction.EAST;
        }
    }
}
