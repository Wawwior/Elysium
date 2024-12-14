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
import net.jadenxgamer.elysium_api.api.registry.GeneratorType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.client.model.generators.BlockStateProvider;
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
    private String woodName;

    private WoodSet(String woodName, Map<WoodBlockType, RegistryObject<Block>> blocks, Map<WoodBlockType, RegistryObject<Item>> items) {
        this.woodName = woodName;
        this.blocks = blocks;
        this.items = items;
    }

    public String woodName() {
        return woodName;
    }

    public Optional<String> getName(WoodBlockType type) {
        return Optional.ofNullable(blocks.get(type)).map(r -> type.nameFor(woodName));
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


    public static class Builder {

        private String name;

        private final List<WoodBlockType> types = new ArrayList<>();
        private final Map<WoodBlockType, BlockTemplate> templates = new HashMap<>();
        private final Map<WoodBlockType, GeneratorType> generatorTypes = new HashMap<>();
        private final Map<WoodBlockType, Function<String, String>> nameModifiers = new HashMap<>();
        private final Map<WoodBlockType, Consumer<BlockBehaviour.Properties>> propertiesModifiers = new HashMap<>();
        private final Set<WoodBlockType> noItem = new HashSet<>();

        private WoodType woodType;

        private Consumer<Properties> propertiesModifier = p -> {};

        private Builder(String name, WoodType type) {
            this.name = name;
            this.woodType = type;
        }

        public static Builder forWood(String name, WoodType type) {
            return new Builder(name, type);
        }

        public Builder addTypes(List<Pair<WoodBlockType, BlockTemplate>> types) {
            types.forEach(type -> {
                WoodBlockType woodBlockType = type.getFirst();
                woodBlockType.dependencies().forEach(dependency -> {
                    if (!this.types.contains(dependency)) {
                        this.types.add(dependency);
                        this.templates.put(woodBlockType, BlockTemplate.EMPTY);
                    }
                });

                if (!this.types.contains(woodBlockType)) {
                    this.types.add(woodBlockType);
                }
                this.templates.put(woodBlockType, type.getSecond());
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

        public WoodSet register(String modid, DeferredRegister<Block> blockRegistry, DeferredRegister<Item> itemRegistry) {

            Map<WoodBlockType, RegistryObject<Block>> blocks = new LinkedHashMap<>();
            Map<WoodBlockType, RegistryObject<Item>> items = new LinkedHashMap<>();
            WoodSet woodSet = new WoodSet(name, blocks, items);
            types.forEach(type -> {
                String id = nameModifiers.getOrDefault(type, s -> s).apply(type.nameFor(name));
                RegistryObject<Block> block = blockRegistry.register(id, () -> {
                    BlockBehaviour.Properties properties = templates.get(type).getProperties();
                    propertiesModifier.accept(properties);
                    propertiesModifiers.getOrDefault(type, t -> {}).accept(properties);
                    return type.make(properties, woodType, woodSet);
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

                generator.addProvider(event.includeClient(), new BlockStateProvider(output, modid, fileHelper) {
                    @Override
                    protected void registerStatesAndModels() {
                        blocks.forEach((type, object) -> {

                            GeneratorType modelType = generatorTypes.getOrDefault(type, GeneratorType.SIMPLE);

                            if (modelType != GeneratorType.NONE) {
                                type.modelGenerator(this, woodSet, modid, modelType).accept(object.get());
                            }
                        });
                    }
                });
            });

            return woodSet;
        }

    }

}
