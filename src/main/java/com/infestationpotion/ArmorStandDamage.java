package com.infestationpotion;

import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/**
 * Called from the coremod (see coremods/armorstand.js) at the start of ArmorStandEntity.hurt.
 * When this returns true, vanilla's armor stand damage code is skipped.
 *
 * Fire damage (fire block, campfire, lava, burning) is applied to the armor stand's 20 health
 * (10 hearts) exactly like for a player: the amount of the damage source, at most one hit
 * every 10 ticks (0.5 s). Every other kind of damage keeps the vanilla behaviour.
 */
public class ArmorStandDamage {
    private static final String LAST_HIT_KEY = "InfestationLastFireHit";

    public static boolean handleFire(ArmorStandEntity stand, DamageSource source, float amount) {
        if (stand.level.isClientSide || !source.isFire() || !stand.isAlive()) {
            return false;
        }
        if (!enabled()) {
            return false;
        }
        if (stand.isMarker() || stand.isInvulnerableTo(source)) {
            return true; // vanilla ignores damage here too
        }

        long now = stand.level.getGameTime();
        CompoundNBT data = stand.getPersistentData();
        if (data.contains(LAST_HIT_KEY) && now - data.getLong(LAST_HIT_KEY) < 10L) {
            return true; // invulnerable between hits, like a player
        }
        data.putLong(LAST_HIT_KEY, now);

        float newHealth = stand.getHealth() - amount;
        if (newHealth > 0.0F) {
            stand.setHealth(newHealth);
        } else {
            breakStand(stand);
        }
        return true;
    }

    private static boolean enabled() {
        try {
            return InfestationConfig.ARMOR_STAND_PLAYER_LIKE_FIRE.get();
        } catch (IllegalStateException e) {
            return true; // config not loaded yet: use the default
        }
    }

    private static void breakStand(ArmorStandEntity stand) {
        World level = stand.level;
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack drop = new ItemStack(Items.ARMOR_STAND);
            if (stand.hasCustomName()) {
                drop.setHoverName(stand.getCustomName());
            }
            stand.spawnAtLocation(drop);
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                ItemStack held = stand.getItemBySlot(slot);
                if (!held.isEmpty()) {
                    stand.spawnAtLocation(held);
                    stand.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
        level.playSound(null, stand.getX(), stand.getY(), stand.getZ(),
                SoundEvents.ARMOR_STAND_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        stand.remove();
    }
}
