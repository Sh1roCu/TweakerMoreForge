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

package me.fallenbreath.tweakermore.util;

//#if MC >= 12000
//$$ import me.fallenbreath.tweakermore.mixins.util.TrapdoorBlockAccessor;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.block.DoorBlock;
//$$ import net.minecraft.block.BlockSetType;
//$$ import java.util.Optional;
//#endif

import me.fallenbreath.tweakermore.mixins.util.TrapdoorBlockAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Optional;

public class BlockUtils {
    public static boolean isReplaceable(BlockState state) {
        //#if MC >= 12000
        return state.canBeReplaced();
        //#else
        //$$ return state.getMaterial().isReplaceable();
        //#endif
    }

    //#if MC >= 12000

    /**
     * Notes: does not support ButtonBlock and AbstractPressurePlateBlock yet
     */
    public static Optional<BlockSetType> getBlockSetType(Block block) {
        if (block instanceof DoorBlock doorBlock) {
            return Optional.of(doorBlock.type());
        } else if (block instanceof TrapdoorBlockAccessor trapdoorBlock) {
            return Optional.ofNullable(trapdoorBlock.getType());
        }
        return Optional.empty();
    }
    //#endif
}
