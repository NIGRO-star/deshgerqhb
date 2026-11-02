package com.infestationpotion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Makes thrown potions (splash and lingering, vanilla and modded) affect armor stands,
 * which vanilla ignores. Also makes arrows trigger the Infested effect on armor stands.
 */
@Mod.EventBusSubscriber(modid = InfestationPotionMod.MODID)
public class ArmorStandPotions {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        if (projectile.level.isClientSide) {
            return;
        }
        RayTraceResult ray = event.getRayTraceResult();

        if (projectile instanceof PotionEntity) {
            handleSplash((PotionEntity) projectile, ray);
        } else if (projectile instanceof AbstractArrowEntity && ray.getType() == RayTraceResult.Type.ENTITY) {
            Entity hit = ((EntityRayTraceResult) ray).getEntity();
            if (hit instanceof ArmorStandEntity) {
                InfestationEvents.trySpawnSilverfish((ArmorStandEntity) hit);
            }
        }
    }

    /** Splash potions: same range and falloff as vanilla (4 blocks), applied to armor stands. */
    private static void handleSplash(PotionEntity potion, RayTraceResult ray) {
        ItemStack stack = potion.getItem();
        if (stack.getItem() != Items.SPLASH_POTION) {
            return; // lingering potions are handled by the cloud logic below
        }
        List<EffectInstance> effects = PotionUtils.getMobEffects(stack);
        if (effects.isEmpty()) {
            return;
        }

        Entity hitEntity = ray.getType() == RayTraceResult.Type.ENTITY
                ? ((EntityRayTraceResult) ray).getEntity() : null;
        AxisAlignedBB box = potion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);

        for (ArmorStandEntity stand : potion.level.getEntitiesOfClass(ArmorStandEntity.class, box)) {
            double distSq = potion.distanceToSqr(stand);
            if (distSq >= 16.0D) {
                continue;
            }
            double scale = stand == hitEntity ? 1.0D : 1.0D - Math.sqrt(distSq) / 4.0D;
            for (EffectInstance effect : effects) {
                Effect type = effect.getEffect();
                if (type.isInstantenous()) {
                    type.applyInstantenousEffect(potion, potion.getOwner(), stand, effect.getAmplifier(), scale);
                } else {
                    int duration = ExactSplashDuration.isExact(type)
                            ? effect.getDuration()
                            : (int) (scale * (double) effect.getDuration() + 0.5D);
                    if (duration > 20) {
                        stand.addEffect(new EffectInstance(type, duration, effect.getAmplifier(),
                                effect.isAmbient(), effect.isVisible()));
                    }
                }
            }
        }
    }

    /** Lingering potion clouds: checked from the armor stand's side once per second. */
    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof ArmorStandEntity)) {
            return;
        }
        ArmorStandEntity stand = (ArmorStandEntity) event.getEntityLiving();
        if (stand.level.isClientSide || stand.tickCount % 20 != 0) {
            return;
        }

        for (AreaEffectCloudEntity cloud
                : stand.level.getEntitiesOfClass(AreaEffectCloudEntity.class, stand.getBoundingBox())) {
            double dx = stand.getX() - cloud.getX();
            double dz = stand.getZ() - cloud.getZ();
            float radius = cloud.getRadius();
            if (dx * dx + dz * dz > (double) (radius * radius)) {
                continue;
            }

            CompoundNBT tag = cloud.saveWithoutId(new CompoundNBT());
            if (tag.getInt("Age") < tag.getInt("WaitTime")) {
                continue; // cloud is still in its waiting phase
            }

            for (EffectInstance effect : getCloudEffects(tag)) {
                Effect type = effect.getEffect();
                if (type.isInstantenous()) {
                    type.applyInstantenousEffect(cloud, cloud.getOwner(), stand, effect.getAmplifier(), 0.5D);
                } else {
                    stand.addEffect(new EffectInstance(effect));
                }
            }
        }
    }

    /** Same effect list the vanilla cloud uses: base potion effects at 1/4 duration plus custom effects. */
    private static List<EffectInstance> getCloudEffects(CompoundNBT tag) {
        List<EffectInstance> result = new ArrayList<>();
        if (tag.contains("Potion", 8)) {
            Potion potion = Potion.byName(tag.getString("Potion"));
            for (EffectInstance e : potion.getEffects()) {
                result.add(new EffectInstance(e.getEffect(), e.getDuration() / 4, e.getAmplifier(),
                        e.isAmbient(), e.isVisible()));
            }
        }
        if (tag.contains("Effects", 9)) {
            ListNBT list = tag.getList("Effects", 10);
            for (int i = 0; i < list.size(); i++) {
                EffectInstance e = EffectInstance.load(list.getCompound(i));
                if (e != null) {
                    result.add(e);
                }
            }
        }
        return result;
    }
}
