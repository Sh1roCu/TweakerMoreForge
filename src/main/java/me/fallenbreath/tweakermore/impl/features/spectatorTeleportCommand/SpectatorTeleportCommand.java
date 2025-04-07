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

package me.fallenbreath.tweakermore.impl.features.spectatorTeleportCommand;

import cn.sh1rocu.tweakermoreforge.TweakerMoreForge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.UUID;

@EventBusSubscriber(modid = TweakerMoreForge.MODID)
public class SpectatorTeleportCommand {
    @SubscribeEvent
    public static void onCmdRegister(RegisterCommandsEvent event) {
        String prefix = TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND_PREFIX.getStringValue();
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        TweakerMoreMod.LOGGER.debug("(spectatorTeleportCommand) Registered client-side command with prefix '{}'", prefix);
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(prefix)
                .requires(s -> TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND.getBooleanValue())
                .then(Commands.argument("target", EntityArgument.entity())
                        .executes(context -> doSpectatorTeleport(context.getSource(), getEntity(context, "target"))));
        dispatcher.register(builder);
    }

    public static UUID getEntity(CommandContext<CommandSourceStack> context, String target) throws CommandSyntaxException {
        EntitySelector selector = context.getArgument(target, EntitySelector.class);
        return EntitySelectorHack.getSingleEntityUuid(selector, context.getSource());
    }

    private static int doSpectatorTeleport(CommandSourceStack source, UUID target) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return 0;
        if (!player.isSpectator()) {
            source.sendFailure(Messenger.tr("tweakermore.impl.spectatorTeleportCommand.need_spectator"));
            return 0;
        }
        TweakerMoreMod.LOGGER.info("Performing spectator teleport to entity {}", target);
        player.connection.send(new ServerboundTeleportToEntityPacket(target));
        return 1;
    }
}