package com.infestationpotion;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SHOW_EFFECT_ICONS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("visual");
        SHOW_EFFECT_ICONS = builder
                .comment("Show a small icon of the potion's effect in the middle of the bottle (inventory and hotbar).")
                .define("showEffectIcons", true);
        builder.pop();
        SPEC = builder.build();
    }
}
