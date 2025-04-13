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

package me.fallenbreath.tweakermore.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Messenger {
    /*
     * ----------------------------
     *    Text Factories - Basic
     * ----------------------------
     */

    // simple Text
    public static MutableComponent s(Object text) {
        return
                //#if MC >= 11900
                Component.literal
                        //#else
                        //$$ new LiteralText
                        //#endif
                                (text.toString());
    }

    // textfy
    public static MutableComponent tf(Object text) {
        return text instanceof MutableComponent ? (MutableComponent) text : s(text);
    }

    // compound text
    public static MutableComponent c(Object... fields) {
        MutableComponent text = s("");
        for (Object field : fields) {
            text.append(Messenger.tf(field));
        }
        return text;
    }

    // Simple Text with formatting
    public static MutableComponent s(Object text, ChatFormatting textFormatting) {
        return formatting(s(text), textFormatting);
    }

    // Translation Text
    public static MutableComponent tr(String key, Object... args) {
        return
                //#if MC >= 11900
                Component.translatable
                        //#else
                        //$$ new TranslatableText
                        //#endif
                                (key, args);
    }

    // Fancy text
    // A copy will be made to make sure the original displayText will not be modified
    public static MutableComponent fancy(@NotNull MutableComponent displayText, @Nullable MutableComponent hoverText, @Nullable ClickEvent clickEvent) {
        MutableComponent text = copy(displayText);
        if (hoverText != null) {
            hover(text, hoverText);
        }
        if (clickEvent != null) {
            click(text, clickEvent);
        }
        return text;
    }

    public static MutableComponent join(MutableComponent joiner, Component... items) {
        MutableComponent text = s("");
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                text.append(joiner);
            }
            text.append(items[i]);
        }
        return text;
    }

    /*
     * --------------------
     *    Text Modifiers
     * --------------------
     */

    public static MutableComponent hover(MutableComponent text, HoverEvent hoverEvent) {
        //#if MC >= 11600
        style(text, text.getStyle().withHoverEvent(hoverEvent));
        //#else
        //$$ text.getStyle().withHoverEvent(hoverEvent);
        //#endif
        return text;
    }

    public static MutableComponent hover(MutableComponent text, MutableComponent hoverText) {
        return hover(
                text,
                //#if MC >= 12105
                //$$ new HoverEvent.ShowText(hoverText)
                //#else
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)
                //#endif
        );
    }

    public static MutableComponent click(MutableComponent text, ClickEvent clickEvent) {
        //#if MC >= 11600
        style(text, text.getStyle().withClickEvent(clickEvent));
        //#else
        //$$ text.getStyle().withClickEvent(clickEvent);
        //#endif
        return text;
    }

    public static MutableComponent formatting(MutableComponent text, ChatFormatting... formattings) {
        text.withStyle(formattings);
        return text;
    }

    public static MutableComponent style(MutableComponent text, Style style) {
        text.setStyle(style);
        return text;
    }

    public static MutableComponent copy(MutableComponent text) {
        //#if MC >= 11900
        return text.copy();
        //#elseif MC >= 11600
        //$$ return (MutableComponent)text.shallowCopy();
        //#else
        //$ return (MutableComponent) text.deepCopy();
        //#endif
    }
}
