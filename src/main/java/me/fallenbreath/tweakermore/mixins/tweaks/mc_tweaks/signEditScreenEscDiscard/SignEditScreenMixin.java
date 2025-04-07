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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenEscDiscard;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signEditScreenCancelCommon.ClientSignTextRollbacker;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(
        //#if MC >= 11903
        AbstractSignEditScreen.class
        //#else
        //$$ SignEditScreen.class
        //#endif
)
public abstract class SignEditScreenMixin {
    //#if MC >= 11903
    @Shadow
    @Final
    protected SignBlockEntity sign;
    //#else
    //$$ @Shadow
    //$$ @Final
    //$$ private SignBlockEntity sign;
    //#endif

    @Unique
    private boolean isClosedByEsc = false;
    @Unique
    private ClientSignTextRollbacker clientSignTextRollbacker = null;

    @Inject(
            //#if MC >= 12001
            method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZLnet/minecraft/network/chat/Component;)V",
            //#elseif MC >= 11903
            //$$ method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZLnet/minecraft/text/Text;)V",
            //#else
            //$$ method = "<init>",
            //#endif
            at = @At("TAIL")
    )
    private void signEditScreenEscDiscard_memorizeSignTexts(CallbackInfo ci) {
        this.clientSignTextRollbacker = new ClientSignTextRollbacker(
                //#if MC >= 11903
                (AbstractSignEditScreen) (Object) this, this.sign
                //#else
                //$$ (SignEditScreen) (Object) this, this.sign
                //#endif
        );
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void signEditScreenEscDiscard_rememberDontSyncOnEsc(CallbackInfo ci) {
        this.isClosedByEsc = true;
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void signEditScreenEscDiscard_rollbackIfFinishEditingDoesNotGetInvoked(CallbackInfo ci) {
        if (TweakerMoreConfigs.SIGN_EDIT_SCREEN_ESC_DISCARD.getBooleanValue()) {
            if (this.isClosedByEsc && this.clientSignTextRollbacker != null) {
                this.clientSignTextRollbacker.rollback();
            }
        }
    }
}
