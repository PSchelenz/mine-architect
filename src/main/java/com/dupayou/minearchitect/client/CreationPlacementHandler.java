package com.dupayou.minearchitect.client;

import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.network.NetworkHandler;
import com.dupayou.minearchitect.network.PlaceCreationMessage;
import com.dupayou.minearchitect.states.CreationPlacementState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class CreationPlacementHandler {
    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseButton.Post event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

        Creation activeCreation = CreationPlacementState.getActiveCreation();
        if (activeCreation == null) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.hitResult instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos().above();
            BlockPos size = activeCreation.getSize();
            pos = pos.offset(-size.getX() / 2, 0, -size.getZ() / 2);

            NetworkHandler.INSTANCE.sendToServer(new PlaceCreationMessage(activeCreation.getName(), pos, CreationPlacementState.getRotation()));
            CreationPlacementState.clearActiveCreation();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_R && event.getAction() == GLFW.GLFW_PRESS
                && CreationPlacementState.getActiveCreation() != null) {
            CreationPlacementState.rotate();
        }
    }
}
