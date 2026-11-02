package com.infestationpotion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Vanilla splash potions scale the duration by distance (hit directly = 100%, 3.9 blocks away
 * = about 2%). Here every entity inside the splash range gets the FULL duration.
 *
 * Vanilla applies the scaled effect right after the impact event, so we remember who was in range
 * and re-apply the full-length effect at the end of the same server tick. Re-adding a longer
 * effect simply replaces the shorter one.
 */
@Mod.EventBusSubscriber(modid = InfestationPotionMod.MODID)
public class ExactSplashDuration {

    private static class Pending {
        final LivingEntity entity;
        final EffectInstance effect;

        Pending(LivingEntity entity, EffectInstance effect) {
            this.entity = entity;
            this.effect = effect;
        }
    }

    private static final List<Pending> PENDING = new ArrayList<>();

    /** True if this effect must always get its full duration from splash potions. */
    public static boolean isExact(Effect effect) {
        if (effect == ModEffects.INFESTED.get()) {
            return true;
        }
        return InfestationConfig.EXACT_SPLASH_ALL_POTIONS.get();
    }

    @SubscribeEvent
    public static void onImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof PotionEntity) || event.getEntity().level.isClientSide) {
            return;
        }
        PotionEntity potion = (PotionEntity) event.getEntity();
        ItemStack stack = potion.getItem();
        if (stack.getItem() != Items.SPLASH_POTION) {
            return; // lingering potions make a cloud, they are not scaled by distance
        }

        List<EffectInstance> exact = new ArrayList<>();
        for (EffectInstance inst : PotionUtils.getMobEffects(stack)) {
            if (!inst.getEffect().isInstantenous() && isExact(inst.getEffect())) {
                exact.add(inst);
            }
        }
        if (exact.isEmpty()) {
            return;
        }

        // Same range as vanilla: 4 blocks (squared distance < 16).
        AxisAlignedBB box = potion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        for (LivingEntity target : potion.level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (target instanceof ArmorStandEntity || !target.isAffectedByPotions()) {
                continue; // armor stands are handled in ArmorStandPotions
            }
            if (potion.distanceToSqr(target) >= 16.0D) {
                continue;
            }
            for (EffectInstance inst : exact) {
                PENDING.add(new Pending(target, new EffectInstance(inst)));
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) {
            return;
        }
        for (Pending pending : PENDING) {
            if (pending.entity.isAlive()) {
                pending.entity.addEffect(pending.effect);
            }
        }
        PENDING.clear();
    }
}
