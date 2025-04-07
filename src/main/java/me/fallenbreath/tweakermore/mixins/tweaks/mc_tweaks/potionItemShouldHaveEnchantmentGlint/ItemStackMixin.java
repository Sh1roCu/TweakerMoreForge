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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.potionItemShouldHaveEnchantmentGlint;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12006
//$$ import net.minecraft.component.DataComponentTypes;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19.4"))
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "hasFoil", at = @At("RETURN"), cancellable = true)
    private void potionItemShouldHaveEnchantmentGlint_alwaysHasGlint(CallbackInfoReturnable<Boolean> cir) {
        if (TweakerMoreConfigs.POTION_ITEM_SHOULD_HAVE_ENCHANTMENT_GLINT.getBooleanValue()) {
            var self = (ItemStack) (Object) this;
            if (self.getItem() instanceof PotionItem) {
                //#if MC >= 12006
                boolean hasEffect = self.get(DataComponents.POTION_CONTENTS) != null;
                //#else
                //$$ boolean hasEffect = !PotionUtil.getPotionEffects(self).isEmpty();
                //#endif
                cir.setReturnValue(cir.getReturnValue() || hasEffect);
            }
        }
    }
}