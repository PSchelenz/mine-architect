package com.dupayou.minearchitect.utils;

import com.dupayou.minearchitect.data.CreationData;
import com.dupayou.minearchitect.models.Creation;
import com.dupayou.minearchitect.models.CreationBlock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JsonBlockLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    public static void loadAndSaveCreations() {
        fetchJsonAsync("http://127.0.0.1:8000/api/load")
                .thenAccept(blockArray -> saveBlocks(blockArray))
                .exceptionally(e -> {
                    LOGGER.error("Failed to load and place blocks", e);
                    return null;
                });
    }

    private static void saveBlocks(List<List<List<String>>> blockArray) {
        CreationData data = CreationData.getCreationData();
        int ySize = blockArray.size();
        int zSize = blockArray.get(0).size();
        int xSize = blockArray.get(0).get(0).size();

        Creation creation = new Creation("stonewall", new BlockPos(xSize, ySize, zSize));

        for (int y = 0; y < blockArray.size(); y++) {
            for (int z = 0; z < blockArray.get(y).size(); z++) {
                for (int x = 0; x < blockArray.get(y).get(z).size(); x++) {
                    String blockId = blockArray.get(y).get(z).get(x);
                    if (blockId != null && !blockId.isEmpty()) {
                        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));

                        if (block != null) {
                            CreationBlock creationBlock = new CreationBlock(new BlockPos(x, y, z), block.defaultBlockState());
                            creation.setBlock(creationBlock, x, y, z);
                        } else {
                            LOGGER.warn("Unknown block ID: " + blockId);
                        }
                    }
                }
            }
        }
        LOGGER.info("Finished saving blocks");

        data.addCreation(creation);
    }

    public static CompletableFuture<List<List<List<String>>>> fetchJsonAsync(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Type listType = new TypeToken<List<List<List<String>>>>(){}.getType();
                    return GSON.fromJson(content.toString(), listType);
                } else {
                    LOGGER.error("HTTP request failed. Response Code: " + responseCode);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                LOGGER.error("Failed to fetch JSON data", e);
                return new ArrayList<>();
            }
        });
    }
}
