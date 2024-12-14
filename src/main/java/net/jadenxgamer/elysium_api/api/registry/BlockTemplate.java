package net.jadenxgamer.elysium_api.api.registry;

import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public interface BlockTemplate {

    public static BlockTemplate of(Block block) {
        return () -> BlockBehaviour.Properties.copy(block);
    }

    public static BlockTemplate of(BlockBehaviour.Properties properties) {
        return () -> properties;
    }

    BlockTemplate EMPTY = () -> BlockBehaviour.Properties.of();

    BlockBehaviour.Properties getProperties();

    default BlockTemplate map(Function<Properties, Properties> mapping) {
        return () -> mapping.apply(getProperties());
    }

}

