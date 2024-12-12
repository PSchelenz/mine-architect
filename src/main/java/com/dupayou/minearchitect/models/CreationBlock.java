package com.dupayou.minearchitect.models;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class CreationBlock {
    private BlockPos relativePos;
    private BlockState blockState;

    public CreationBlock(BlockPos relativePos, BlockState blockState) {
        this.relativePos = relativePos;
        this.blockState = blockState;
    }

    public BlockPos getRelativePos() {
        return relativePos;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        // Save relative position
        tag.putInt("X", relativePos.getX());
        tag.putInt("Y", relativePos.getY());
        tag.putInt("Z", relativePos.getZ());

        // Save block state
        tag.putString("Block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(blockState.getBlock())).toString());

        // Save block state properties
        CompoundTag propertiesTag = new CompoundTag();
        for (Property<?> property : blockState.getProperties()) {
            String propertyName = property.getName();
            String valueString = blockState.getValue(property).toString();
            propertiesTag.putString(propertyName, valueString);
        }
        tag.put("Properties", propertiesTag);

        return tag;
    }

    public static CreationBlock fromNBT(CompoundTag tag) {
        // Load relative position
        BlockPos relativePos = new BlockPos(
                tag.getInt("X"),
                tag.getInt("Y"),
                tag.getInt("Z")
        );

        // Load block
        ResourceLocation blockRL = new ResourceLocation(tag.getString("Block"));
        Block block = ForgeRegistries.BLOCKS.getValue(blockRL);
        if (block == null) {
            block = Blocks.AIR; // Fallback if block is not found
        }

        // Load block state
        BlockState blockState = block.defaultBlockState();
        CompoundTag propertiesTag = tag.getCompound("Properties");
        for (Property<?> property : blockState.getProperties()) {
            String propertyName = property.getName();
            if (propertiesTag.contains(propertyName)) {
                String valueString = propertiesTag.getString(propertyName);
                blockState = setPropertyValue(blockState, property, valueString);
            }
        }

        return new CreationBlock(relativePos, blockState);
    }

    private static <T extends Comparable<T>> BlockState setPropertyValue(BlockState state, Property<T> property, String valueString) {
        return state.setValue(property, property.getValue(valueString).orElseThrow(() ->
                new IllegalArgumentException("Invalid property value: " + valueString + " for property: " + property.getName())
        ));
    }
}
