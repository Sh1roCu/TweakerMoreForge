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

package me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.beacon;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC >= 12006
//$$ import net.minecraft.registry.entry.RegistryEntry;
//#endif

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("levels")
    int getLevel();

    //#if MC >= 12006
    @Accessor("primaryPower")
    Holder<MobEffect> getPrimary();
    //#else
    //$$ MobEffect getPrimary();
    //#endif

    @Nullable
    //#if MC >= 12006
    @Accessor("secondaryPower")
    Holder<MobEffect> getSecondary();
    //#else
    //MobEffect getSecondary();
    //#endif
}
