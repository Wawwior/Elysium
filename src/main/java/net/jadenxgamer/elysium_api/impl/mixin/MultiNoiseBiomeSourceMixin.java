package net.jadenxgamer.elysium_api.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
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

@Mixin(value = MultiNoiseBiomeSource.class, priority = 800)
public abstract class MultiNoiseBiomeSourceMixin {
    @Shadow protected abstract Climate.ParameterList<Holder<Biome>> parameters();

    @Shadow public abstract Holder<Biome> getNoiseBiome(Climate.TargetPoint pTargetPoint);

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void netherexp$getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
//        if (Math.random() < 0.5) {
//
//        }
        Holder<Biome> currentBiome = this.getNoiseBiome(sampler.sample(x, y, z));
        if (currentBiome.is(Biomes.SOUL_SAND_VALLEY)) {
            cir.setReturnValue(Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.BADLANDS));
        }
    }

//    @Unique
//    private static Holder<Biome> replaceBiome(Holder<Biome> original, int x, int y, int z, Climate.Sampler sampler) {
//        if (original.is(Biomes.SOUL_SAND_VALLEY)) {
//            return cir.setReturnValue(Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.BADLANDS));;
//        }
//        return original;
//    }
}
