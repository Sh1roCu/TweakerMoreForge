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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling.compat.sodium;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = {@Condition(ModIds.sodium)})
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.viewport.frustum.SimpleFrustum")
public abstract class JomlFrustumMixin implements CouldBeAlwaysVisibleFrustum {
    @Unique
    private boolean alwaysVisible = false;

    @Override
    public void setAlwaysVisible$TKM(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    @Override
    public boolean getAlwaysVisible$TKM() {
        return this.alwaysVisible;
    }

    @SuppressWarnings({"rawtypes", "unchecked", "UnresolvedMixinReference"})
    @Inject(
            method = "testAab",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void disableCameraFrustumCulling_implementAlwaysVisible(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable cir) {
        if (this.alwaysVisible) {
            cir.setReturnValue(true);
        }
    }
}
