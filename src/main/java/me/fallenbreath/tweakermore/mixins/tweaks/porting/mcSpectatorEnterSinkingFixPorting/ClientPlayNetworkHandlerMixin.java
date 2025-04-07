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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.mcSpectatorEnterSinkingFixPorting;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin
        //#if MC >= 12002
        extends ClientCommonPacketListenerImpl
        //#endif
{
    //#if MC >= 12002
    protected ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }
    //#else
    //$$ @Shadow
    //$$ private Minecraft minecraft;
    //#endif

    //#if MC >= 11903
    @Inject(
            method = "applyPlayerInfoUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/PlayerInfo;setGameMode(Lnet/minecraft/world/level/GameType;)V"
            )
    )
    private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(ClientboundPlayerInfoUpdatePacket.Action action, ClientboundPlayerInfoUpdatePacket.Entry entry, PlayerInfo playerInfo, CallbackInfo ci)

    //#else
    //$$ @Inject(
    //$$        method = "onPlayerList",
    //$$        at = @At(
    //$$            value = "INVOKE",
    //$$            target = "Lnet/minecraft/client/network/PlayerListEntry;setGameMode(Lnet/minecraft/world/GameMode;)V"
    //$$    ),
    //$$    locals = LocalCapture.CAPTURE_FAILHARD
    //$$)
    //$$private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(PlayerListS2CPacket packet, CallbackInfo ci, Iterator<?> iterator, PlayerListS2CPacket.Entry packetEntry, PlayerListEntry playerListEntry)
    //#endif
    {
        if (TweakerMoreConfigs.MC_SPECTATOR_ENTER_SINKING_FIX_PORTING.getBooleanValue()) {
            LocalPlayer player = this.minecraft.player;

            // is the client's player
            if (player != null && player.getUUID().equals(
                    //#if MC >= 11903
                    playerInfo.getProfile().getId()
                    //#else
                    //$$ playerInfo.getProfile().getId()
                    //#endif
            )) {
                GameType newGameMode =
                        //#if MC >= 11903
                        playerInfo.getGameMode();
                //#else
                //$$ playerInfo.getGameMode();
                //#endif
                if (newGameMode == GameType.SPECTATOR && newGameMode != entry.gameMode())  // entering spectator
                {
                    // clean the velocity at y-axis
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
                }
            }
        }
    }
}
