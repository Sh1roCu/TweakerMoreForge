/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.handlers;

import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.growthSpeed.AttachedStemBlockAccessor;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.growthSpeed.CropBlockAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

import static me.fallenbreath.tweakermore.util.Messenger.c;
import static me.fallenbreath.tweakermore.util.Messenger.s;

//#if MC >= 12004
//$$ import net.minecraft.registry.RegistryKeys;
//#endif

//#if MC >= 12001
//$$ import net.minecraft.block.PitcherCropBlock;
//$$ import net.minecraft.block.enums.DoubleBlockHalf;
//#endif

public class CropGrowthSpeedRendererHandler extends BasicGrowthSpeedRendererHandler {
    @Override
    public boolean isTarget(BlockState blockState) {
        Block block = blockState.getBlock();
        return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock
                //#if MC >= 12001
                || (block instanceof PitcherCropBlock && blockState.getValue(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER)
                //#endif
                ;
    }

    @Override
    public void addInfoLines(RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos, List<MutableComponent> lines) {
        Block block = world.getBlockState(pos).getBlock();
        Block cropBlock = block;
        if (block instanceof AttachedStemBlockAccessor) {
            //#if MC >= 12004
            var optional = world.getBestWorld().registryAccess().
                    get(((AttachedStemBlockAccessor) block).getStem());
            if (optional.isPresent()) {
                cropBlock = optional.get().value();
            }
            //#else
            //$$ cropBlock = ((AttachedStemBlockAccessor)block).getStem();
            //#endif
        }
        float baseSpeed = CropBlockAccessor.invokeGetAvailableMoisture(cropBlock.defaultBlockState(), world, pos);

        Attributes attributes = new Attributes();
        ChatFormatting color = heatColor(baseSpeed / 10.0);
        attributes.add(tr("crop.base"), s(round(baseSpeed, 6), color));

        {
            int randomBound = (int) (25.0F / baseSpeed) + 1;
            attributes.add(tr("crop.chance"), s("1/" + randomBound, color));

            int light = world.getBaseLightLevel(pos, 0);
            boolean lightToLiveOk = light >= getMinimumRequiredLightLevelToSurvive(cropBlock);
            boolean lightToGrowOk = light >= getMinimumRequiredLightLevelToGrowNaturally(cropBlock);
            boolean lightOk = lightToLiveOk && lightToGrowOk;
            ChatFormatting lightColor = lightOk ? ChatFormatting.GREEN : lightToLiveOk ? ChatFormatting.GOLD : ChatFormatting.RED;
            Component value = c(s(light + " ", lightColor), bool(lightOk, lightColor));
            attributes.add(tr("crop.light"), value, lightOk);
        }

        attributes.export(lines, isCrossHairPos);
    }

    private int getMinimumRequiredLightLevelToGrowNaturally(Block block) {
        // net.minecraft.block.PitcherCropBlock#randomTick  --  no check
        // net.minecraft.block.CropBlock#randomTick         --  9
        // net.minecraft.block.CropBlock#hasEnoughLightAt   --  8
        //#if MC >= 12001
        if (block instanceof PitcherCropBlock) {
            return 8;
        }
        //#endif
        return 9;
    }

    private int getMinimumRequiredLightLevelToSurvive(Block block) {
        // net.minecraft.block.CropBlock#hasEnoughLightAt
        return 8;
    }
}
