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

package me.fallenbreath.tweakermore.impl.features.infoView.commandBlock;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.commandBlock.CommandSuggestorAccessor;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
//$$ import net.minecraft.text.OrderedText;
//#endif

public class CommandBlockContentRenderer extends CommonScannerInfoViewer {
    public CommandBlockContentRenderer() {
        super(
                TweakerMoreConfigs.INFO_VIEW_COMMAND_BLOCK,
                TweakerMoreConfigs.INFO_VIEW_COMMAND_BLOCK_RENDER_STRATEGY,
                TweakerMoreConfigs.INFO_VIEW_COMMAND_BLOCK_TARGET_STRATEGY
        );
    }

    @Override
    public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof CommandBlock && world.getBlockEntity(pos) instanceof CommandBlockEntity;
    }

    @Override
    public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CommandBlockEntity)) {
            return;
        }

        BaseCommandBlock executor = ((CommandBlockEntity) blockEntity).getCommandBlock();
        String command = executor.getCommand();
        Component lastOutput = executor.getLastOutput();
        final int MAX_WIDTH = TweakerMoreConfigs.INFO_VIEW_COMMAND_BLOCK_MAX_WIDTH.getIntegerValue();

        // parse command for highlight logic
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        StringReader stringReader = new StringReader(command);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.read();
        }

        ParseResults<SharedSuggestionProvider> parse = player.connection.getCommands().parse(stringReader, player.connection.getSuggestionsProvider());
        // trim command
        String trimmedCommand = TextRenderingUtil.trim(command, MAX_WIDTH);
        //#if MC >= 11600

        FormattedCharSequence
                //#else
                //$$ String
                //#endif

                displayText = CommandSuggestorAccessor.invokeHighlight(parse, trimmedCommand, 0);
        if (trimmedCommand.length() < command.length()) {
            //#if MC >= 11600
            displayText = FormattedCharSequence.fromPair(displayText, TextRenderingUtil.string2orderedText(ChatFormatting.DARK_GRAY + "..."));
            //#else
            //$$ displayText += Formatting.DARK_GRAY + "...";
            //#endif
        }

        // render
        TextRenderer textRenderer = TextRenderer.create().
                text(displayText).atCenter(pos).
                fontScale(TextRenderer.DEFAULT_FONT_SCALE * TweakerMoreConfigs.INFO_VIEW_COMMAND_BLOCK_TEXT_SCALE.getDoubleValue()).
                bgColor(0x3F000000).
                shadow().seeThrough();
        if (!lastOutput.getString().isEmpty()) {
            //#if MC >= 11600
            FormattedCharSequence trimmedLastOutput = TextRenderingUtil.trim(
                    lastOutput.getVisualOrderText(),
                    MAX_WIDTH,
                    trimmedText -> FormattedCharSequence.fromPair(trimmedText, TextRenderingUtil.string2orderedText(ChatFormatting.DARK_GRAY + "..."))
            );
            //#else
            //$$ String trimmedLastOutput = TextRenderingUtil.trim(
            //$$        lastOutput.asFormattedString(),
            //$$        MAX_WIDTH,
            //$$        trimmedText -> trimmedText + Formatting.DARK_GRAY + "..."
            //$$ );
            //#endif
            textRenderer.addLine(trimmedLastOutput);
        }

        textRenderer.render();
    }
}
