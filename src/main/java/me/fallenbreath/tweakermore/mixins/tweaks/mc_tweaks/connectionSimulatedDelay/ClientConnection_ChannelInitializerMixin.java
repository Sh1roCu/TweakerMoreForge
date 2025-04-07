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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.connectionSimulatedDelay;

import io.netty.channel.Channel;
import me.fallenbreath.tweakermore.impl.mc_tweaks.connectionSimulatedDelay.ChannelInboundDelayer;
import me.fallenbreath.tweakermore.impl.mc_tweaks.connectionSimulatedDelay.ChannelOutboundDelayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {
        "net/minecraft/network/Connection$1",  // anonymous class in connect
        "net/minecraft/network/Connection$" +  // anonymous class in connectLocal
                //#if MC >= 12006
                "3"
        //#else
        //$$ "2"
        //#endif
})
public abstract class ClientConnection_ChannelInitializerMixin {
    @Inject(method = "initChannel", at = @At("HEAD"))
    private void connectionSimulatedDelay(Channel channel, CallbackInfo ci) {
        channel.pipeline().addLast(new ChannelInboundDelayer());
        channel.pipeline().addLast(new ChannelOutboundDelayer());
    }
}
