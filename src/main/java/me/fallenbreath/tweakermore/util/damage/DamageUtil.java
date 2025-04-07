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

package me.fallenbreath.tweakermore.util.damage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class DamageUtil {

    public static float modifyDamageForDifficulty(float amount, Difficulty difficulty) {
        switch (difficulty) {
            case PEACEFUL:
                amount = 0.0F;
                break;
            case EASY:
                amount = Math.min(amount / 2.0F + 1.0F, amount);
                break;
            case HARD:
                amount = amount * 3.0F / 2.0F;
                break;
        }
        return amount;
    }

    public static float modifyDamageForDifficulty(float amount, Difficulty difficulty, DamageSource damageSource) {
        if (damageSource.scalesWithDifficulty()) {
            amount = modifyDamageForDifficulty(amount, difficulty);
        }
        return amount;
    }

    public static float getDamageLeft(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
        float i;
        label:
        {
            float f = 2.0F + armorToughness / 4.0F;

            float g = Mth.clamp(armor - damageAmount / f, armor * 0.2F, 20.0F);
            float h = g / 25.0F;
            ItemStack itemStack = damageSource.getWeaponItem();
            if (itemStack != null) {
                Level world = armorWearer.level();
                if (world instanceof ServerLevel serverWorld) {
                    i = Mth.clamp(EnchantmentHelper.getDamageProtection(serverWorld, armorWearer, damageSource), 0.0F, 1.0F);
                    break label;
                }
            }

            i = h;
        }

        float j = 1.0F - i;
        return damageAmount * j;
    }

    public static float getInflictedDamage(float damageDealt, float protection) {
        float f = Mth.clamp(protection, 0.0F, 20.0F);
        return damageDealt * (1.0F - f / 25.0F);
    }
}
