package com.dupayou.minearchitect.screens;

import com.dupayou.minearchitect.MineArchitect;
import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.models.CreationBlock;
import com.dupayou.minearchitect.states.CreationPlacementState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CreationsScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + MineArchitect.MOD_ID + ".creations_screen.title");
    private static final ResourceLocation TEXTURE = new ResourceLocation(MineArchitect.MOD_ID, "textures/gui/creations_screen.png");

    private final int imageWidth, imageHeight;
    private int leftPos, topPos;
    private float rotationAngle = 0;
    private float rotationX = 30f;
    private float rotationY = 45f;
    private float scale = 30f;
    private final int currentPage;
    private final Screen parent;

    private final Creation creation;

    public CreationsScreen(Creation creation, Screen parent, int currentPage) {
        super(TITLE);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.currentPage = currentPage;
        this.parent = parent;
        this.creation = creation;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        addRenderableWidget(Button.builder(Component.literal("Back"), button -> {
            if (parent instanceof GridCreationsListScreen listScreen) {
                minecraft.setScreen(new GridCreationsListScreen(currentPage));
            }
        }).bounds(10, 10, 50, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Use"), button -> {
            CreationPlacementState.setActiveCreation(creation);
            minecraft.setScreen(null);
        }).bounds(width - 60, 10, 50, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Center of the screen
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Handle rotation
        if (this.minecraft.mouseHandler.isLeftPressed()) {
            rotationY += this.minecraft.mouseHandler.xpos() * 0.5f;
            rotationX -= this.minecraft.mouseHandler.ypos() * 0.5f;
        }

        // Handle zoom
        double dWheel = this.minecraft.mouseHandler.getXVelocity();
        if (dWheel != 0) {
            scale += dWheel > 0 ? 1f : -1f;
            scale = Math.max(10f, Math.min(50f, scale));
        }

        // Handle structure rendering
        renderStructure(graphics, centerX, centerY);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderStructure(GuiGraphics graphics, int centerX, int centerY) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100f);
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(rotationX));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationY));

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BlockPos size = creation.getSize();
        Vector3f center = new Vector3f(size.getX() / 2f, size.getY() / 2f, size.getZ() / 2f);

        ItemRenderer itemRenderer = this.minecraft.getItemRenderer();

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    CreationBlock block = creation.getBlock(x, y, z);
                    if (block.getBlockState().getBlock() != Blocks.AIR) {
                        BlockPos relativePos = block.getRelativePos().subtract(
                                new BlockPos((int)center.x(), (int)center.y(), (int)center.z()));

                        poseStack.pushPose();
                        poseStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
                        poseStack.scale(2f, 2f, 2f);

                        ItemStack itemStack = block.getBlockState().getBlock().asItem().getDefaultInstance();
                        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, 15728880, 655360,
                                poseStack, graphics.bufferSource(), this.minecraft.level, 0);

                        poseStack.popPose();
                    }
                }
            }
        }

        graphics.flush();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {  // Left mouse button
            rotationY += dragX;
            rotationX += dragY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scale += delta > 0 ? 1f : -1f;
        scale = Math.max(10f, Math.min(50f, scale));
        return true;
    }

    private void handleExampleButton(Button button) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
