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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignTextLengthLimit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
//#endif

//#if MC >= 11700
//$$ import net.minecraft.client.render.VertexConsumer;
//$$ import net.minecraft.client.util.SpriteIdentifier;
//#endif

/**
 * The implementation for mc [1.15.2, 1.20)
 * See subproject 1.14.4 or 1.20 for implementation for other version range
 */
@Restriction(conflict = @Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2"))
@Mixin(
        //#if MC >= 11903
        AbstractSignEditScreen.class
        //#else
        //$$ SignEditScreen.class
        //#endif
)
public abstract class SignEditScreenMixin extends Screen {
    //#if MC < 11600
    //$$ @Shadow
    //$$ private SelectionManager selectionManager;
    //#endif

    @Shadow
    @Final
    protected SignBlockEntity
            //#if MC >= 11903
            sign;
    //#else
    //$$ sign;
    //#endif

    //#if MC >= 11600
    @Shadow
    private SignText text;
    //#endif

    @Shadow
    @Final
    private String[] messages;
    //#if MC >= 11700
    @Unique
    private boolean filtered$TKM;
    //#endif

    protected SignEditScreenMixin(Component title) {
        super(title);
    }

    //#if MC >= 11700
    @Inject(
            //#if MC >= 12000
            method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZLnet/minecraft/network/chat/Component;)V",
            //#elseif MC >= 11903
            //$$ method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZLnet/minecraft/text/Text;)V",
            //#else
            //$$ method = "<init>",
            //#endif
            at = @At("TAIL")
    )
    private void recordFilteredParam(
            SignBlockEntity sign,
            //#if MC >= 12000
            boolean front,
            //#endif
            boolean filtered,
            //#if MC >= 11903
            Component title,
            //#endif
            CallbackInfo ci
    ) {
        this.filtered$TKM = filtered;
    }
    //#endif

    //#if MC >= 11903
    @ModifyExpressionValue(
            method = "lambda$init$4",  // lambda method in init
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;getMaxTextLineWidth()I",
                    remap = true
            ),
            remap = false
    )
    //#elseif MC >= 11600
    //$$ @ModifyConstant(
    //$$ 		method = "method_27611",  // lambda method in init
    //$$ 		constant = @Constant(intValue = 90),
    //$$ 		remap = false,
    //$$ 		require = 0
    //$$ )
    //#else
    //$$ @ModifyArg(
    //$$       method = "init",
    //$$       at = @At(
    //$$              value = "INVOKE",
    //$$             target = "Lnet/minecraft/client/util/SelectionManager;<init>(Lnet/minecraft/client/Minecraft;Ljava/util/function/Supplier;Ljava/util/function/Consumer;I)V"
    //$$    )
    //$$  )
    //#endif
    private int disableSignTextLengthLimitInSignEditor(int maxLength) {
        if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue()) {
            maxLength = Integer.MAX_VALUE;
        }
        return maxLength;
    }

    //#if MC < 11600
    //$$ @ModifyArg(
    //$$        method = "method_23773",  // lambda method in method render
    //$$       at = @At(
    //$$              value = "INVOKE",
    //$$               target = "Lnet/minecraft/client/util/Texts;wrapLines(Lnet/minecraft/text/Text;ILnet/minecraft/client/font/TextRenderer;ZZ)Ljava/util/List;",
    //$$               remap = true
    //$$       ),
    //$$       remap = false
    //$$  )
    //$$ private int disableSignTextLengthLimitInSignEditScreenRendering(int maxLength) {
    //$$    if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue()) {
    //$$        // should be modified into Integer.MAX_VALUE too in the @ModifyArg above
    //$$        maxLength = ((SelectionManagerAccessor) this.selectionManager).getMaxLength();
    //$$     }
    //$$    return maxLength;
    //$$  }
    //#endif  // if MC < 11600

    @Inject(
            method = "renderSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void drawLineOverflowHint(
            GuiGraphics context, CallbackInfo ci,
            @Local(ordinal = 5) int lineIdx,
            @Local(ordinal = 6) int xStart
    ) {
        if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue()) {
            int textArrayLen = this.messages.length;
            Minecraft mc = this.minecraft;
            if (mc != null && 0 <= lineIdx && lineIdx < textArrayLen) {
                Component text = this.text.getMessage(lineIdx, this.filtered$TKM);
                int maxWidth = this.sign.getMaxTextLineWidth();
                List<?> wrapped = mc.font.split(text, maxWidth);
                boolean overflowed = wrapped.size() > 1;

                if (overflowed) {
                    assert ChatFormatting.RED.getColor() != null;
                    int lineHeight = this.sign.getTextLineHeight();
                    int x = xStart - 10;
                    int y = lineIdx * lineHeight - (4 * lineHeight / 2);
                    context.drawString(this.font, "!", x, y, ChatFormatting.RED.getColor(), false);
                }
            }
        }
    }
}
