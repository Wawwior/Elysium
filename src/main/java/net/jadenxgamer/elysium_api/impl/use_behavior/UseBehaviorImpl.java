package net.jadenxgamer.elysium_api.impl.use_behavior;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class UseBehaviorImpl {

    public static void init(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (Elysium.registryAccess != null) {
            Optional<UseBehavior> registry = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BLOCK_USE_BEHAVIORS).stream().filter(s -> s.blocks().contains(state.getBlockHolder())).findFirst();
            if (registry.isEmpty()) {
                return;
            }

            if (stack.is(Items.STICK)) {
                if (registry.get().blocks().contains(state.getBlockHolder())) {
                    level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), Block.UPDATE_ALL);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}
