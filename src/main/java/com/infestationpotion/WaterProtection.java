package com.infestationpotion;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Called from the coremod (see coremods/flowingfluid.js) at the start of
 * FlowingFluid.canHoldFluid. When this returns true, flowing water treats the block
 * like a solid wall: it cannot enter it, destroy it, waterlog it or flow through it.
 */
public class WaterProtection {

    public static boolean blocksWater(BlockState state, Fluid fluid) {
        if (fluid != Fluids.WATER && fluid != Fluids.FLOWING_WATER) {
            return false;
        }
        if (state.getBlock() instanceof CampfireBlock) { // campfire and soul campfire
            return enabled(InfestationConfig.CAMPFIRE_WATER_PROTECTION);
        }
        if (state.getBlock() instanceof AbstractPressurePlateBlock
                || state.getBlock() instanceof AbstractButtonBlock) {
            return enabled(InfestationConfig.PROTECT_PLATES_AND_BUTTONS);
        }
        return false;
    }

    private static boolean enabled(ForgeConfigSpec.BooleanValue value) {
        try {
            return value.get();
        } catch (IllegalStateException e) {
            return true; // config not loaded yet: use the default
        }
    }
}
