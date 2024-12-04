package net.jadenxgamer.elysium_api.impl.use_behavior;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.ElysiumRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class UseBehaviorImpl {

    public static void init(PlayerInteractEvent.RightClickBlock event) {
        if (Elysium.registryAccess == null) {
            return;
        }
        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());

        Optional<UseBehavior> useBehavior = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BLOCK_USE_BEHAVIORS).stream()
                .filter(s -> s.blocks().contains(state.getBlockHolder()) && s.itemCondition().contains(stack.getItemHolder())).findFirst();

        if (level.isClientSide()) {
            return;
        }

        if (useBehavior.isEmpty()) {
            // returns if no registry was found
            return;
        }

        UseBehavior registry = useBehavior.get();
        BlockPos pos = getPosFromCodec(registry.behavior().pos(), registry.behavior().posOffset(), event);
        if (!registry.behavior().canReplace() && !level.getBlockState(pos).canBeReplaced()) {
            // returns if the current block in pos cannot be replaced
            return;
        }

        if (!player.getAbilities().instabuild) {
            handleItemAfterUse(registry.behavior().afterUseItem(), stack, event);
        }

        int chanceToFail = registry.chanceToFail();
        if (chanceToFail > 0) {
            if (level.random.nextInt(chanceToFail) != 0) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        switch (registry.behavior().type()) {
            case PLACE -> placeBlock(level, pos, registry.behavior().place(), event);
            case PLACE_ITSELF -> placeBlock(level, pos, ForgeRegistries.BLOCKS.getKey(state.getBlock()), event);
            case DROP -> dropStack(level, pos, event.getFace(), state.getBlock(), registry.behavior().place());
            case DROP_ITSELF -> dropStack(level, pos, event.getFace(), state.getBlock(), ForgeRegistries.BLOCKS.getKey(state.getBlock()));
            case FEATURE -> placeFeature(level, pos, registry.behavior().place());
        }

        if (registry.behavior().sounds().isPresent()) {
            level.playSound(null, event.getPos(), registry.behavior().sounds().get().soundEvent(), SoundSource.BLOCKS, registry.behavior().sounds().get().volume(), registry.behavior().sounds().get().pitch());
        }
        if (registry.behavior().particles().isPresent()) {
            spawnParticles(level, pos, registry.behavior().particles().get().particleType(), registry.behavior().particles().get().xv(), registry.behavior().particles().get().yv(), registry.behavior().particles().get().zv());
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void placeBlock(Level level, BlockPos pos, ResourceLocation location, PlayerInteractEvent.RightClickBlock event) {
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if (block != null && block.canSurvive(level.getBlockState(pos), level, pos)) {
            level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static void dropStack(Level level, BlockPos pos, Direction direction, Block block, ResourceLocation location) {
        Item item = ForgeRegistries.ITEMS.getValue(location);
        if (item != null) {
            block.popResourceFromFace(level, pos, direction, new ItemStack(item));
        }
    }

    private static void placeFeature(Level level, BlockPos pos, ResourceLocation location) {
        if (level instanceof ServerLevel serverLevel) {
            ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(Registries.CONFIGURED_FEATURE, location);
            serverLevel.registryAccess()
                    .registry(Registries.CONFIGURED_FEATURE).flatMap(registry -> registry.getHolder(featureKey)).ifPresent(holder -> holder.value()
                            .place(serverLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.random, pos));
        }
    }

    private static void handleItemAfterUse(AfterUseItemEnum afterUse, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        switch (afterUse) {
            case CONSUME -> stack.shrink(1);
            case DAMAGE -> stack.hurtAndBreak(1, event.getEntity(), p -> p.broadcastBreakEvent(event.getHand()));
        }
    }

    private static BlockPos getPosFromCodec(PosEnum pos, int offset, PlayerInteractEvent.RightClickBlock event) {
        BlockPos basePos = event.getPos();
        return switch (pos) {
            case ABOVE -> basePos.above(offset);
            case BELOW -> basePos.below(offset);
            case NORTH -> basePos.north(offset);
            case SOUTH -> basePos.south(offset);
            case EAST -> basePos.east(offset);
            case WEST -> basePos.west(offset);
            case RANDOM_HORIZONTAL -> {
                Direction randomDirection = Direction.Plane.HORIZONTAL.getRandomDirection(event.getLevel().random);
                yield basePos.relative(randomDirection);
            }
            default -> basePos;
        };
    }

    private static void spawnParticles(Level level, BlockPos pos, ResourceLocation location, double xVelocity, double yVelcoity, double zVelocity) {
        ParticleType<?> particleType = ForgeRegistries.PARTICLE_TYPES.getValue(location);
        if (particleType instanceof SimpleParticleType simple) {
            RandomSource random = level.random;
            for (Direction direction : Direction.values()) {
                BlockPos blockPos = pos.relative(direction);
                if (!level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos) && random.nextInt(120) == 0) {
                    Direction.Axis axis = direction.getAxis();
                    double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double) direction.getStepX() : (double) random.nextFloat();
                    double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double) direction.getStepY() : (double) random.nextFloat();
                    double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double) direction.getStepZ() : (double) random.nextFloat();
                    level.addParticle(simple, (double) pos.getX() + e, (double) pos.getY() + f, (double) pos.getZ() + g, xVelocity, yVelcoity, zVelocity);
                }
            }
        }
    }
}
