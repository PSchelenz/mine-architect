package com.dupayou.minearchitect.model;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class Creation {
    private String name;
    private CreationBlock[][][] blocks;
    private BlockPos size;

    public Creation(String name, BlockPos size) {
        this.name = name;
        this.size = size;
        this.blocks = new CreationBlock[size.getX()][size.getY()][size.getZ()];
        initializeBlocks();
    }

    private Creation() {}

    private void initializeBlocks() {
        for (int x = 0; x < this.size.getX(); x++) {
            for (int y = 0; y < this.size.getY(); y++) {
                for (int z = 0; z < this.size.getZ(); z++) {
                    this.blocks[x][y][z] = new CreationBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    public void setBlock(CreationBlock block, int x, int y, int z) {
        this.blocks[x][y][z] = block;
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getSize() {
        return this.size;
    }

    public void placeCreation(Level level, BlockPos startingPos)
    {
        for (int y = 0; y < this.size.getY(); y++) {
            for (int z = 0; z < this.size.getZ(); z++) {
                for (int x = 0; x < this.size.getX(); x++) {
                    CreationBlock block = this.blocks[x][y][z];

                    if (block.getBlockState().getBlock() != Blocks.AIR) {
                        BlockState blockState = block.getBlockState();

                        level.setBlock(
                                new BlockPos(
                                        startingPos.getX() + x,
                                        startingPos.getY() + y,
                                        startingPos.getZ() + z
                                ),
                                blockState,
                                3
                        );
                    }
                }
            }
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);

        tag.putInt("SizeX", size.getX());
        tag.putInt("SizeY", size.getY());
        tag.putInt("SizeZ", size.getZ());

        ListTag blockList = new ListTag();
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (blocks[x][y][z].getBlockState().getBlock() != Blocks.AIR) {
                        blockList.add(blocks[x][y][z].toNBT());
                    }
                }
            }
        }
        tag.put("Blocks", blockList);

        return tag;
    }

    public static Creation fromNBT(CompoundTag tag) {
        Creation creation = new Creation();
        creation.name = tag.getString("Name");

        int sizeX = tag.getInt("SizeX");
        int sizeY = tag.getInt("SizeY");
        int sizeZ = tag.getInt("SizeZ");
        creation.size = new BlockPos(sizeX, sizeY, sizeZ);

        creation.blocks = new CreationBlock[sizeX][sizeY][sizeZ];
        creation.initializeBlocks();

        ListTag blockList = tag.getList("Blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blockList.size(); i++) {
            CreationBlock blockInfo = CreationBlock.fromNBT(blockList.getCompound(i));
            BlockPos pos = blockInfo.getRelativePos();
            creation.blocks[pos.getX()][pos.getY()][pos.getZ()] = blockInfo;
        }

        return creation;
    }
}
