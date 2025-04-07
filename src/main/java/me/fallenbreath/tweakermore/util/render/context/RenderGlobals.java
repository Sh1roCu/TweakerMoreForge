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

package me.fallenbreath.tweakermore.util.render.context;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

//#if 11600 <= MC && MC < 11700
//$$ @SuppressWarnings("deprecation")
//#endif
public class RenderGlobals {
    private RenderGlobals() {
    }

    public static void enableDepthTest() {
        RenderSystem.enableDepthTest();
    }

    public static void disableDepthTest() {
        RenderSystem.disableDepthTest();
    }

    //#if MC < 11904
    //$$ public static void enableTexture()
    //$$ {
    //$$ 	RenderSystem.enableTexture();
    //$$ }
    //#endif

    //#if MC < 11700
    //$$ public static void enableAlphaTest()
    //$$ {
    //$$ 	RenderSystem.enableAlphaTest();
    //$$ }
    //#endif

    public static void depthMask(boolean mask) {
        RenderSystem.depthMask(mask);
    }

    public static void color4f(float red, float green, float blue, float alpha) {
        //#if MC >= 11700
        RenderSystem.setShaderColor(red, green, blue, alpha);
        //#else
        //$$ RenderSystem.color4f(red, green, blue, alpha);
        //#endif
    }

    public static void enableBlend() {
        RenderSystem.enableBlend();
    }

    public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor) {
        RenderSystem.blendFunc(srcFactor, dstFactor);
    }

    public static void blendFuncForAlpha() {
        blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    //#if MC < 11700
    //$$ public static void disableLighting() {
    //$$     RenderSystem.disableLighting();
    //$$ }
    //#endif
}
