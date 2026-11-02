package com.infestationpotion;

import net.minecraftforge.common.ForgeConfigSpec;

public class InfestationConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue SPAWN_CHANCE;
    public static final ForgeConfigSpec.IntValue MAX_SILVERFISH;
    public static final ForgeConfigSpec.BooleanValue CAMPFIRE_WATER_PROTECTION;
    public static final ForgeConfigSpec.BooleanValue PROTECT_PLATES_AND_BUTTONS;
    public static final ForgeConfigSpec.BooleanValue ARMOR_STAND_PLAYER_LIKE_FIRE;
    public static final ForgeConfigSpec.BooleanValue ARROWS_KEEP_FLYING_IN_BUBBLES;
    public static final ForgeConfigSpec.BooleanValue EXACT_SPLASH_ALL_POTIONS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("infestation");
        SPAWN_CHANCE = builder
                .comment("Chance (0.0 - 1.0) that silverfish spawn each time an infested entity takes damage. Vanilla 1.21 = 0.10")
                .defineInRange("spawnChance", 0.10D, 0.0D, 1.0D);
        MAX_SILVERFISH = builder
                .comment("Maximum silverfish spawned per trigger (1 to this value). Vanilla 1.21 = 2")
                .defineInRange("maxSilverfish", 2, 1, 10);
        builder.pop();

        builder.push("water");
        CAMPFIRE_WATER_PROTECTION = builder
                .comment("If true, flowing water cannot enter, pass through or put out campfires (they act like a wall for water).")
                .define("campfireWaterProtection", true);
        PROTECT_PLATES_AND_BUTTONS = builder
                .comment("If true, flowing water cannot wash away pressure plates and buttons (you can still walk through/press them).")
                .define("protectPlatesAndButtons", true);
        builder.pop();

        builder.push("armorstand");
        ARMOR_STAND_PLAYER_LIKE_FIRE = builder
                .comment("If true, armor stands (20 health = 10 hearts) take fire, lava, campfire and burning damage exactly like a player: same damage amount, at most one hit every 0.5 seconds. Vanilla armor stands burn far faster.")
                .define("playerLikeFireDamage", true);
        builder.pop();

        builder.push("arrows");
        ARROWS_KEEP_FLYING_IN_BUBBLES = builder
                .comment("If true, an arrow that hits a mob or player while inside an upward bubble column (water over soul sand) is NOT removed: it keeps flying and floating.")
                .define("keepFlyingInBubbleColumns", true);
        builder.pop();

        builder.push("potions");
        EXACT_SPLASH_ALL_POTIONS = builder
                .comment("Splash potions always give the Infested effect its full duration (3:00) to everything inside the splash range. Set this to true to do the same for ALL potions (vanilla and modded), not only Infestation.")
                .define("exactSplashDurationForAllPotions", false);
        builder.pop();

        SPEC = builder.build();
    }
}
