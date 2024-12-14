package net.jadenxgamer.elysium_api.api.registry;

import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.datafixers.util.Function3;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * GeneratorData
 */
public record GeneratorData<T>(BlockStateProvider provider, String modid, BlockSet<T> set) {

    Function<GeneratorData<T>, Consumer<Block>> pack(Function3<BlockStateProvider, String, BlockSet<T>, Consumer<Block>> fn) {
        return gd -> fn.apply(gd.provider(), gd.modid(), gd.set());
    }

}

