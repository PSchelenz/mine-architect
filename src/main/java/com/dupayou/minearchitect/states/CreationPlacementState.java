package com.dupayou.minearchitect.states;

import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.utils.LandscapeObserver;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class CreationPlacementState {
    private static Creation activeCreation = null;
    private static int rotation = 0;

    public static void setActiveCreation(Creation creation) {
        activeCreation = creation;
        rotation = 0;
    }

    public static void rotate() {
        rotation = (rotation + 1) % 4;
    }

    public static BlockPos toCenter() {
        BlockPos size = activeCreation.getSize();
        BlockPos pos = LandscapeObserver.getTargetedBlockPos(Minecraft.getInstance());

        // Calculate rotated size - this changes based on rotation
        BlockPos rotatedSize = switch (rotation) {
            case 1, 3 -> new BlockPos(size.getZ(), size.getY(), size.getX());
            default -> size;
        };

        // Use the same offset calculation as the placement handler
        return pos.offset(-rotatedSize.getX() / 2, 0, -rotatedSize.getZ() / 2);
    }

    public static Creation getActiveCreation() {
        return activeCreation;
    }

    public static int getRotation() {
        return rotation;
    }

    public static void clearActiveCreation() {
        activeCreation = null;
        rotation = 0;
    }
}
