package com.dupayou.minearchitect.states;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PencilSelectionState {
    private static final Map<UUID, BlockPos> firstPositions = new HashMap<>();
    private static final Map<UUID, BlockPos> secondPositions = new HashMap<>();

    public static void setFirstPos(UUID playerId, BlockPos pos) {
        firstPositions.put(playerId, pos);
    }

    public static void setSecondPos(UUID player, BlockPos pos) {
        secondPositions.put(player, pos);
    }

    public static BlockPos getFirstPos(UUID player) {
        return firstPositions.get(player);
    }

    public static BlockPos getSecondPos(UUID player) {
        return secondPositions.get(player);
    }
}
