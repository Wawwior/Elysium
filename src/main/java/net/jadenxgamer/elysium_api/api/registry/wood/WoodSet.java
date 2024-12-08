package net.jadenxgamer.elysium_api.api.registry.wood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.jadenxgamer.elysium_api.api.registry.BlockTemplate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * WoodSet
 */
public class WoodSet {

    private Map<WoodBlockType, RegistryObject<Block>> blocks;
    private Map<WoodBlockType, RegistryObject<Item>> items;

    public WoodSet(Map<WoodBlockType, RegistryObject<Block>> blocks, Map<WoodBlockType, RegistryObject<Item>> items) {
        this.blocks = blocks;
        this.items = items;
    }

    public List<RegistryObject<Block>> getBlockObjects() {
        return List.copyOf(blocks.values());
    }

    public Optional<RegistryObject<Block>> getBlock(WoodBlockType type) {
        return Optional.ofNullable(blocks.get(type));
    }

    public List<RegistryObject<Item>> getItemObjects() {
        return List.copyOf(items.values());
    }

    public Optional<RegistryObject<Item>> getItem(WoodBlockType type) {
        return Optional.ofNullable(items.get(type));
    }


    public static Builder overworldBuilder(String name, WoodType woodType) {
        return Builder.forWood(name, woodType)
               .addTypes(WoodBlockType.DEFAULT)
               .addTypes(WoodBlockType.LOGS)
               .noItem(
                   WoodBlockType.SIGN,
                   WoodBlockType.WALL_SIGN,
                   WoodBlockType.HANGING_SIGN,
                   WoodBlockType.WALL_HANGING_SIGN
               );
    }

    public static Builder netherBuilder(String name, WoodType woodType) {
        return Builder.forWood(name, woodType)
               .addTypes(WoodBlockType.DEFAULT)
               .addTypes(WoodBlockType.STEMS)
               .noItem(
                   WoodBlockType.SIGN,
                   WoodBlockType.WALL_SIGN,
                   WoodBlockType.HANGING_SIGN,
                   WoodBlockType.WALL_HANGING_SIGN
               );
    }


    public static class Builder {

        private String name;

        private final List<WoodBlockType> types = new ArrayList<>();
        private final Map<WoodBlockType, BlockTemplate> templates = new HashMap<>();
        private final Map<WoodBlockType, Function<String, String>> nameModifiers = new HashMap<>();
        private final Map<WoodBlockType, Consumer<BlockBehaviour.Properties>> propertiesModifiers = new HashMap<>();
        private final Set<WoodBlockType> noItem = new HashSet<>();

        private WoodType woodType;

        private Consumer<Properties> propertiesModifier = p -> {};

        public Builder(String name, WoodType type) {
            this.name = name;
            this.woodType = type;
        }

        public static Builder forWood(String name, WoodType type) {
            return new Builder(name, type);
        }

        public Builder addTypes(List<Pair<WoodBlockType, BlockTemplate>> types) {
            types.forEach(type -> {
                this.types.add(type.getFirst());
                this.templates.put(type.getFirst(), type.getSecond());
            });
            return this;
        }

        public Builder removeTypes(WoodBlockType... types) {
            for (WoodBlockType type : types) {
                this.types.remove(type);
            }

            return this;
        }

        public Builder withNameModifier(WoodBlockType type, Function<String, String> modifier) {
            nameModifiers.put(type, modifier);
            return this;
        }

        public Builder withPropertiesModifier(WoodBlockType type, Consumer<BlockBehaviour.Properties> modifier) {
            propertiesModifiers.put(type, modifier);
            return this;
        }

        public Builder withPropertiesModifier(Consumer<BlockBehaviour.Properties> modifier) {
            this.propertiesModifier = modifier;
            return this;
        }

        public Builder noItem(WoodBlockType... types) {
            for (WoodBlockType type : types) {
                noItem.add(type);
            }

            return this;
        }

        public WoodSet register(DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry) {

            if (this.types.contains(WoodBlockType.STAIRS) && !this.types.contains(WoodBlockType.PLANKS)) {
                throw new RuntimeException(new IllegalStateException("To register Stairs, Planks need to be registered too! Don't ask why..."));
            }

            Map<WoodBlockType, RegistryObject<Block>> blocks = new LinkedHashMap<>();
            Map<WoodBlockType, RegistryObject<Item>> items = new LinkedHashMap<>();
            WoodSet woodSet = new WoodSet(blocks, items);
            types.forEach(type -> {
                String id = nameModifiers.getOrDefault(type, s -> s).apply(type.nameFor(name));
                RegistryObject<Block> block = blockRegistry.register(id, () -> {
                    BlockBehaviour.Properties properties = templates.get(type).getProperties();
                    propertiesModifier.accept(properties);
                    propertiesModifiers.getOrDefault(type, t -> {}).accept(properties);
                    return type.make(properties, woodType, woodSet.getBlock(WoodBlockType.PLANKS));
                });

                if (!noItem.contains(type)) {
                    items.put(type, itemRegistry.register(id, () -> new BlockItem(block.get(), new Item.Properties())));
                }
                blocks.put(type, block);
            });

            MinecraftForge.EVENT_BUS.addListener((GatherDataEvent event) -> {
                DataGenerator generator = event.getGenerator();
                PackOutput output = generator.getPackOutput();
                ExistingFileHelper fileHelper = event.getExistingFileHelper();

                generator.addProvider(event.includeClient(), new BlockStateProvider(output, /* TODO */ "", fileHelper) {
                    @Override
                    protected void registerStatesAndModels() {
                        blocks.forEach((type, object) -> {
                            type.modelGenerator(this, name).accept(object.get());
                        });
                    }
                });
            });

            return woodSet;
        }

    }

}
