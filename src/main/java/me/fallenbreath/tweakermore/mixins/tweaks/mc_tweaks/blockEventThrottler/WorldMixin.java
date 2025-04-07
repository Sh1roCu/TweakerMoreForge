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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.blockEventThrottler;

import me.fallenbreath.tweakermore.impl.mc_tweaks.blockEventThrottler.BlockEventThrottler;
import me.fallenbreath.tweakermore.impl.mc_tweaks.blockEventThrottler.TimedCounter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Level.class, priority = 500)
public abstract class WorldMixin {
    @Unique
    private final TimedCounter blockEventThrottlerCounter = new TimedCounter();

    @Inject(method = "blockEvent", at = @At("HEAD"), cancellable = true)
    private void blockEventThrottler_throttle$TKM(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
        BlockEventThrottler.throttle((Level) (Object) this, pos, block, this.blockEventThrottlerCounter, ci);
    }
}
