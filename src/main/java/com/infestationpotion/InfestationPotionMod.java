package com.infestationpotion;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InfestationPotionMod.MODID)
public class InfestationPotionMod {
    public static final String MODID = "infestationpotion";

    public InfestationPotionMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEffects.EFFECTS.register(bus);
        ModPotions.POTIONS.register(bus);
        bus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, InfestationConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Awkward Potion + Stone -> Potion of Infestation
        // Splash (Gunpowder), Lingering (Dragon's Breath) and Tipped Arrows
        // are handled automatically by vanilla for every registered potion.
        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new InfestationBrewingRecipe()));
    }
}
