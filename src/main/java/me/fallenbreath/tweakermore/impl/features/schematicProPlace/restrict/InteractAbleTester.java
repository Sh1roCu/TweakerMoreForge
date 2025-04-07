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

import me.fallenbreath.tweakermore.util.BlockUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
//#endif

/**
 * Test if the block is interact-able
 */
public interface InteractAbleTester {
    boolean isInteractAble(Player player, BlockState worldState);

    default InteractAbleTester and(InteractAbleTester other) {
        return (player, worldState) -> this.isInteractAble(player, worldState) && other.isInteractAble(player, worldState);
    }

    static InteractAbleTester always() {
        return (player, worldState) -> true;
    }

    static InteractAbleTester playerCanModifyWorld() {
        return (player, worldState) -> player.mayBuild();
    }

    /**
     * For doors / trapdoors
     */
    static InteractAbleTester canOpenByHand() {
        //#if MC >= 12000
        return (player, worldState) -> BlockUtils.getBlockSetType(worldState.getBlock()).map(BlockSetType::canOpenByHand).orElse(false);
        //#else
        //$$ return (player, worldState) -> worldState.getMaterial() != Material.METAL;
        //#endif
    }
}
