package net.jadenxgamer.elysium_api.api.sound_transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.SoundType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SoundTypeDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<ResourceLocation, SoundTypeData> soundTypeMap = new HashMap<>();
    private final Map<ResourceLocation, SoundType> blockSoundMap = new HashMap<>();

    public SoundTypeDataLoader() {
        super(GSON, "elysium/sound_transformers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        soundTypeMap.clear();
        blockSoundMap.clear();
        resourceLocationJsonElementMap.forEach((id, jsonElement) -> {
            try {
                SoundTypeData data = SoundTypeData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result().orElseThrow(() -> new JsonParseException("Invalid Sound-Transformer for " + id));
                soundTypeMap.put(id, data);
                for (ResourceLocation blockId : data.getBlocks()) {
                    blockSoundMap.put(blockId, data.toSoundType());
                }
            } catch (Exception e) {
                Elysium.LOGGER.error("Failed to parse Sound-Transformer {}", id, e);
            }
        });
    }

    public Optional<SoundType> getSoundType(ResourceLocation blockId) {
        return Optional.ofNullable(blockSoundMap.get(blockId));
    }
}
