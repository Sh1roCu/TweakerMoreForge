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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.movingPistonBlockSelectable;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.fallenbreath.tweakermore.impl.mc_tweaks.movingPistonBlockSelectable.MovingPistonBlockSelectableHelper;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(
            //#if MC > 12101
            method = "renderLevel",
            //#elseif MC >= 11500
            //$$ method = "renderBlockOutline",
            //#else
            //$$ method = "drawHighlightedBlockOutline",
            //#endif
            //$$ at = @At("HEAD")
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
            //#endif
    )
    private void movingPistonBlockSelectable_blockOutlineRender_start(CallbackInfo ci, @Share("") LocalBooleanRef hasSet) {
        if (MovingPistonBlockSelectableHelper.shouldEnableFeature()) {
            MovingPistonBlockSelectableHelper.applyOutlineShapeOverride.set(true);
            hasSet.set(true);
        }
    }

    @Inject(
            //#if MC > 12101
            method = "renderLevel",
            //#elseif MC >= 11500
            //#else
            //$$ method = "drawHighlightedBlockOutline",
            //#endif
            //$$ at = @At("TAIL")
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
                    shift = At.Shift.AFTER
            )
            //#endif
    )
    private void movingPistonBlockSelectable_blockOutlineRender_end(CallbackInfo ci, @Share("") LocalBooleanRef hasSet) {
        if (hasSet.get()) {
            MovingPistonBlockSelectableHelper.applyOutlineShapeOverride.set(false);
        }
    }
}
