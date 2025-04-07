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

package me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.phys.Vec3;

public class PreciseItemEntityModelUtils {
    public static final ThreadLocal<Boolean> transformOverrideFlag = ThreadLocal.withInitial(() -> false);

    public static ItemTransform modifyTransformation(ItemTransform transformation) {
        if (transformOverrideFlag.get()) {
            float len = 0.25f;  // size of the item entity
            transformation = new ItemTransform(
                    //#if MC >= 12105
                    //$$ transformation.rotation(),
                    //#else
                    transformation.rotation,
                    //#endif
                    new Vec3(0, len / 2.0f, 0).toVector3f(),
                    new Vec3(len, len, len).toVector3f()
            );
        }
        return transformation;
    }
}
