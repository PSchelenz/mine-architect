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
        int currentWidth = (rotation % 2 == 0) ? size.getX() : size.getZ();
        int currentDepth = (rotation % 2 == 0) ? size.getZ() : size.getX();

        float xOffset = -currentWidth / 2f + 1;  // Added +1 to correct the westward shift
        float zOffset = -currentDepth / 2f;

        xOffset += (currentWidth % 2 == 0) ? 0 : 0.5f;
        zOffset += (currentDepth % 2 == 0) ? 0 : 0.5f;

        startingPos = startingPos.offset((int)Math.floor(xOffset), 0, (int)Math.floor(zOffset));

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

    private BlockPos rotatePosition(int x, int y, int z, int rotation) {
        // Calculate center (same as preview renderer)
        float centerX = size.getX() / 2.0f;
        float centerZ = size.getZ() / 2.0f;

        // Offset for odd dimensions
        centerX -= (size.getX() % 2 == 0) ? 0.5f : 0;
        centerZ -= (size.getZ() % 2 == 0) ? 0.5f : 0;

        // Translate to origin
        float translatedX = x - centerX;
        float translatedZ = z - centerZ;

        // Rotate
        float rotatedX = switch (rotation) {
            case 0 -> translatedX;
            case 1 -> -translatedZ;
            case 2 -> -translatedX;
            case 3 -> translatedZ;
            default -> translatedX;
        };

        float rotatedZ = switch (rotation) {
            case 0 -> translatedZ;
            case 1 -> translatedX;
            case 2 -> -translatedZ;
            case 3 -> -translatedX;
            default -> translatedZ;
        };

        // Translate back
        return new BlockPos(
                Math.round(rotatedX + centerX),
                y,
                Math.round(rotatedZ + centerZ)
        );
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
