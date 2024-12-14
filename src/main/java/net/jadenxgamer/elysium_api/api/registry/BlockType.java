package net.jadenxgamer.elysium_api.api.registry;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Optional;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * BlockType
 * @param T: BlockSetType like
 */
public interface BlockType<T> {

    String nameFor(String baseName);

    Block make(Properties properties, T type, BlockSet<T> set);

    Optional<Function<GeneratorData<T>, Consumer<Block>>> generatorFor(GeneratorType type);

    List<BlockType<T>> dependencies();

    BlockType<T> mapName(Function<String, String> map);

}
