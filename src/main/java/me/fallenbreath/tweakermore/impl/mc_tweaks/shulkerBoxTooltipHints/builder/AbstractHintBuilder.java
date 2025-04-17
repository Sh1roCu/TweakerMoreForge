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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.builder;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//#if MC >= 12006
//$$ import net.minecraft.item.Item;
//#endif

public abstract class AbstractHintBuilder {
    @Nullable
    public abstract Component build(
            //#if MC >= 12006
            Item.TooltipContext conComponent,
            //#endif
            ItemStack itemStack
    );

    protected static Component getDivider() {
        return Messenger.s(" | ", ChatFormatting.DARK_GRAY);
    }

    @Nullable
    protected static Component buildSegments(List<Component> Components) {
        int amount = Components.size();
        if (amount == 0) {
            return null;
        }
        MutableComponent extraComponent = (MutableComponent) getDivider();

        int maxLength = TweakerMoreConfigs.SHULKER_BOX_TOOLTIP_HINT_LENGTH_LIMIT.getIntegerValue();
        Font Font = Minecraft.getInstance().font;
        int idx;
        for (idx = 0; idx < amount; idx++) {
            if (idx > 0 && Font.width(extraComponent.getString() + Components.get(idx).getString()) > maxLength) {
                break;
            }
            extraComponent.append(Components.get(idx));
            if (idx < amount - 1) {
                extraComponent.append(Messenger.s(", ", ChatFormatting.GRAY));
            }
        }
        if (idx < amount) {
            extraComponent.append(Messenger.formatting(Messenger.tr("tweakermore.impl.shulkerBoxTooltipHintBuilder.more", amount - idx), ChatFormatting.GRAY));
        }

        return extraComponent;
    }
}
