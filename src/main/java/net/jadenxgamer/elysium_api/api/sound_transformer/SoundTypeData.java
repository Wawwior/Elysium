package net.jadenxgamer.elysium_api.api.sound_transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SoundTypeData {
    public static final Codec<SoundTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("blocks").forGetter(SoundTypeData::getBlocks),
            ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("break_sound").forGetter(SoundTypeData::getBreakSound),
            ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("step_sound").forGetter(SoundTypeData::getStepSound),
            ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("place_sound").forGetter(SoundTypeData::getPlaceSound),
            ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("hit_sound").forGetter(SoundTypeData::getHitSound),
            ForgeRegistries.SOUND_EVENTS.getCodec().fieldOf("fall_sound").forGetter(SoundTypeData::getFallSound),
            Codec.FLOAT.fieldOf("volume").orElse(1.0f).forGetter(SoundTypeData::getVolume),
            Codec.FLOAT.fieldOf("pitch").orElse(1.0f).forGetter(SoundTypeData::getPitch)
    ).apply(instance, SoundTypeData::new));

    private final List<ResourceLocation> blocks;
    private final SoundEvent breakSound;
    private final SoundEvent stepSound;
    private final SoundEvent placeSound;
    private final SoundEvent hitSound;
    private final SoundEvent fallSound;
    private final float volume;
    private final float pitch;

    public SoundTypeData(List<ResourceLocation> blocks, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound, float volume, float pitch) {
        this.blocks = blocks;
        this.breakSound = breakSound;
        this.stepSound = stepSound;
        this.placeSound = placeSound;
        this.hitSound = hitSound;
        this.fallSound = fallSound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public List<ResourceLocation> getBlocks() {
        return blocks;
    }

    public SoundEvent getBreakSound() {
        return breakSound;
    }

    public SoundEvent getStepSound() {
        return stepSound;
    }

    public SoundEvent getPlaceSound() {
        return placeSound;
    }

    public SoundEvent getHitSound() {
        return hitSound;
    }

    public SoundEvent getFallSound() {
        return fallSound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public SoundType toSoundType() {
        return new SoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
}