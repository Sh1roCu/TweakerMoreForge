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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.minihud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
//#endif

//#if MC >= 12101
//$$ import org.apache.commons.lang3.tuple.Pair;
//$$ import net.minecraft.nbt.NbtCompound;
//#endif

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin {
    // no bee hive in 1.14
    //#if MC >= 11500
    @ModifyExpressionValue(
            method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V",
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lfi/dy/masa/minihud/config/InfoToggle;BEE_COUNT:Lfi/dy/masa/minihud/config/InfoToggle;",
                            remap = false
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 12101
                    //$$ target = "Lfi/dy/masa/minihud/event/RenderHandler;getTargetedBlockEntity(Lnet/minecraft/world/World;Lnet/minecraft/client/Minecraft;)Lorg/apache/commons/lang3/tuple/Pair;",
                    //#else
                    target = "Lfi/dy/masa/minihud/event/RenderHandler;getTargetedBlockEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/client/Minecraft;)Lorg/apache/commons/lang3/tuple/Pair;",
                    //#endif
                    ordinal = 0,
                    remap = true
            ),
            remap = false
    )
    //#if MC >= 12101
    private Pair<BlockEntity, CompoundTag> serverDataSyncer4BeehiveBeeCount(Pair<BlockEntity, CompoundTag> original)
    //#else
    //$$ private BlockEntity serverDataSyncer4BeehiveBeeCount(BlockEntity blockEntity)
    //#endif
    {
        if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue()) {
            //#if MC >= 12101
            BlockEntity blockEntity = original.getLeft();
            //#endif
            if (blockEntity instanceof BeehiveBlockEntity && !Minecraft.getInstance().isSingleplayer()) {
                ServerDataSyncer.getInstance().syncBlockEntity(blockEntity);
            }
        }
        //#if MC >= 12101
        return original;
        //#else
        //$$ return blockEntity;
        //#endif
    }
    //#endif
}
