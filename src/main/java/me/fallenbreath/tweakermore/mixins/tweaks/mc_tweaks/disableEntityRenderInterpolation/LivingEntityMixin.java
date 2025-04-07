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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableEntityRenderInterpolation.DisableEntityRenderInterpolationHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    protected int
            //#if MC >= 11500
            lerpSteps;
    //#else
    //$$ field_6210;
    //#endif

    @Inject(method = "lerpTo", at = @At("TAIL"))
    private void disableEntityRenderInterpolation_noExtraInterpolationSteps1(
            double x, double y, double z, float yaw, float pitch, int interpolationSteps,
            //#if MC < 12002
            //$$ boolean interpolate,
            //#endif
            CallbackInfo ci
    ) {
        if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue()) {
            //#if MC >= 11500
            this.lerpSteps = 1;
            //#else
            //$$ this.field_6210 = 1;
            //#endif

            if (DisableEntityRenderInterpolationHelper.shouldUpdatePositionOrAnglesDirectly()) {
                super.lerpTo(
                        x, y, z, yaw, pitch, interpolationSteps
                        //#if MC < 12002
                        //$$ , interpolate
                        //#endif
                );
            }
        }
    }

    @ModifyVariable(method = "lerpHeadTo", at = @At("HEAD"), argsOnly = true)
    private int disableEntityRenderInterpolation_noExtraInterpolationSteps2(int interpolationSteps, float yaw, int interpolationSteps_) {
        if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue()) {
            interpolationSteps = 1;

            if (DisableEntityRenderInterpolationHelper.shouldUpdatePositionOrAnglesDirectly()) {
                super.lerpHeadTo(yaw, interpolationSteps);
            }
        }
        return interpolationSteps;
    }
}
