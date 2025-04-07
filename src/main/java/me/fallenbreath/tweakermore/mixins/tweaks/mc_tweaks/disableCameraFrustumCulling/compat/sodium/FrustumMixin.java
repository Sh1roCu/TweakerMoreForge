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
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inject after sodium's me.jellysquid.mods.sodium.mixin.core.MixinFrustum is applied, so priority 2000
 */
@Restriction(require = @Condition(type = Condition.Type.MIXIN, value = "me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling.compat.sodium.JomlFrustumMixin"))
@Mixin(value = Frustum.class, priority = 2000)
public abstract class FrustumMixin {
    @Dynamic("Added by sodium")
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "sodium$createFrustum",
            at = @At("TAIL"),
            remap = false
    )
    private void disableCameraFrustumCulling_implementAlwaysVisible(CallbackInfoReturnable<? extends CouldBeAlwaysVisibleFrustum> cir) {
        CouldBeAlwaysVisibleFrustum self = (CouldBeAlwaysVisibleFrustum) this;
        cir.getReturnValue().setAlwaysVisible$TKM(self.getAlwaysVisible$TKM());
    }
}
