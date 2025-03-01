package com.dupayou.minearchitect.client;

import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.states.CreationPlacementState;
import com.dupayou.minearchitect.utils.LandscapeObserver;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreationPreviewRenderer {
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Creation activeCreation = CreationPlacementState.getActiveCreation();
        if(activeCreation == null) return;

        Minecraft mc = Minecraft.getInstance();

        // Check for any hit result, not just within reach
        if(mc.hitResult != null) {
            BlockPos pos = CreationPlacementState.toCenter();
            BlockPos size = activeCreation.getSize();
            int rotation = CreationPlacementState.getRotation();

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            var camera = mc.gameRenderer.getMainCamera();
            double camX = camera.getPosition().x;
            double camY = camera.getPosition().y;
            double camZ = camera.getPosition().z;
            poseStack.translate(-camX, -camY, -camZ);

            // Setup for transparent rendering
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();

            // First draw the outline box
//            LevelRenderer.renderLineBox(
//                    poseStack,
//                    bufferSource.getBuffer(RenderType.lines()),
//                    pos.getX(), pos.getY(), pos.getZ(),
//                    pos.getX() + rotatedSize.getX(), pos.getY() + rotatedSize.getY(), pos.getZ() + rotatedSize.getZ(),
//                    1.0F, 1.0F, 1.0F, 0.8F,
//                    1.0F, 1.0F, 1.0F
//            );

            // Now render each block in the creation with transparency
            for(int x = 0; x < size.getX(); x++) {
                for(int y = 0; y < size.getY(); y++) {
                    for(int z = 0; z < size.getZ(); z++) {
                        BlockState blockState = activeCreation.getBlock(x, y, z).getBlockState();

                        if(!blockState.isAir()) {
                            // Important: Use the exact same rotation logic as in placeCreation
                            BlockPos rotatedPos = switch (rotation) {
                                case 1 -> new BlockPos(z, y, size.getX() - 1 - x);
                                case 2 -> new BlockPos(size.getX() - 1 - x, y, size.getZ() - 1 - z);
                                case 3 -> new BlockPos(size.getZ() - 1 - z, y, x);
                                default -> new BlockPos(x, y, z);
                            };

                            BlockPos finalPos = pos.offset(rotatedPos);

                            poseStack.pushPose();
                            poseStack.translate(finalPos.getX(), finalPos.getY(), finalPos.getZ());

                            // Render the block with transparency
                            int packedLight = 0xF000F0; // Full light

                            try {
                                blockRenderer.renderSingleBlock(
                                        blockState,
                                        poseStack,
                                        bufferSource,
                                        packedLight,
                                        OverlayTexture.NO_OVERLAY,
                                        ModelData.EMPTY,
                                        RenderType.translucent()
                                );
                            } catch (Exception e) {
                                // Just in case a block causes issues
                            }

                            poseStack.popPose();
                        }
                    }
                }
            }

            // Finish rendering
            bufferSource.endBatch();
            RenderSystem.disableBlend();

            poseStack.popPose();
        }
    }
}