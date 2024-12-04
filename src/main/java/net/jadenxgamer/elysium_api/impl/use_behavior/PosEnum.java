package net.jadenxgamer.elysium_api.impl.use_behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;

public enum PosEnum {
    NOOP,
    ABOVE,
    BELOW,
    NORTH,
    EAST,
    SOUTH,
    WEST,
    RANDOM_HORIZONTAL;

    public static final Codec<PosEnum> CODEC = Codec.STRING.comapFlatMap(
            s -> Arrays.stream(PosEnum.values()).filter(pos -> pos.name().equalsIgnoreCase(s)).findFirst().map(DataResult::success).orElse(DataResult.error(() -> "Invalid position: " + s)),
            PosEnum::name
    );
}
