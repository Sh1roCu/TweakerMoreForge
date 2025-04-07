/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.features.villagerOfferUsesDisplay;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.network.chat.Component
//$$ import me.fallenbreath.tweakermore.util.Messenger;
//#endif

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
public abstract class MerchantScreenWidgetButtonPageMixin extends Button {
    @SuppressWarnings("target")
    @Shadow
    @Final
    MerchantScreen this$0;

    @Shadow
    @Final
    //#if MC >= 11500
    int index;
    //#else
    //$$ int field_19165;
    //#endif

    public MerchantScreenWidgetButtonPageMixin(
            int x, int y, int width, int height,
            //#if MC >= 11600
            Component message,
            //#else
            //$$ String message,
            //#endif
            OnPress onPress

            //#if MC >= 11903
            , NarrationSupplier narrationSupplier
            //#endif
    ) {
        super(
                x, y, width, height, message, onPress
                //#if MC >= 11903
                , (CreateNarration) narrationSupplier
                //#endif
        );
    }

    @Inject(
            //#if MC >= 12000
            method = "renderToolTip",
            //#elseif MC >= 11700
            //$$ method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V",
            //#elseif MC >= 11600
            //$$ method = "renderToolTip(Lnet/minecraft/client/util/math/MatrixStack;II)V",
            //#else
            //$$ method = "renderToolTip(II)V",
            //#endif
            at = @At(
                    //#if MC >= 11903
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen$TradeOfferButton;getX()I",
                    //#else
                    //$$ value = "FIELD",
                    //$$ target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen$WidgetButtonPage;x:I",
                    //#endif
                    ordinal = 0
            )
    )
    private void renderMaxUsesAmount(
            //#if MC >= 12000
            GuiGraphics context,
            //#elseif MC >= 11600
            //$$ MatrixStack matrices,
            //#endif
            int mouseX, int mouseY, CallbackInfo ci
    ) {
        if (TweakerMoreConfigs.VILLAGER_OFFER_USES_DISPLAY.getBooleanValue()) {
            int x =
                    //#if MC >= 11903
                    this.getX();
            //#else
            //$$ this.x;
            //#endif
            if (x + 50 <= mouseX && mouseX <= x + 65) {
                //#if MC >= 11500
                int index = this.index;
                //#else
                //$$ int index = this.field_19165;
                //#endif
                MerchantOffer offer = this.this$0.getMenu().getOffers().get(index + ((MerchantScreenAccessor) this.this$0).getIndexStartOffset());

                String text = String.format("%d / %d", offer.getUses(), offer.getMaxUses());

                //#if MC >= 12000
                context.renderTooltip(((ScreenAccessor) this.this$0).getTextRenderer(), Messenger.s(text), mouseX, mouseY);
                //#else
                //$$ this.field_19166.renderTooltip(
                //$$         //#if MC >= 11600
                //$$        //$$ matrices, Messenger.s(text),
                //$$        //#else
                //$$        text,
                //$$        //#endif
                //$$        mouseX, mouseY
                //$$ );
                //#endif
            }
        }
    }
}
