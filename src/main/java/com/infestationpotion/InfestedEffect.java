package com.infestationpotion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * The "Infested" status effect. It has no per-tick behaviour; the silverfish
 * spawning is handled in {@link InfestationEvents} when the entity takes damage.
 */
public class InfestedEffect extends Effect {
    public InfestedEffect() {
        super(EffectType.HARMFUL, 0x8C9B8C);
    }
}
