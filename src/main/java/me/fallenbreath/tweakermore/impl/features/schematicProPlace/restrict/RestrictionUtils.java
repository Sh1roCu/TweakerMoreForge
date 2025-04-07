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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace.restrict;

import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.features.schematicProPlace.BlockItemAccessor;
import me.fallenbreath.tweakermore.util.IdentifierUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

class RestrictionUtils {
    public static boolean isItemInRestrictorWhitelist(ItemStack itemStack) {
        return TweakerMoreConfigs.SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_ITEM_WHITELIST.getStrings().
                stream().
                map(IdentifierUtils::tryParseOrNull).
                filter(Objects::nonNull).
                map(BuiltInRegistries.ITEM::get).
                anyMatch(item -> Objects.equals(
                        //#if MC >= 12103
                        item.map(Holder.Reference::value).orElse(null),
                        //#else
                        //$$ item,
                        //#endif
                        itemStack.getItem()
                ));
    }

    public static boolean isWithinLayerRange(LayerRange layerRange, BlockPos pos, int margin) {
        return layerRange.intersectsBox(
                pos.offset(-margin, -margin, -margin),
                pos.offset(margin, margin, margin)
        );
    }

    /**
     * Just a simple check. Not perfect, but it's safe enough
     */
    public static ItemStack getPlayerUsingStack(Player player) {
        ItemStack stackToUse = player.getMainHandItem();
        if (stackToUse.isEmpty()) {
            stackToUse = player.getOffhandItem();
        }
        return stackToUse;
    }

    /**
     * What will the block state be like if the player does the block placement
     */
    public static Optional<BlockState> getStateToPlace(BlockPlaceContext context, ItemStack stackToUse) {
        if (stackToUse.getItem() instanceof BlockItem blockItem) {
            // ref: net.minecraft.item.BlockItem.place(net.minecraft.item.ItemPlacementContext)
            BlockPlaceContext ctx = blockItem.updatePlacementContext(context);
            if (ctx != null) {
                return Optional.ofNullable(((BlockItemAccessor) blockItem).invokeGetPlacementState(ctx));
            }
        }
        return Optional.empty();
    }
}
