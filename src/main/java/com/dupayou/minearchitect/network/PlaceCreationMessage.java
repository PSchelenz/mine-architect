package com.dupayou.minearchitect.network;

import com.dupayou.minearchitect.data.CreationData;
import com.dupayou.minearchitect.model.Creation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaceCreationMessage {
    private final String creationName;
    private final BlockPos pos;
    private final int rotation;

    public PlaceCreationMessage(String creationName, BlockPos pos, int rotation) {
        this.creationName = creationName;
        this.pos = pos;
        this.rotation = rotation;
    }

    public static void encode(PlaceCreationMessage msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.creationName);
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.rotation);
    }

    public static PlaceCreationMessage decode(FriendlyByteBuf buf) {
        return new PlaceCreationMessage(buf.readUtf(), buf.readBlockPos(), buf.readInt());
    }

    public static void handle(PlaceCreationMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Creation creation = CreationData.getCreationData().creations.get(msg.creationName);
            if(creation != null) {
                creation.placeCreation(ctx.get().getSender().level(), msg.pos, msg.rotation);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
