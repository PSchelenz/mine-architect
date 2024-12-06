package com.dupayou.minearchitect.network;

import com.dupayou.minearchitect.MineArchitect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MineArchitect.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.registerMessage(0, PlaceCreationMessage.class,
                PlaceCreationMessage::encode,
                PlaceCreationMessage::decode,
                PlaceCreationMessage::handle
        );
    }
}
