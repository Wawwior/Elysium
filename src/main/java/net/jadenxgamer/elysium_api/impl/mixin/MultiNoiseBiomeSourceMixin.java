package net.jadenxgamer.elysium_api.impl.mixin;

import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 800)
public abstract class MultiNoiseBiomeSourceMixin {
    @Shadow
    protected abstract Climate.ParameterList<Holder<Biome>> parameters();


    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        int eX = x / 16;
        int eZ = z / 16;

        long seed = makeCoordinatesIntoSeed(eX, eZ);
        Random random = new Random(seed);

        if (random.nextDouble() < 0.5) {
            Holder<Biome> currentBiome = this.parameters().findValueBruteForce(sampler.sample(x, y, z));
            if (currentBiome.is(Biomes.SOUL_SAND_VALLEY)) {
                cir.setReturnValue(Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.BADLANDS));
            }
        }
    }

//    @Unique
//    private static Holder<Biome> replaceBiome(Holder<Biome> original, int x, int y, int z, Climate.Sampler sampler) {
//        if (original.is(Biomes.SOUL_SAND_VALLEY)) {
//            return cir.setReturnValue(Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.BADLANDS));;
//        }
//        return original;
//    }

    @Unique
    private long makeCoordinatesIntoSeed(int regionX, int regionZ) {
        // Generates a seed depending on the region quad coordinates for consistent randomness
        long x = 31L * regionX + 17;
        long z = 37L * regionZ + 23;
        return (x ^ z) * 0x5DEECE66DL;
    }
}
