package net.jadenxgamer.elysium_api.api.registry.wood;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;

import net.jadenxgamer.elysium_api.api.registry.BlockTemplate;
import net.jadenxgamer.elysium_api.api.registry.ModelType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.model.generators.BlockStateProvider;

/**
 * WoodBlockType
 */
@Deprecated
public interface WoodBlockType {

    public static WoodBlockType LOG = Builder.suffixed("_log", RotatedPillarBlock::new).withSimpleModelGen(p -> p::logBlock).build();
    public static WoodBlockType WOOD = Builder.suffixed("_wood", RotatedPillarBlock::new).withSimpleModelGen(p -> p::logBlock).build();
    public static WoodBlockType STRIPPED_LOG = Builder.of(s -> "stripped_" + s + "_log", RotatedPillarBlock::new).withSimpleModelGen(p -> p::logBlock).build();
    public static WoodBlockType STRIPPED_WOOD = Builder.of(s -> "stripped_" + s + "_wood", RotatedPillarBlock::new).withSimpleModelGen(p -> p::logBlock).build();

    public static List<Pair<WoodBlockType, BlockTemplate>> LOGS = ImmutableList.of(
            Pair.of(LOG, BlockTemplate.of(Blocks.OAK_LOG)),
            Pair.of(WOOD, BlockTemplate.of(Blocks.OAK_WOOD)),
            Pair.of(STRIPPED_LOG, BlockTemplate.of(Blocks.STRIPPED_OAK_LOG)),
            Pair.of(STRIPPED_WOOD, BlockTemplate.of(Blocks.STRIPPED_OAK_WOOD)));

    public static WoodBlockType PLANKS = Builder.suffixed("_planks", Block::new).withSimpleModelGen(p -> p::simpleBlock).build();
    public static WoodBlockType SLAB = Builder.suffixed("_slab", SlabBlock::new).withSimpleModelGen((p, s, m) -> block -> {
        ResourceLocation planks = ResourceLocation.tryBuild(m, s.getName(PLANKS).get());
        p.slabBlock(block, planks, planks);
    }).withDependencies(PLANKS).build();

    // TODO: add "WoodBlockType"s
    String nameFor(String woodName);

    Block make(Properties properties, WoodType woodyType, WoodSet set);

    Consumer<Block> modelGenerator(BlockStateProvider provider, WoodSet set, String modid, ModelType type);

    List<WoodBlockType> dependencies();

    public static class Builder<T extends Block> {

        private Function<String, String> nameFor;
        private Function3<Properties, WoodType, WoodSet, T> make;
        private List<WoodBlockType> dependencies = new ArrayList<>();
        private Function3<BlockStateProvider, WoodSet, String, Consumer<T>> simpleModelGen = (a, b, c) -> block -> {};
        private Function3<BlockStateProvider, WoodSet, String, Consumer<T>> splitModelGen = (a, b, c) -> block -> {};

        private Builder(Function<String, String> nameFor, Function3<Properties, WoodType, WoodSet, T> make) {
            this.nameFor = nameFor;
            this.make = make;
        }

        public static <T extends Block> Builder<T> of(Function<String, String> nameFor, Function3<Properties, WoodType, WoodSet, T> make) {
            return new Builder<>(nameFor, make);
        }

        public static <T extends Block> Builder<T> of(Function<String, String> nameFor, Function<Properties, T> make) {
            return of(nameFor, (p, t, s) -> make.apply(p));
        }

        public static <T extends Block> Builder<T> suffixed(String suffix, Function3<Properties, WoodType, WoodSet, T> make) {
            return of(s -> s + suffix, make);
        }

        public static <T extends Block> Builder<T> suffixed(String suffix, Function<Properties, T> make) {
            return of(s -> s + suffix, make);
        }

        public Builder<T> withDependencies(WoodBlockType... types) {
            this.dependencies.addAll(Set.of(types));
            return this;
        }

        public Builder<T> withSimpleModelGen(Function3<BlockStateProvider, WoodSet, String, Consumer<T>> modelGenerator) {
            this.simpleModelGen = modelGenerator;
            return this;
        }

        public Builder<T> withSimpleModelGen(BiFunction<BlockStateProvider, WoodSet, Consumer<T>> modelGenerator) {
            this.simpleModelGen = (a, b, c) -> modelGenerator.apply(a, b);
            return this;
        }

        public Builder<T> withSimpleModelGen(Function<BlockStateProvider, Consumer<T>> modelGenerator) {
            this.simpleModelGen = (a, b, c) -> modelGenerator.apply(a);
            return this;
        }

        public Builder<T> withSplitModelGen(Function3<BlockStateProvider, WoodSet, String, Consumer<T>> modelGenerator) {
            this.splitModelGen = modelGenerator;
            return this;
        }

        public WoodBlockType build() {
            return new WoodBlockType() {

                @Override
                public String nameFor(String woodName) {
                    return nameFor.apply(woodName);
                }

                @Override
                public Block make(Properties properties, WoodType woodyType, WoodSet set) {
                    return make.apply(properties, woodyType, set);
                }

                @SuppressWarnings("unchecked")
                @Override
                public Consumer<Block> modelGenerator(BlockStateProvider provider, WoodSet set, String modid, ModelType type) {

                    if (splitModelGen == null) {
                        return b -> simpleModelGen.apply(provider, set, modid).accept((T) b);
                    }

                    if (type == ModelType.SIMPLE) {
                        return b -> simpleModelGen.apply(provider, set, modid).accept((T) b);
                    } else {
                        return b -> splitModelGen.apply(provider, set, modid).accept((T) b);
                    }
                }

                @Override
                public List<WoodBlockType> dependencies() {
                    return ImmutableList.copyOf(this.dependencies());
                }

            };
        }

    }

}
