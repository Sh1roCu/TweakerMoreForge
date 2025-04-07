/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.yeetServerIpReversedDnsLookup;

import net.minecraft.client.multiplayer.ServerStatusPinger;
import org.spongepowered.asm.mixin.Mixin;

// used in mc < 1.17
@Mixin(ServerStatusPinger.class)
public abstract class MultiplayerServerListPingerMixin {
/*	@Shadow @Final private Minecraft minecraft;

	@WrapOperation(
			method = "add",
			at = @At(
					value = "INVOKE",
					target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"
			)
	)
	private InetAddress setHostnameToIpAddressToAvoidReversedDnsLookupOnGetHostname_ping(String hostName, Operation<InetAddress> original) throws UnknownHostException
	{
		minecraft.getConnection()
		return InetAddressPatcher.patch(hostName, original.call(hostName));
	}*/
}
