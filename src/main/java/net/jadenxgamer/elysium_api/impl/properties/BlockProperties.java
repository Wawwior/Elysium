package net.jadenxgamer.elysium_api.impl.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record BlockProperties(HolderSet<Block> blocks, Optional<Integer> lightEmission) {

    public static final Codec<BlockProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(BlockProperties::blocks),
            Codec.INT.optionalFieldOf("light_emission").forGetter(BlockProperties::lightEmission)
    ).apply(instance, BlockProperties::new));
}
