package com.infestationpotion;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Draws a small icon of the potion's (first) effect in the middle of every potion bottle
 * (drinkable, splash, lingering, vanilla or modded) in inventories and on the hotbar.
 * Client side only.
 */
@Mod.EventBusSubscriber(modid = InfestationPotionMod.MODID, value = Dist.CLIENT)
public class PotionIconOverlay {

    @SubscribeEvent
    public static void onContainerForeground(GuiContainerEvent.DrawForeground event) {
        if (!ClientConfig.SHOW_EFFECT_ICONS.get()) {
            return;
        }
        for (Slot slot : event.getGuiContainer().getMenu().slots) {
            if (slot.isActive()) {
                drawIcon(event.getMatrixStack(), slot.getItem(), slot.x, slot.y);
            }
        }
    }

    @SubscribeEvent
    public static void onHotbar(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (!ClientConfig.SHOW_EFFECT_ICONS.get()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.isSpectator()) {
            return;
        }
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        for (int i = 0; i < 9; i++) {
            drawIcon(event.getMatrixStack(), mc.player.inventory.getItem(i),
                    width / 2 - 90 + i * 20 + 2, height - 16 - 3);
        }
    }

    private static void drawIcon(MatrixStack matrixStack, ItemStack stack, int x, int y) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PotionItem)) {
            return;
        }
        List<EffectInstance> effects = PotionUtils.getMobEffects(stack);
        if (effects.isEmpty()) {
            return; // water bottle, awkward, mundane, thick...
        }

        Minecraft mc = Minecraft.getInstance();
        TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effects.get(0).getEffect());
        mc.getTextureManager().bind(sprite.atlas().location());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        // 8x8 icon centred on the bottle body, drawn above the item (z = 200)
        AbstractGui.blit(matrixStack, x + 4, y + 6, 200, 8, 8, sprite);
    }
}
