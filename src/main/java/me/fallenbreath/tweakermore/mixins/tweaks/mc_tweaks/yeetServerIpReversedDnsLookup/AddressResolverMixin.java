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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.yeetServerIpReversedDnsLookup;

import com.mojang.logging.LogUtils;
import me.fallenbreath.tweakermore.impl.mc_tweaks.yeetServerIpReversedDnsLookup.InetAddressPatcher;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddressResolver;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

// used in mc >= 1.17

/**
 * can not inject interface in forge1.20.1 because of Mixin8.5,
 * so use ServerNameResolver to replace
 */
//@Mixin(ServerAddressResolver.class)
@Mixin(ServerNameResolver.class)
public class AddressResolverMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/resolver/ServerNameResolver;<init>(Lnet/minecraft/client/multiplayer/resolver/ServerAddressResolver;Lnet/minecraft/client/multiplayer/resolver/ServerRedirectHandler;Lnet/minecraft/client/multiplayer/resolver/AddressCheck;)V"),
            index = 0
    )
    private static ServerAddressResolver setHostnameToIpAddressToAvoidReversedDnsLookupOnGetHostname(ServerAddressResolver resolver) throws UnknownHostException {
        return (serverAddress) -> {
            try {
                InetAddress original = InetAddress.getByName(serverAddress.getHost());
                InetAddress result = InetAddressPatcher.patch(serverAddress.getHost(), original);
                return Optional.of(ResolvedServerAddress.from(new InetSocketAddress(result, serverAddress.getPort())));
            } catch (UnknownHostException $$2) {
                LogUtils.getLogger().debug("Couldn't resolve server {} address", serverAddress.getHost(), $$2);
                return Optional.empty();
            }
        };
    }
}