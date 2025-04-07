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

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Collection;

public class PlatformUtils {
    public static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modId::equals);
        } else {
            return ModList.get().isLoaded(modId);
        }
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    public static boolean doesModFitsAnyPredicate(String modId, Collection<String> versionPredicates) {
        if (!isModLoaded(modId))
            return false;
        if (ModList.get() == null) {
            ArtifactVersion version = modId.equals(ModIds.minecraft) ? new DefaultArtifactVersion(FMLLoader.versionInfo().mcVersion()) : LoadingModList.get().getModFileById(modId).getFile().getJarVersion();
            return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(version, vp));
        }
        return ModList.get().getModContainerById(modId).
                map(mod -> {
                    ArtifactVersion version = modId.equals(ModIds.minecraft) ? new DefaultArtifactVersion(FMLLoader.versionInfo().mcVersion()) : mod.getModInfo().getVersion();
                    return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(version, vp));
                }).
                orElse(false);
    }

    public static boolean doesVersionSatisfyPredicate(ArtifactVersion version, String versionPredicate) {
        try {
            if (versionPredicate.contains("=")) {
                if (versionPredicate.contains("<")) {
                    String[] target = versionPredicate.split("=")[1].split("\\.");
                    if (version.getMajorVersion() > Integer.parseInt(target[0]))
                        return false;
                    if (version.getMinorVersion() > Integer.parseInt(target[1]))
                        return false;
                    return target.length == 2 || version.getIncrementalVersion() <= Integer.parseInt(target[2]);
                }
                if (versionPredicate.contains(">")) {
                    String[] target = versionPredicate.split("=")[1].split("\\.");
                    if (version.getMajorVersion() < Integer.parseInt(target[0]))
                        return false;
                    if (version.getMinorVersion() < Integer.parseInt(target[1]))
                        return false;
                    return target.length == 2 || version.getIncrementalVersion() >= Integer.parseInt(target[2]);
                }
                // just =
                String[] target = versionPredicate.replace("=", "").split("\\.");
                return version.getMajorVersion() == Integer.parseInt(target[0]) && version.getMinorVersion() == Integer.parseInt(target[1])
                        && (target.length == 2 || version.getIncrementalVersion() == Integer.parseInt(target[2]));
            }
            if (versionPredicate.contains("<")) {
                String[] target = versionPredicate.replace("<", "").split("\\.");
                if (version.getMajorVersion() > Integer.parseInt(target[0]))
                    return false;
                if (version.getMinorVersion() > Integer.parseInt(target[1]))
                    return false;
                return target.length == 2 || version.getIncrementalVersion() < Integer.parseInt(target[2]);
            }
            if (versionPredicate.contains(">")) {
                String[] target = versionPredicate.replace(">", "").split("\\.");
                if (version.getMajorVersion() < Integer.parseInt(target[0]))
                    return false;
                if (version.getMinorVersion() < Integer.parseInt(target[1]))
                    return false;
                return target.length == 2 || version.getIncrementalVersion() > Integer.parseInt(target[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
