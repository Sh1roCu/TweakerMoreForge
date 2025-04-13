/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.infoView.cache;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderVisitorWorldView implements SimpleWorldView {
    private final SimpleCachedWorldView bestWorldView;
    private final SimpleCachedWorldView clientWorldView;
    private final LongOpenHashSet syncBlockEntityPositions = new LongOpenHashSet();

    public RenderVisitorWorldView(SimpleCachedWorldView bestWorld, SimpleCachedWorldView clientWorld) {
        this.bestWorldView = bestWorld;
        this.clientWorldView = clientWorld;
    }

    public Level getBestWorld() {
        return this.bestWorldView.getLevel();
    }

    public Level getClientWorld() {
        return this.clientWorldView.getLevel();
    }

    public boolean isBestWorldServerWorld() {
        return this.getBestWorld() instanceof ServerLevel;
    }

    public void syncBlockEntity(BlockPos pos) {
        if (this.syncBlockEntityPositions.add(pos.asLong())) {
            this.doSyncBlockEntity(pos);
        }
    }

    private void doSyncBlockEntity(BlockPos pos) {
        // serverDataSyncer do your job here
    }

    // ======================== Vanilla BlockView Interface ========================

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        return this.clientWorldView.getBlockState(pos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        return this.clientWorldView.getFluidState(pos);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        return this.bestWorldView.getBlockEntity(pos);
    }

    @Override
    public int getHeight() {
        return this.clientWorldView.getHeight();
    }

    //#if MC >= 11700
    @Override
    public int getMinY() {
        return this.clientWorldView.getMinY();
    }
    //#endif

    // ======================== SimpleWorldView Light Getters ========================

    @Override
    public int getLightLevel(BlockPos pos) {
        return this.clientWorldView.getLightLevel(pos);
    }

    @Override
    public int getLightLevel(LightLayer type, BlockPos pos) {
        return this.clientWorldView.getLightLevel(type, pos);
    }

    @Override
    public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return this.clientWorldView.getBaseLightLevel(pos, ambientDarkness);
    }
}
