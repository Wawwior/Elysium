package net.jadenxgamer.elysium_api.impl.use_behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;

public enum UseBehaviorTypeEnum {
    PLACE,
    PLACE_ITSELF,
    DROP,
    DROP_ITSELF,
    FEATURE,
    INSERT_STACK;

    public static final Codec<UseBehaviorTypeEnum> CODEC = Codec.STRING.comapFlatMap(
            s -> Arrays.stream(UseBehaviorTypeEnum.values()).filter(pos -> pos.name().equalsIgnoreCase(s)).findFirst().map(DataResult::success).orElse(DataResult.error(() -> "Invalid position: " + s)),
            UseBehaviorTypeEnum::name
    );
}
