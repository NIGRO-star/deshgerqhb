package com.infestationpotion;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * An arrow that hits a mob or player while inside an upward bubble column (water above soul sand)
 * does not disappear: it is given piercing, so it keeps flying and floating after the hit.
 * (Vanilla piercing hits each entity only once, then the arrow continues.)
 */
@Mod.EventBusSubscriber(modid = InfestationPotionMod.MODID)
public class BubbleColumnArrows {

    @SubscribeEvent
    public static void onImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractArrowEntity) || entity.level.isClientSide) {
            return;
        }
        if (event.getRayTraceResult().getType() != RayTraceResult.Type.ENTITY) {
            return;
        }
        if (!InfestationConfig.ARROWS_KEEP_FLYING_IN_BUBBLES.get()) {
            return;
        }
        AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
        if (isInUpwardBubbleColumn(arrow) && arrow.getPierceLevel() < 127) {
            arrow.setPierceLevel((byte) 127);
        }
    }

    private static boolean isInUpwardBubbleColumn(Entity arrow) {
        BlockPos pos = arrow.blockPosition();
        return isUpwardColumn(arrow.level.getBlockState(pos))
                || isUpwardColumn(arrow.level.getBlockState(pos.below()));
    }

    private static boolean isUpwardColumn(BlockState state) {
        return state.is(Blocks.BUBBLE_COLUMN) && !state.getValue(BubbleColumnBlock.DRAG_DOWN);
    }
}
