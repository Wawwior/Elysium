package net.jadenxgamer.elysium_api.api.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Contract;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * BlockSetBuilder
 */
public interface BlockSetFactory<T> {

    @Contract("_ -> this")
    public BlockSetFactory<T> with(List<Pair<BlockType<T>, BlockTemplate>> pairs);

    @Contract("_ -> this")
    public BlockSetFactory<T> without(List<BlockType<T>> types);

    @Contract("_ -> this")
    public BlockSetFactory<T> mapAll(Function<BlockTemplate, BlockTemplate> fn);

    @Contract("!null, !null, !null -> new")
    public BlockSet<T> buildTo(String modid, DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry);

    public static <T> BlockSetFactory<T> of(String s, T t) {
        return new BlockSetFactory<T>() {

            private final String name = s;
            private final T type = t;

            private final List<BlockType<T>> types = new ArrayList<>();
            private final Map<BlockType<T>, BlockTemplate> templates = new HashMap<>();
            private Function<BlockTemplate, BlockTemplate> mapper = t -> t;

            @Override
            public BlockSetFactory<T> with(List<Pair<BlockType<T>, BlockTemplate>> pairs) {
                pairs.forEach(pair -> {
                    BlockType<T> blockType = pair.getFirst();
                    BlockTemplate template = pair.getSecond();

                    nonOverwrite(blockType.dependencies().stream().map(t -> Pair.of(t, template)).collect(Collectors.toList()));

                    if (!types.contains(blockType)) {
                        types.add(blockType);
                    }
                    templates.put(blockType, template);
                });
                return this;
            }

            private void nonOverwrite(List<Pair<BlockType<T>, BlockTemplate>> pairs) {
                pairs.forEach(pair -> {
                    BlockType<T> blockType = pair.getFirst();
                    BlockTemplate template = pair.getSecond();

                    nonOverwrite(blockType.dependencies().stream().map(t -> Pair.of(t, template)).collect(Collectors.toList()));

                    if (!types.contains(blockType)) {
                        types.add(blockType);
                        templates.put(blockType, template);
                    }
                });
            }

            @Override
            public BlockSetFactory<T> without(List<BlockType<T>> types) {
                types.removeAll(types);
                return this;
            }

            @Override
            public BlockSetFactory<T> mapAll(Function<BlockTemplate, BlockTemplate> fn) {
                mapper = mapper.andThen(fn);
                return this;
            }

            @Override
            public BlockSet<T> buildTo(String modid, DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry) {
                Map<BlockType<T>, RegistryObject<Block>> blocks = new LinkedHashMap<>();
                Map<BlockType<T>, RegistryObject<Item>> items = new LinkedHashMap<>();

                BlockSet<T> set = new BlockSet<T>() {

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public Optional<String> getName(BlockType<T> type) {
                        return getBlock(type).map(b -> type.nameFor(name()));
                    }

                    @Override
                    public Optional<RegistryObject<Block>> getBlock(BlockType<T> type) {
                        return Optional.ofNullable(blocks.get(type));
                    }

                    @Override
                    public Optional<RegistryObject<Item>> getItem(BlockType<T> type) {
                        return Optional.ofNullable(items.get(type));
                    }

                };


                return set;
            }

        };
    }

}
