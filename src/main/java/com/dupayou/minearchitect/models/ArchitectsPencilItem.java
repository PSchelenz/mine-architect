package com.dupayou.minearchitect.models;

import com.dupayou.minearchitect.states.PencilSelectionState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ArchitectsPencilItem extends Item {
    public ArchitectsPencilItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if(!context.getLevel().isClientSide && player != null) {
            PencilSelectionState.setSecondPos(player.getUUID(), pos);
            player.sendSystemMessage(Component.literal("Second position set at: " + + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (!player.level().isClientSide) {
            PencilSelectionState.setFirstPos(player.getUUID(), pos);
            player.sendSystemMessage(Component.literal("First position set at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
        }

        return true;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
