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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.litematica;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.AreaSelectionUtil;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.TargetPair;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(SchematicUtils.class)
public abstract class SchematicUtilsMixin {
    @ModifyExpressionValue(
            method = "saveSchematic",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/litematica/selection/SelectionManager;getCurrentSelection()Lfi/dy/masa/litematica/selection/AreaSelection;",
                    remap = false
            ),
            remap = false
    )
    private static AreaSelection serverDataSyncer4SchematicSave(AreaSelection area) {
        if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue()) {
            Minecraft mc = Minecraft.getInstance();
            if (area != null && !mc.hasSingleplayerServer() && ServerDataSyncer.hasEnoughPermission()) {
                Minecraft.getInstance().execute(() -> syncEverything(area));
            }
        }
        return area;
    }

    @Unique
    @Nullable
    private static String currentSyncingArenaName = null;

    @Unique
    private static final ThreadLocal<Boolean> dontInjectClone = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "cloneSelectionArea",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lfi/dy/masa/litematica/selection/SelectionManager;getCurrentSelection()Lfi/dy/masa/litematica/selection/AreaSelection;",
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            cancellable = true,
            remap = false
    )
    private static void serverDataSyncer4SelectionClone(Minecraft mc, CallbackInfo ci, @Local AreaSelection area) {
        if (dontInjectClone.get()) {
            return;
        }

        ci.cancel();

        String name = currentSyncingArenaName;
        if (name != null)  // prevent duplicated sync
        {
            showMessage$TKM(Message.MessageType.INFO, "clone_syncing", currentSyncingArenaName);
            return;
        }

        Minecraft.getInstance().execute(() -> {
            try {
                currentSyncingArenaName = area.getName();
                syncEverything(area);
                dontInjectClone.set(true);
                SchematicUtils.cloneSelectionArea(mc);
            } finally {
                currentSyncingArenaName = null;
                dontInjectClone.remove();
            }
        });
    }

    @Unique
    private static void syncEverything(AreaSelection area) {
        showMessage$TKM(Message.MessageType.INFO, "start", area.getName());

        TargetPair pair = AreaSelectionUtil.extractBlockEntitiesAndEntities(area, true);
        final int beTotal = pair.getBlockEntityAmount();
        final int eTotal = pair.getEntityAmount();

        AtomicLong lastUpdateTime = new AtomicLong(Util.getEpochMillis());
        CompletableFuture<Void> future = ServerDataSyncer.getInstance().syncEverything(pair, (be, e) -> {
            long currentTime = Util.getEpochMillis();
            if (currentTime - lastUpdateTime.get() > 500) {
                lastUpdateTime.set(currentTime);
                String percent = String.format("%.1f%%", 100.0D * (be + e) / (beTotal + eTotal));
                showMessage$TKM(Message.MessageType.INFO, "progress", be, beTotal, e, eTotal, percent);
            }
        });
        future.thenRun(() -> showMessage$TKM(Message.MessageType.SUCCESS, "synced", beTotal, eTotal));
    }

    @Unique
    private static void showMessage$TKM(Message.MessageType type, String textName, Object... args) {
        InfoUtils.showGuiOrInGameMessage(type, 3000, "tweakermore.impl.serverDataSyncer.schematic_sync." + textName, args);
    }
}
