package net.jadenxgamer.elysium_api.impl.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public interface ElysiumBiomeSource {

    void addPossibleBiomes(Set<Holder<Biome>> biome);
}
