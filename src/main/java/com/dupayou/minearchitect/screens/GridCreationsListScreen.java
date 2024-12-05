package com.dupayou.minearchitect.screens;

import com.dupayou.minearchitect.MineArchitect;
import com.dupayou.minearchitect.data.CreationData;
import com.dupayou.minearchitect.model.Creation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GridCreationsListScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui." + MineArchitect.MOD_ID + ".grid_creations_list_screen.title");
    private static final ResourceLocation ACTION_ICON = new ResourceLocation(MineArchitect.MOD_ID, "textures/gui/action_icon.png");

    private final Screen parent;
    private final int CREATION_CONTAINER_WIDTH = 120;
    private final int CREATION_CONTAINER_HEIGHT = 140;
    private final int GRID_SPACING = 10;
    private final int ACTION_BUTTON_SIZE = 16;

    private List<Button> actionButtons = new ArrayList<>();
    private int hoveredCreationIndex = -1;
    private final List<Creation> creationsList;

    public GridCreationsListScreen(Screen parent) {
        super(TITLE);

        this.parent = parent;
        this.creationsList = new ArrayList<>(CreationData.getCreationData().creations.values());
    }

    @Override
    protected void init() {
        super.init();
        actionButtons.clear();

        int startX = (width - (CREATION_CONTAINER_WIDTH * 3 + GRID_SPACING * 2)) / 2;
        int startY = 50;

        for (int i = 0; i < creationsList.size(); i++) {
            final int index = i;
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * (CREATION_CONTAINER_WIDTH + GRID_SPACING);
            int y = startY + row * (CREATION_CONTAINER_HEIGHT + GRID_SPACING);

            Button actionButton = Button.builder(Component.empty(), button -> {
                handleCreationAction(index);
            })
                .bounds(x + CREATION_CONTAINER_WIDTH - ACTION_BUTTON_SIZE - 4,
                        y + CREATION_CONTAINER_HEIGHT - ACTION_BUTTON_SIZE - 4,
                        ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE)
                .build();

            actionButtons.add(actionButton);
            addRenderableWidget(actionButton);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(minecraft.font, getTitle(), width / 2, 20, 0xFFFFFF);

        int startX = (width - (CREATION_CONTAINER_WIDTH * 3 + GRID_SPACING * 2)) / 2;
        int startY = 50;
        hoveredCreationIndex = -1;

        for (int i = 0; i < creationsList.size(); i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * (CREATION_CONTAINER_WIDTH + GRID_SPACING);
            int y = startY + row * (CREATION_CONTAINER_HEIGHT + GRID_SPACING);

            Creation creation = creationsList.get(i);
            renderCreationCard(graphics, creation, x, y);

            if (isMouseOver(mouseX, mouseY, x, y)) {
                hoveredCreationIndex = i;
                graphics.fill(x, y, x + CREATION_CONTAINER_WIDTH, y + CREATION_CONTAINER_HEIGHT, 0x20FFFFFF);
            }
        }

        updateActionButtonsVisibility();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderCreationCard(GuiGraphics graphics, Creation creation, int x, int y) {
        graphics.fill(x, y, x + CREATION_CONTAINER_WIDTH, y + CREATION_CONTAINER_HEIGHT, 0x80000000);

        // Preview area
        graphics.fill(x + 10, y + 10, x + CREATION_CONTAINER_WIDTH - 10, y + CREATION_CONTAINER_HEIGHT - 30, 0x40FFFFFF);

        // Name
        graphics.drawCenteredString(minecraft.font, creation.getName(),
                x + CREATION_CONTAINER_WIDTH / 2,
                y + CREATION_CONTAINER_HEIGHT - 20,
                0xFFFFFF);

        // Size info
        BlockPos size = creation.getSize();
        String sizeText = size.getX() + "x" + size.getY() + "x" + size.getZ();
        graphics.drawString(minecraft.font, sizeText,
                x + 10,
                y + CREATION_CONTAINER_HEIGHT - 30,
                0xAAAAAA);
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX <= x + CREATION_CONTAINER_WIDTH &&
                mouseY >= y && mouseY <= y + CREATION_CONTAINER_HEIGHT;
    }

    private void updateActionButtonsVisibility() {
        for (int i = 0; i < actionButtons.size(); i++) {
            if (i == hoveredCreationIndex) {
                addWidget(actionButtons.get(i));
            } else {
                removeWidget(actionButtons.get(i));
            }
        }
    }

    private void handleCreationAction(int index) {

    }
}
