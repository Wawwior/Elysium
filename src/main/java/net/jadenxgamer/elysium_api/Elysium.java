package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.api.sound_transformer.SoundTypeDataLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
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

    private static SoundTypeDataLoader soundTypeDataLoader;
    public Elysium() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {
        soundTypeDataLoader = new SoundTypeDataLoader();
        event.addListener(soundTypeDataLoader);
    }

    public static SoundTypeDataLoader getSoundTypeDataLoader() {
        return soundTypeDataLoader;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
