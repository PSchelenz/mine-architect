package com.dupayou.minearchitect.screens;

import com.dupayou.minearchitect.MineArchitect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MenuScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + MineArchitect.MOD_ID + ".menu_screen.title");
    private static final Component CREATIONS_BUTTON = Component.translatable("gui." + MineArchitect.MOD_ID + ".menu_screen.buttons.creations");
    private static final Component SOMETHING_BUTTON = Component.translatable("gui." + MineArchitect.MOD_ID + ".menu_screen.buttons.something");

    private static final ResourceLocation BACKGROUND = new ResourceLocation(MineArchitect.MOD_ID, "textures/gui/menu_screen.png");

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 24;

    private final int imageWidth = 248;
    private final int imageHeight = 200;
    private int leftPos, topPos;

    public MenuScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        int buttonX = leftPos + (imageWidth - BUTTON_WIDTH) / 2;
        int firstButtonY = topPos + 60;

        addRenderableWidget(Button.builder(CREATIONS_BUTTON, button -> {
            minecraft.setScreen(new GridCreationsListScreen(0));
        })
            .bounds(buttonX, firstButtonY, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());

        addRenderableWidget(Button.builder(SOMETHING_BUTTON, button -> {

        })
            .bounds(buttonX, firstButtonY + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT)
            .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageWidth, 0x80000000);

        graphics.drawCenteredString(
            minecraft.font,
            getTitle(),
            width / 2,
            topPos + 20,
            0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
