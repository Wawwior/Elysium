package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehaviorImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Elysium.MOD_ID)
public class Elysium {
    public static final String MOD_ID = "elysium_api";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RegistryAccess registryAccess;

    public Elysium() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ElysiumRegistries::datapackRegistry);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void onServerStart(ServerStartedEvent event) {
        registryAccess = event.getServer().registryAccess();
    }

    private void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        UseBehaviorImpl.init(event);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
