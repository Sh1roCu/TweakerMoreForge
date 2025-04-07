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

package me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer;

import com.google.common.collect.Maps;
import me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.DataQueryHandlerAccessor;
import me.fallenbreath.tweakermore.util.collection.ExpiringMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class DataQueryHandlerPro {
    private final Map<Integer, Callback> callbacks = new ExpiringMap<>(Maps.newHashMap(), 30_000);
    private int idCounter = 0;

    private static Optional<ClientPacketListener> getNetworkHandler() {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        return clientPacketListener != null ? Optional.of(clientPacketListener) : Optional.empty();
    }

    public int generateTransactionId(ClientPacketListener networkHandler) {
        DataQueryHandlerAccessor accessor = (DataQueryHandlerAccessor) networkHandler.getDebugQueryHandler();
        int id = (accessor.getTransactionId() ^ 0x80000000) + this.idCounter;  // make sure it's far enough from vanilla's id
        this.idCounter = (this.idCounter + 1) % (1 << 20);  // 10M concurrency is enough
        return id;
    }

    public boolean handleQueryResponse(int transactionId, @Nullable CompoundTag tag) {
        Callback callback = this.callbacks.remove(transactionId);
        if (callback != null) {
            callback.accept(tag);
            return true;
        } else {
            return false;
        }
    }

    private int nextQuery(ClientPacketListener networkHandler, Callback callback) {
        int transactionId = this.generateTransactionId(networkHandler);
        this.callbacks.put(transactionId, callback);
        return transactionId;
    }

    public void clearCallbacks() {
        this.callbacks.clear();
    }

    public void queryEntityNbt(int entityNetworkId, Callback callback) {
        getNetworkHandler().ifPresent(networkHandler -> {
            int id = this.nextQuery(networkHandler, callback);
            networkHandler.send(new ServerboundEntityTagQueryPacket(id, entityNetworkId));
        });
    }

    public void queryBlockNbt(BlockPos pos, Callback callback) {
        getNetworkHandler().ifPresent(networkHandler -> {
            int id = this.nextQuery(networkHandler, callback);
            networkHandler.send(new ServerboundBlockEntityTagQueryPacket(id, pos));
        });
    }

    @FunctionalInterface
    public interface Callback {
        void accept(@Nullable CompoundTag nbt);
    }
}
