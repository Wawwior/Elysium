package net.jadenxgamer.elysium_api.impl.use_behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;

public enum AfterUseItemEnum {
    NOTHING,
    CONSUME,
    DAMAGE;

    public static final Codec<AfterUseItemEnum> CODEC = Codec.STRING.comapFlatMap(
            s -> Arrays.stream(AfterUseItemEnum.values()).filter(pos -> pos.name().equalsIgnoreCase(s)).findFirst().map(DataResult::success).orElse(DataResult.error(() -> "Invalid position: " + s)),
            AfterUseItemEnum::name
    );
}
