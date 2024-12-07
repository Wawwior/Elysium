package net.jadenxgamer.elysium_api;

import com.mojang.logging.LogUtils;
import net.jadenxgamer.elysium_api.api.biome.ElysiumBiomeRegistry;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehaviorImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

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
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private void onServerAboutToStart(ServerAboutToStartEvent event) {
        registryAccess = event.getServer().registryAccess();

        ElysiumBiomeRegistry.registerNetherBiome(Biomes.BADLANDS, registryAccess);

        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        for (ResourceKey<LevelStem> levelStemKey : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> optionalLevelStem = levelStems.getHolder(levelStemKey);
            if (optionalLevelStem.isPresent() && optionalLevelStem.get().value().generator().getBiomeSource() instanceof ElysiumBiomeSource biomeSource) {
                if (levelStemKey.equals(LevelStem.OVERWORLD)) {
                    // makes all overworld registered biomes locatable
                    biomeSource.addPossibleBiomes(ElysiumBiomeRegistry.overworldPossibleBiomes);
                }
                else if (levelStemKey.equals(LevelStem.NETHER)) {
                    // makes all nether registered biomes locatable
                    biomeSource.addPossibleBiomes(ElysiumBiomeRegistry.netherPossibleBiomes);
                }
                //TODO: End Biomes
            }
        }
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
