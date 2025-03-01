package com.dupayou.minearchitect.models;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

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

    public CreationBlock getBlock(int x, int y, int z) {
        return this.blocks[x][y][z];
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getSize() {
        return this.size;
    }

    public void placeCreation(Level level, BlockPos startingPos, int rotation) {
        for (int y = 0; y < this.size.getY(); y++) {
            for (int z = 0; z < this.size.getZ(); z++) {
                for (int x = 0; x < this.size.getX(); x++) {
                    CreationBlock block = this.blocks[x][y][z];
                    if (block.getBlockState().getBlock() != Blocks.AIR) {
                        BlockPos pos = switch (rotation) {
                            case 1 -> new BlockPos(z, y, size.getX() - 1 - x);
                            case 2 -> new BlockPos(size.getX() - 1 - x, y, size.getZ() - 1 - z);
                            case 3 -> new BlockPos(size.getZ() - 1 - z, y, x);
                            default -> new BlockPos(x, y, z);
                        };

                        BlockState rotatedState = block.getBlockState().rotate(level, startingPos.offset(pos),
                                switch(rotation) {
                                    case 1 -> Rotation.CLOCKWISE_90;
                                    case 2 -> Rotation.CLOCKWISE_180;
                                    case 3 -> Rotation.COUNTERCLOCKWISE_90;
                                    default -> Rotation.NONE;
                                }
                        );

                        level.setBlock(startingPos.offset(pos), rotatedState, 3);
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
