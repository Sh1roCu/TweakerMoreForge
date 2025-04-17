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

package me.fallenbreath.tweakermore;

import cn.sh1rocu.tweakermoreforge.TweakerMoreForge;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.MalilibStuffsInitializer;
import me.fallenbreath.tweakermore.util.AutoMixinAuditExecutor;
import me.fallenbreath.tweakermore.util.bootstrap.TweakerMorePreLaunchEntrypoint;
import net.neoforged.fml.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TweakerMoreMod {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_NAME = "TweakerMore";
    public static final String MOD_ID = "tweakermore";
    public static String VERSION = StringUtils.getModVersionString(TweakerMoreForge.MODID);

    public static void onInitialize(ModContainer modContainer) {
        MalilibStuffsInitializer.init();
        AutoMixinAuditExecutor.run();
        TweakerMorePreLaunchEntrypoint.onPreLaunch();
    }
}
