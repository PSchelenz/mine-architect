package com.dupayou.minearchitect.commands;

import com.dupayou.minearchitect.MineArchitect;
import com.dupayou.minearchitect.data.CreationData;
import com.dupayou.minearchitect.models.ArchitectsPencilItem;
import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.models.CreationBlock;
import com.dupayou.minearchitect.states.PencilSelectionState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MineArchitectCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("pencil")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ItemStack pencil = new ItemStack(MineArchitect.ARCHITECTS_PENCIL.get());
                    pencil.setHoverName(Component.literal("Architect's Pencil"));
                    player.addItem(pencil);
                    return 1;
                })
        );

        dispatcher.register(
            Commands.literal("csave")
                .requires(source -> source.hasPermission(0))
                .then(Commands.argument("name", StringArgumentType.string())
                    .then(Commands.argument("withAir", BoolArgumentType.bool())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(context, "name");
                            boolean withAir = BoolArgumentType.getBool(context, "withAir");

                            BlockPos pos1 = PencilSelectionState.getFirstPos(player.getUUID());
                            BlockPos pos2 = PencilSelectionState.getSecondPos(player.getUUID());

                            if(pos1 == null) {
                                context.getSource().sendFailure(Component.literal("First creation position not set"));
                                return -1;
                            }

                            if(pos2 == null) {
                                context.getSource().sendFailure(Component.literal("Second creation position not set"));
                                return -1;
                            }

                            int minX = Math.min(pos1.getX(), pos2.getX());
                            int minY = Math.min(pos1.getY(), pos2.getY());
                            int minZ = Math.min(pos1.getZ(), pos2.getZ());
                            int maxX = Math.max(pos1.getX(), pos2.getX());
                            int maxY = Math.max(pos1.getY(), pos2.getY());
                            int maxZ = Math.max(pos1.getZ(), pos2.getZ());

                            BlockPos size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
                            Creation creation = new Creation(name, size);

                            for (int y = minY; y <= maxY; y++) {
                                for (int z = minZ; z <= maxZ; z++) {
                                    for (int x = minX; x <= maxX; x++) {
                                        BlockPos currentPos = new BlockPos(x, y, z);
                                        BlockState state = player.level().getBlockState(currentPos);

                                        if (withAir || state.getBlock() != Blocks.AIR) {
                                            creation.setBlock(
                                                    new CreationBlock(
                                                            new BlockPos(x - minX, y - minY, z - minZ),
                                                            state
                                                    ),
                                                    x - minX,
                                                    y - minY,
                                                    z - minZ
                                            );
                                        }
                                    }
                                }
                            }

                            CreationData.getCreationData().addCreation(creation);
                            context.getSource().sendSuccess(() -> Component.literal("Creation " + name + " saved"), true);
                            return 1;
                        })))
        );
    }
}
