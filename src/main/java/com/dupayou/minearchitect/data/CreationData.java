package com.dupayou.minearchitect.data;

import com.dupayou.minearchitect.MineArchitect;
import com.dupayou.minearchitect.models.Creation;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreationData extends SavedData {
    public static final String DATA_NAME = MineArchitect.MOD_ID + "_creations";
    public Map<String, Creation> creations;

    public CreationData() {
        this.creations = new HashMap<>();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag creationsList = new ListTag();

        for (Creation creation : creations.values()) {
            creationsList.add(creation.toNBT());
        }

        tag.put("Creations", creationsList);

        return tag;
    }

    public static CreationData load(CompoundTag tag) {
        CreationData data = new CreationData();
        ListTag creationsList = tag.getList("Creations", ListTag.TAG_COMPOUND);

        for (int i = 0; i < creationsList.size(); i++) {
            CompoundTag creationTag = creationsList.getCompound(i);
            LogUtils.getLogger().info("Loading creation from NBT: " + creationTag);
            Creation creation = Creation.fromNBT(creationTag);

            data.creations.put(creation.getName(), creation);
        }

        return data;
    }

    public void addCreation(Creation creation) {
        creations.put(creation.getName(), creation);
        this.setDirty();
    }

    public Creation getCreation(String name) {
        return creations.get(name);
    }

    public static CreationData getCreationData() {
        ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();

        return world.getDataStorage().computeIfAbsent(CreationData::load, CreationData::new, DATA_NAME);
    }
}
