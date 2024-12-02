package net.jadenxgamer.elysium_api.impl.mixin;

import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour {

    public BlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "getSoundType",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$soundTransformer(BlockState state, CallbackInfoReturnable<SoundType> cir) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId != null) {
            Elysium.getSoundTypeDataLoader().getSoundType(blockId).ifPresent(cir::setReturnValue);
        }
    }
}
