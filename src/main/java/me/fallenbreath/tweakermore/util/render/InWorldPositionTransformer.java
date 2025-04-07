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

package me.fallenbreath.tweakermore.util.render;

import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Objects;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

public class InWorldPositionTransformer {
    private final Vec3 pos;
    private RenderContext renderContext;

    public InWorldPositionTransformer(Vec3 pos) {
        this.pos = pos;
        this.renderContext = null;
    }

    /**
     * Matrix stack of renderContext will be pushed
     */
    public void apply(RenderContext renderContext) {
        this.renderContext = renderContext;
        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        Vec3 vec3d = this.pos.subtract(camera.getPosition());

        renderContext.pushMatrix();
        renderContext.translate(vec3d.x, vec3d.y, vec3d.z);
        //#if MC >= 11500
        renderContext.multMatrix(
                //#if MC >= 11903
                new Matrix4f().rotation(camera.rotation())
                //#else
                //$$ new Matrix4f(camera.rotation())
                //#endif
        );
        //#else
        //$$ EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderManager();
        //$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
        //$$ GlStateManager.rotatef(entityRenderDispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
        //#endif
    }

    /**
     * Matrix stack of renderContext will be popped
     */
    public void restore() {
        Objects.requireNonNull(this.renderContext);
        this.renderContext.popMatrix();
        this.renderContext = null;
    }
}
