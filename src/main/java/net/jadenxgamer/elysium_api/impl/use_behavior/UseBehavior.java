package net.jadenxgamer.elysium_api.impl.use_behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public record UseBehavior(HolderSet<Block> blocks, HolderSet<Item> itemCondition, int chanceToFail, Behavior behavior) {
    public static final Codec<UseBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(UseBehavior::blocks),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("item_condition").forGetter(UseBehavior::itemCondition),
            Codec.INT.optionalFieldOf("chance_to_fail", 0).forGetter(UseBehavior::chanceToFail),
            Behavior.CODEC.fieldOf("behaviors").forGetter(UseBehavior::behavior)
    ).apply(instance, UseBehavior::new));

    public record Behavior(UseBehaviorTypeEnum type, ResourceLocation place, PosEnum pos, int posOffset,
                           AfterUseItemEnum afterUseItem, boolean canReplace,
                           Optional<Sounds> sounds, Optional<Particles> particles) {
        public static final Codec<Behavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UseBehaviorTypeEnum.CODEC.optionalFieldOf("type", UseBehaviorTypeEnum.PLACE).forGetter(Behavior::type),
                ResourceLocation.CODEC.optionalFieldOf("place", new ResourceLocation("minecraft", "stone")).forGetter(Behavior::place),
                PosEnum.CODEC.optionalFieldOf("pos", PosEnum.NOOP).forGetter(Behavior::pos),
                Codec.INT.optionalFieldOf("pos_offset", 0).forGetter(Behavior::posOffset),
                AfterUseItemEnum.CODEC.optionalFieldOf("after_use_item", AfterUseItemEnum.NOTHING).forGetter(Behavior::afterUseItem),
                Codec.BOOL.optionalFieldOf("can_replace_solids", false).forGetter(Behavior::canReplace),
                Sounds.CODEC.optionalFieldOf("sounds").forGetter(Behavior::sounds),
                Particles.CODEC.optionalFieldOf("particles").forGetter(Behavior::particles)
        ).apply(instance, Behavior::new));
    }

    public record Sounds(SoundEvent soundEvent, float volume, float pitch) {

        public static final Codec<Sounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("sound_event").forGetter(Sounds::soundEvent),
                Codec.FLOAT.optionalFieldOf("volume", 1.0f).forGetter(Sounds::volume),
                Codec.FLOAT.optionalFieldOf("pitch", 1.0f).forGetter(Sounds::pitch)
        ).apply(instance, Sounds::new));
    }

    public record Particles(ResourceLocation particleType, double xv, double yv, double zv) {
        public static final Codec<Particles> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("particle_type").forGetter(Particles::particleType),
                Codec.DOUBLE.optionalFieldOf("x_velocity", 0.0).forGetter(Particles::xv),
                Codec.DOUBLE.optionalFieldOf("y_velocity", 0.0).forGetter(Particles::yv),
                Codec.DOUBLE.optionalFieldOf("z_velocity", 0.0).forGetter(Particles::zv)
        ).apply(instance, Particles::new));
    }
}
