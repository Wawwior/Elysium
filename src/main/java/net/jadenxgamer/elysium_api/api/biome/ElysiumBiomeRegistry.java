package net.jadenxgamer.elysium_api.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Set;

public class ElysiumBiomeRegistry {
    public static Set<Holder<Biome>> overworldPossibleBiomes = new HashSet<>();
    public static Set<Holder<Biome>> netherPossibleBiomes = new HashSet<>();

    public static void registerOverworldBiome(ResourceKey<Biome> biome, RegistryAccess registryAccess) {
        overworldPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(biome));
    }

    public static void registerNetherBiome(ResourceKey<Biome> biome, RegistryAccess registryAccess) {
        netherPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(biome));
    }
}
