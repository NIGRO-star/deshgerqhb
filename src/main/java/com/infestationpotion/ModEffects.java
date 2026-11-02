package com.infestationpotion;

import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {
    // In 1.16.5, ForgeRegistries.POTIONS holds the status effects.
    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, InfestationPotionMod.MODID);

    public static final RegistryObject<Effect> INFESTED =
            EFFECTS.register("infested", InfestedEffect::new);
}
