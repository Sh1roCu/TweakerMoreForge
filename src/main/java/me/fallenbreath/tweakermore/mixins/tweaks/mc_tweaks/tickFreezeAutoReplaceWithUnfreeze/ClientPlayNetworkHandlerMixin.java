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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tickFreezeAutoReplaceWithUnfreeze;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {
    // impl in mc1.20.3+ (subproject 1.20.4)

    @ModifyVariable(method = "sendCommand", at = @At("HEAD"), argsOnly = true)
    private String tickFreezeAutoReplaceWithUnfreeze_doModification(String command) {
        if (TweakerMoreConfigs.TICK_FREEZE_AUTO_REPLACE_WITH_UNFREEZE.getBooleanValue()) {
            if ("tick freeze".equals(command)) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null && mc.level.tickRateManager().isFrozen()) {
                    command = "tick unfreeze";
                }
            }
        }
        return command;
    }
}
