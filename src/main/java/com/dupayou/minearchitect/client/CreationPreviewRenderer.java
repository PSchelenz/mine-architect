package com.dupayou.minearchitect.client;

import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.states.CreationPlacementState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreationPreviewRenderer {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Creation activeCreation = CreationPlacementState.getActiveCreation();
        if(activeCreation == null) return;

        Minecraft mc = Minecraft.getInstance();

        if(mc.hitResult instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos().above();
            BlockPos size = activeCreation.getSize();
            int rotation = CreationPlacementState.getRotation();

            // Determine current dimensions based on rotation
            int currentWidth = (rotation % 2 == 0) ? size.getX() : size.getZ();
            int currentDepth = (rotation % 2 == 0) ? size.getZ() : size.getX();

            // Calculate offsets considering both odd and even dimensions
            float xOffset = -currentWidth / 2f;
            float zOffset = -currentDepth / 2f;

            // For odd dimensions, we need to shift by 0.5 blocks
            xOffset += (currentWidth % 2 == 0) ? 0 : 0.5f;
            zOffset += (currentDepth % 2 == 0) ? 0 : 0.5f;

            BlockPos rotatedSize = switch (rotation) {
                case 1, 3 -> new BlockPos(size.getZ(), size.getY(), size.getX());
                default -> size;
            };

            pos = pos.offset((int)Math.floor(xOffset), 0, (int)Math.floor(zOffset));

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            var camera = mc.gameRenderer.getMainCamera();
            double camX = camera.getPosition().x;
            double camY = camera.getPosition().y;
            double camZ = camera.getPosition().z;
            poseStack.translate(-camX, -camY, -camZ);

            LevelRenderer.renderLineBox(
                    poseStack,
                    mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()),
                    pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + rotatedSize.getX(),
                    pos.getY() + rotatedSize.getY(),
                    pos.getZ() + rotatedSize.getZ(),
                    1.0F, 1.0F, 1.0F, 1.0F,
                    1.0F, 1.0F, 1.0F
            );

            poseStack.popPose();
        }
    }
}
