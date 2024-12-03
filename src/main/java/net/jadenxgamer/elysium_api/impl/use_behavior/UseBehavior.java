package net.jadenxgamer.elysium_api.impl.use_behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public record UseBehavior(HolderSet<Block> blocks) {
    public static final Codec<UseBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(UseBehavior::blocks)
    ).apply(instance, UseBehavior::new));
}
