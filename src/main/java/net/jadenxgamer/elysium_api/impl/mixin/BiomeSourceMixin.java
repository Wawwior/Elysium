package net.jadenxgamer.elysium_api.impl.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements ElysiumBiomeSource {

    @Shadow
    @Mutable
    public Supplier<Set<Holder<Biome>>> possibleBiomes;

    @Unique
    private boolean elysium$hasMergedPossibleBiomes = false;

    @Override
    public void addPossibleBiomes(Set<Holder<Biome>> biomes) {
        if(elysium$hasMergedPossibleBiomes) {
            return;
        }

        ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
        builder.addAll(this.possibleBiomes.get());
        builder.addAll(biomes);
        this.possibleBiomes = Suppliers.memoize(builder::build);
        this.elysium$hasMergedPossibleBiomes = true;
        Elysium.LOGGER.info("Successfully mapped new biomes to possibleBiomes");
    }
}
