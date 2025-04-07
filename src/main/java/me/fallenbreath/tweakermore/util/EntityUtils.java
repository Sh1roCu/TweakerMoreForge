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

import me.fallenbreath.tweakermore.util.compat.tweakeroo.TweakerooAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class EntityUtils {
    public static Abilities getPlayerAbilities(Player player) {
        //#if MC >= 11700
        return player.getAbilities();
        //#else
        //$$ return player.abilities;
        //#endif
    }

    public static boolean isFlyingCreativePlayer(Entity entity) {
        if (entity instanceof Player player) {
            return player.isCreative() && getPlayerAbilities(player).flying;
        }
        return false;
    }

    public static final boolean TWEAKEROO_LOADED = PlatformUtils.isModLoaded(ModIds.tweakeroo);

    @Nullable
    public static LocalPlayer getCurrentPlayerOrFreeCameraEntity() {
        if (TWEAKEROO_LOADED) {
            LocalPlayer freecam = TweakerooAccess.getFreecamEntity();
            if (freecam != null) {
                return freecam;
            }
        }
        return Minecraft.getInstance().player;
    }
}
