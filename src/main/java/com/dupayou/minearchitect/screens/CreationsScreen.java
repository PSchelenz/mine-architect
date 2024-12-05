package com.dupayou.minearchitect.screens;

import com.dupayou.minearchitect.MineArchitect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;

public class CreationsScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + MineArchitect.MOD_ID + ".creations_screen.title");
    private static final Component EX_BUTTON_TEXT = Component.translatable("gui." + MineArchitect.MOD_ID + ".creations_screen.buttons.ex");

    private static final ResourceLocation TEXTURE = new ResourceLocation(MineArchitect.MOD_ID, "textures/gui/creations_screen.png");

    private final int imageWidth, imageHeight;

    private int leftPos, topPos;
    private Button button;

    private float rotationAngle = 0;

    private final Map<BlockPos, ItemStack> structure;
    private float rotationX = 30f;
    private float rotationY = 45f;
    private float scale = 30f;

    public CreationsScreen(Map<BlockPos, ItemStack> structure) {
        super(TITLE);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.structure = structure;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;


//        this.button = addRenderableWidget(
//                Button.builder(EX_BUTTON_TEXT, this::handleExampleButton)
//                        .bounds(this.leftPos + 8, this.topPos + 20, 100, 20)
//                        .tooltip(Tooltip.create(EX_BUTTON_TEXT))
//                        .build()
//        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Center of the screen
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Handle rotation
        if (this.minecraft.mouseHandler.isLeftPressed()) {
            MineArchitect.LOGGER.info("Mouse moved to pos: " + this.minecraft.mouseHandler.xpos() + ", " + this.minecraft.mouseHandler.ypos());
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

        Vector3f center = new Vector3f();
        for (BlockPos pos : structure.keySet()) {
            center.add(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
        }
        center.div(structure.size());

        ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
        for (Map.Entry<BlockPos, ItemStack> entry : structure.entrySet()) {
            BlockPos relativePos = entry.getKey().subtract(new BlockPos((int)center.x(), (int)center.y(), (int)center.z()));
            poseStack.pushPose();
            poseStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
            poseStack.scale(2f, 2f, 2f);
            itemRenderer.renderStatic(entry.getValue(), ItemDisplayContext.FIXED, 15728880, 655360, poseStack, graphics.bufferSource(), this.minecraft.level, 0);
            poseStack.popPose();
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
