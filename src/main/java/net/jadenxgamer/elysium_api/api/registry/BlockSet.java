package net.jadenxgamer.elysium_api.api.registry;

import java.util.Optional;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public interface BlockSet<T> {

    String name();

    Optional<String> getName(BlockType<T> type);

    Optional<RegistryObject<Block>> getBlock(BlockType<T> type);

    Optional<RegistryObject<Item>> getItem(BlockType<T> type);

}

