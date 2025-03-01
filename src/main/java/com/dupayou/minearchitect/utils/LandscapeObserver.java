package com.dupayou.minearchitect.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LandscapeObserver {
    /**
     * Performs a raytrace to find a block position at distance
     * This allows previewing structures beyond player reach
     */
    public static BlockPos getTargetedBlockPos(Minecraft mc) {
        // Get player position and look vector
        var player = mc.player;
        if (player == null || mc.level == null) return null;

        var eyePos = player.getEyePosition();
        var lookVec = player.getViewVector(1.0F);

        // Calculate a point 100 blocks away in the looking direction
        double reachDistance = 100.0;
        double rayX = eyePos.x + (lookVec.x * reachDistance);
        double rayY = eyePos.y + (lookVec.y * reachDistance);
        double rayZ = eyePos.z + (lookVec.z * reachDistance);
        Vec3 endPos = new Vec3(rayX, rayY, rayZ);

        // Create a proper ClipContext
        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        );

        // Use the level's clip method with the context
        BlockHitResult result = mc.level.clip(context);

        if (result.getType() == HitResult.Type.BLOCK) {
            // We hit a block, return it
            return result.getBlockPos().above();
        } else {
            // No block hit, return null
            return null;
        }
    }
}
