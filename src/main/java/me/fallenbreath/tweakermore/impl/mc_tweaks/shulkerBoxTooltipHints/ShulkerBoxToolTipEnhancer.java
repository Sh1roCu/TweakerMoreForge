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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.AbstractHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.EnchantmentHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder.PotionHintBuilder;
import me.fallenbreath.tweakermore.util.InventoryUtils;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

//#if MC >= 12006
//$$ import net.minecraft.item.Item;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.Component.MutableComponent;
//#endif

public class ShulkerBoxToolTipEnhancer {
    private static final List<AbstractHintBuilder> HINT_BUILDERS = ImmutableList.of(
            new EnchantmentHintBuilder(),
            new PotionHintBuilder()
    );

    public static void appendContentHints(
            //#if MC >= 12006
            Item.TooltipContext conComponent,
            //#endif
            ItemStack itemStack,
            //#if MC >= 11600
            MutableComponent Component
            //#else
            //$$ Font Component
            //#endif
    ) {
        HINT_BUILDERS.stream().
                map(builder -> builder.build(
                        //#if MC >= 12006
                        conComponent,
                        //#endif
                        itemStack
                )).
                filter(Objects::nonNull).
                forEach(Component::append);
    }

    public static void applyFillLevelHint(ItemStack skulker, List<Component> tooltip) {
        if (TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_FILL_LEVEL_HINT.getBooleanValue() && !tooltip.isEmpty()) {
            int slotAmount = InventoryUtils.getInventorySlotAmount(skulker);
            if (slotAmount == -1) {
                return;
            }

            NonNullList<ItemStack> stackList = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(skulker, slotAmount);
            if (stackList.isEmpty()) {
                return;
            }

            double sum = 0;
            for (ItemStack stack : stackList) {
                sum += 1.0D * stack.getCount() / stack.getMaxStackSize();
            }

            double ratio = sum / slotAmount;
            ChatFormatting color = ratio >= 1.0D ? ChatFormatting.DARK_GREEN : ChatFormatting.GRAY;
            Component fillLevelComponent = Messenger.s(String.format("%.2f%%", 100 * ratio), color);

            // let fillLevelComponent be rendered right-aligned
            String spacing = " ";
            Component firstLine = Messenger.c(tooltip.get(0), spacing, fillLevelComponent);
            Font Font = Minecraft.getInstance().font;

            ToIntFunction<Component> ComponentLenCalc = text ->
                    //#if MC >= 11600
                    Font.width(text);
            //#else
            //$$Font.width(text.getString());
            //#endif

            int maxWidth = tooltip.stream().mapToInt(ComponentLenCalc).max().orElse(0);

            while (true) {
                List<Component> siblings = firstLine.getSiblings();
                spacing += " ";
                Component prevSpacing = siblings.get(1);
                siblings.set(1, Messenger.s(spacing));
                if (ComponentLenCalc.applyAsInt(firstLine) > maxWidth) {
                    siblings.set(1, prevSpacing);  // rollback
                    break;
                }
            }
            tooltip.set(0, firstLine);
        }
    }
}
