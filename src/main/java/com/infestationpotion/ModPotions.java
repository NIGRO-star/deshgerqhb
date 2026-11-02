package com.infestationpotion;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPotions {
    // In 1.16.5, ForgeRegistries.POTION_TYPES holds the potion types.
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTION_TYPES, InfestationPotionMod.MODID);

    // 3 minutes = 3600 ticks. The explicit name gives the translation key
    // item.minecraft.potion.effect.infestation (and splash/lingering/arrow variants).
    public static final RegistryObject<Potion> INFESTATION = POTIONS.register("infestation",
            () -> new Potion("infestation", new EffectInstance(ModEffects.INFESTED.get(), 3600)));
}
