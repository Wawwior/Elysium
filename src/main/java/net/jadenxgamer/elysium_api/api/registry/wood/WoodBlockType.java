package net.jadenxgamer.elysium_api.api.registry.wood;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.jadenxgamer.elysium_api.api.registry.BlockTemplate;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.registries.RegistryObject;

/**
 * WoodBlockType
 */
public interface WoodBlockType {

    public static WoodBlockType LOG = of(name -> name + "_log", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType WOOD = of(name -> name + "_wood", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType STRIPPED_LOG = of(name -> "stripped_" + name + "_log", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType STRIPPED_WOOD = of(name -> "stripped_" + name + "_wood", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));

    public static List<Pair<WoodBlockType, BlockTemplate>> LOGS = ImmutableList.of(
            Pair.of(LOG, BlockTemplate.of(Blocks.OAK_LOG)),
            Pair.of(WOOD, BlockTemplate.of(Blocks.OAK_WOOD)),
            Pair.of(STRIPPED_LOG, BlockTemplate.of(Blocks.STRIPPED_OAK_LOG)),
            Pair.of(STRIPPED_WOOD, BlockTemplate.of(Blocks.STRIPPED_OAK_WOOD)));

    public static WoodBlockType PLANKS = of(name -> name + "_planks", Block::new, (provider, name) -> block -> provider.simpleBlock(block));
    public static WoodBlockType SLAB = of(name -> name + "_slab", SlabBlock::new, (provider, name) -> block -> provider.slabBlock(block, provider.modLoc(name + "_double"), provider.modLoc(name)));

    public static WoodBlockType STAIRS = forStairs(name -> name + "_stairs", (properties, base) -> new StairBlock(base.orElseThrow().get()::defaultBlockState, properties));

    public static WoodBlockType FENCE = of(name -> name + "_fence", FenceBlock::new, (provider, name) -> block -> provider.fenceBlock(block, provider.modLoc(name)));
    public static WoodBlockType FENCE_GATE = withWoodType(name -> name + "_fence_gate", FenceGateBlock::new, (provider, name) -> block -> provider.fenceGateBlock(block, provider.modLoc(name)));
    public static WoodBlockType DOOR = withSetType(name -> name + "_door", DoorBlock::new, (provider, name) -> block -> provider.doorBlock(block, provider.modLoc(name), provider.modLoc(name + "_top")));
    public static WoodBlockType TRAPDOOR = withSetType(name -> name + "_trapdoor", TrapDoorBlock::new, (provider, name) -> block -> provider.trapdoorBlock(block, provider.modLoc(name), true));
    public static WoodBlockType BUTTON = withSetType(name -> name + "_button", (properties, setType) -> new ButtonBlock(properties, setType, 30, true), (provider, name) -> block -> provider.buttonBlock(block, provider.modLoc(name)));
    public static WoodBlockType PRESSURE_PLATE = withSetType(name -> name + "_pressure_plate", (properties, setType) -> new PressurePlateBlock(Sensitivity.EVERYTHING, properties, setType), (provider, name) -> block -> provider.pressurePlateBlock(block, provider.modLoc(name)));
    public static WoodBlockType SIGN = withWoodType(name -> name + "_sign", StandingSignBlock::new);
    public static WoodBlockType WALL_SIGN = withWoodType(name -> name + "_wall_sign", WallSignBlock::new);
    public static WoodBlockType HANGING_SIGN = withWoodType(name -> name + "_hanging_sign", CeilingHangingSignBlock::new);
    public static WoodBlockType WALL_HANGING_SIGN = withWoodType(name -> name + "_wall_hanging_sign",  WallHangingSignBlock::new);

    public static List<Pair<WoodBlockType, BlockTemplate>> DEFAULT = ImmutableList.of(
            Pair.of(PLANKS, BlockTemplate.of(Blocks.OAK_PLANKS)),
            Pair.of(SLAB, BlockTemplate.of(Blocks.OAK_SLAB)),
            Pair.of(STAIRS, BlockTemplate.of(Blocks.OAK_STAIRS)),
            Pair.of(FENCE, BlockTemplate.of(Blocks.OAK_FENCE)),
            Pair.of(FENCE_GATE, BlockTemplate.of(Blocks.OAK_FENCE_GATE)),
            Pair.of(DOOR, BlockTemplate.of(Blocks.OAK_DOOR)),
            Pair.of(TRAPDOOR, BlockTemplate.of(Blocks.OAK_TRAPDOOR)),
            Pair.of(BUTTON, BlockTemplate.of(Blocks.OAK_BUTTON)),
            Pair.of(PRESSURE_PLATE, BlockTemplate.of(Blocks.OAK_PRESSURE_PLATE)),
            Pair.of(SIGN, BlockTemplate.of(Blocks.OAK_SIGN)),
            Pair.of(WALL_SIGN, BlockTemplate.of(Blocks.OAK_WALL_SIGN)),
            Pair.of(HANGING_SIGN, BlockTemplate.of(Blocks.OAK_HANGING_SIGN)),
            Pair.of(WALL_HANGING_SIGN, BlockTemplate.of(Blocks.OAK_WALL_HANGING_SIGN))
        );

    public static WoodBlockType STEM = of(name -> name + "_stem", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType HYPHAE = of(name -> name + "_hyphae", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType STRIPPED_STEM = of(name -> "stripped_" + name + "_stem", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType STRIPPED_HYPHAE = of(name -> "stripped_" + name + "_hyphae", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));

    public static List<Pair<WoodBlockType, BlockTemplate>> STEMS = ImmutableList.of(
            Pair.of(STEM, BlockTemplate.of(Blocks.CRIMSON_STEM)),
            Pair.of(HYPHAE, BlockTemplate.of(Blocks.CRIMSON_HYPHAE)),
            Pair.of(STRIPPED_STEM, BlockTemplate.of(Blocks.STRIPPED_CRIMSON_STEM)),
            Pair.of(STRIPPED_HYPHAE, BlockTemplate.of(Blocks.STRIPPED_CRIMSON_HYPHAE)));

    public static WoodBlockType BAMBOO_LIKE = of(name -> name, BambooStalkBlock::new);
    public static WoodBlockType BAMBOO_BLOCK_LIKE = of(name -> name + "_block", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));
    public static WoodBlockType STRIPPED_BAMBOO_BLOCK_LIKE = of(name -> "stripped_" + name + "_block", RotatedPillarBlock::new, (provider, name) -> block -> provider.logBlock(block));

    public static List<Pair<WoodBlockType, BlockTemplate>> STALKS = ImmutableList.of(
            Pair.of(BAMBOO_LIKE, BlockTemplate.of(Blocks.BAMBOO)),
            Pair.of(BAMBOO_BLOCK_LIKE, BlockTemplate.of(Blocks.BAMBOO_BLOCK)),
            Pair.of(STRIPPED_BAMBOO_BLOCK_LIKE, BlockTemplate.of(Blocks.STRIPPED_BAMBOO_BLOCK))
        );


    public static <T extends Block> WoodBlockType withWoodType(Function<String, String> nameFor, BiFunction<BlockBehaviour.Properties, WoodType, T> constructor, BiFunction<BlockStateProvider, String, Consumer<T>> modelGenerator) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodType, Optional<RegistryObject<Block>> stairBase) {
                return constructor.apply(properties, woodType);
            }


            @SuppressWarnings("unchecked")
            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> modelGenerator.apply(provider, nameFor(woodName)).accept((T) block);
            }
        };
    }

    public static <T extends Block> WoodBlockType withWoodType(Function<String, String> nameFor, BiFunction<BlockBehaviour.Properties, WoodType, T> constructor) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodType, Optional<RegistryObject<Block>> stairBase) {
                return constructor.apply(properties, woodType);
            }

            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> {};
            }
        };
    }

    public static <T extends Block> WoodBlockType withSetType(Function<String, String> nameFor, BiFunction<BlockBehaviour.Properties, BlockSetType, T> constructor, BiFunction<BlockStateProvider, String, Consumer<T>> modelGenerator) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodType, Optional<RegistryObject<Block>> stairBase) {
                return constructor.apply(properties, woodType.setType());
            }

            @SuppressWarnings("unchecked")
            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> modelGenerator.apply(provider, nameFor(woodName)).accept((T) block);
            }
        };
    }

    public static <T extends Block> WoodBlockType forStairs(Function<String, String> nameFor, BiFunction<Properties, Optional<RegistryObject<Block>>, T> constructor) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodType, Optional<RegistryObject<Block>> stairBase) {
                return constructor.apply(properties, stairBase);
            }

            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> provider.stairsBlock((StairBlock) block, provider.modLoc(nameFor(woodName)));
            }

        };

    }

    public static <T extends Block> WoodBlockType of(Function<String, String> nameFor, Function<BlockBehaviour.Properties, T> constructor, BiFunction<BlockStateProvider, String, Consumer<T>> modelGenerator) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodType, Optional<RegistryObject<Block>> stairBase) {
                return constructor.apply(properties);
            }

            @SuppressWarnings("unchecked")
            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> modelGenerator.apply(provider, nameFor(woodName)).accept((T) block);
            }

        };
    }

    public static <T extends Block> WoodBlockType of(Function<String, String> nameFor, Function<BlockBehaviour.Properties, T> constructor) {
        return new WoodBlockType() {

            @Override
            public String nameFor(String woodName) {
                return nameFor.apply(woodName);
            }

            @Override
            public Block make(Properties properties, WoodType woodyType,
                              Optional<RegistryObject<Block>> planks) {
                return constructor.apply(properties);
            }

            @Override
            public Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName) {
                return block -> {};
            }

        };
    }

    String nameFor(String woodName);

    Block make(Properties properties, WoodType woodyType, Optional<RegistryObject<Block>> planks);

    Consumer<Block> modelGenerator(BlockStateProvider provider, String woodName);

}
