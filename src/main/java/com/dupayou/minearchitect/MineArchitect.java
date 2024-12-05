package com.dupayou.minearchitect;

import com.dupayou.minearchitect.data.CreationData;
import com.dupayou.minearchitect.model.Creation;
import com.dupayou.minearchitect.model.CreationBlock;
import com.dupayou.minearchitect.screens.CreationsScreen;
import com.dupayou.minearchitect.screens.MenuScreen;
import com.dupayou.minearchitect.utils.JsonBlockLoader;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MineArchitect.MOD_ID)
public class MineArchitect
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "mine_architect";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MineArchitect(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void clientSetup(final FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event)
    {
        if (event.getKey() == GLFW.GLFW_KEY_P && event.getAction() == GLFW.GLFW_PRESS)
        {
            LOGGER.info("P key pressed, placing blocks!");

//            CreationData data = CreationData.getCreationData();
//            Creation creation = data.getCreation("stonewall");
//
//            Minecraft mc = Minecraft.getInstance();
//
//            creation.placeCreation(mc.level, mc.player.blockPosition());
//            Map<BlockPos, ItemStack> structure = new HashMap<>();
//            structure.put(new BlockPos(0, 0, 0), new ItemStack(Blocks.DIRT));
//            structure.put(new BlockPos(0, 1, 0), new ItemStack(Blocks.STONE));
//            structure.put(new BlockPos(0, 2, 0), new ItemStack(Blocks.STONE));
//            structure.put(new BlockPos(0, 3, 0), new ItemStack(Blocks.STONE));
//            structure.put(new BlockPos(0, 4, 0), new ItemStack(Blocks.STONE));

            Minecraft.getInstance().setScreen(new MenuScreen());
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
