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

package me.fallenbreath.tweakermore.util.mixin;

import com.google.common.collect.ImmutableList;
import cpw.mods.cl.JarModuleFinder;
import cpw.mods.jarhandling.SecureJar;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.module.Configuration;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

public class TweakerMoreMixinConfigPlugin extends RestrictiveMixinConfigPlugin {
    private final Logger LOGGER = LogManager.getLogger();
    // from CustomSkinLoader
    private final static MethodHandles.Lookup IMPL_LOOKUP = ((Supplier<MethodHandles.Lookup>) () -> {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            Object theUnsafe = theUnsafeField.get(null);

            Method getObjectMethod = unsafeClass.getMethod("getObject", Object.class, long.class);
            Method staticFieldBaseMethod = unsafeClass.getMethod("staticFieldBase", Field.class);
            Method staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", Field.class);

            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            return (MethodHandles.Lookup) getObjectMethod.invoke(theUnsafe, staticFieldBaseMethod.invoke(theUnsafe, implLookupField), staticFieldOffsetMethod.invoke(theUnsafe, implLookupField));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }).get();

    static {
        try {
            fixMixinModifyArgs();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    //from CustomSkinLoader
    // Dynamically creates and configures a fake module to ensure the package (org.spongepowered.asm.synthetic.args) is correctly loaded.
    @SuppressWarnings("unchecked")
    private static void fixMixinModifyArgs() throws Throwable {
        ClassLoader loader = TweakerMoreMixinConfigPlugin.class.getClassLoader();
        Map<String, MethodHandle> handles = findPackageLookup(loader.getClass());
        if (handles != null) {
            Map<String, Object> packageToOurModules = (Map<String, Object>) handles.get("packageLookup").invokeWithArguments(loader);
            String packageName = "org.spongepowered.asm.synthetic.args";
            if (packageToOurModules.get(packageName) == null) {
                Path moduleRoot = Paths.get("./TweakerMoreForge/FakeModule");
                Path classPath = Files.createDirectories(moduleRoot.resolve(packageName.replace('.', '/')));
                Path classFile = classPath.resolve("package-info.class");
                if (!Files.exists(classFile)) {
                    Files.createFile(classFile);
                }

                Configuration config = Configuration.resolve(JarModuleFinder.of(SecureJar.from(moduleRoot)), ImmutableList.of(ModuleLayer.boot().configuration()), JarModuleFinder.of(), ImmutableList.of("FakeModule"));
                ResolvedModule module = config.findModule("FakeModule").orElse(null);
                packageToOurModules.put(packageName, module);

                Object configuration = handles.get("configuration").invokeWithArguments(loader);
                Map<String, ResolvedModule> nameToModule = new HashMap<>((Map<String, ResolvedModule>) handles.get("nameToModuleGetter").invokeWithArguments(configuration));
                nameToModule.put("FakeModule", module);
                handles.get("nameToModuleSetter").invokeWithArguments(configuration, nameToModule);

                MethodHandle resolvedRootsGetter = handles.get("resolvedRoots");
                if (resolvedRootsGetter != null) {
                    ((Map<String, ModuleReference>) resolvedRootsGetter.invokeWithArguments(loader)).put("FakeModule", module.reference());
                }
            }
        }
    }

    //from CustomSkinLoader
    private static Map<String, MethodHandle> findPackageLookup(Class<?> cl) throws Throwable {
        if (!ClassLoader.class.equals(cl)) {
            Map<String, MethodHandle> map = new HashMap<>();
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) type;
                    if (Map.class.equals(paramType.getRawType())) {
                        Class<?> classJarModuleReference = findClass("cpw.mods.cl.JarModuleFinder$JarModuleReference");
                        if (Arrays.equals(new Class<?>[]{String.class, ResolvedModule.class}, paramType.getActualTypeArguments())) {
                            map.put("packageLookup", IMPL_LOOKUP.findGetter(cl, field.getName(), field.getType())); // Forge & NeoForge
                        } else if (Arrays.equals(new Class<?>[]{String.class, classJarModuleReference}, paramType.getActualTypeArguments())) {
                            map.put("resolvedRoots", IMPL_LOOKUP.findGetter(cl, field.getName(), field.getType())); // NeoForge
                        }
                    }
                } else if (Configuration.class.equals(field.getType())) {
                    map.put("configuration", IMPL_LOOKUP.findGetter(cl, field.getName(), field.getType())); // NeoForge
                    map.put("nameToModuleGetter", IMPL_LOOKUP.findGetter(field.getType(), "nameToModule", Map.class)); // NeoForge
                    map.put("nameToModuleSetter", IMPL_LOOKUP.findSetter(field.getType(), "nameToModule", Map.class)); // NeoForge
                }
            }
            if (!map.isEmpty()) {
                return map;
            }
            return findPackageLookup(cl.getSuperclass());
        }
        return null;
    }

    //from CustomSkinLoader
    private static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    protected void onRestrictionCheckFailed(String mixinClassName, String reason) {
        LOGGER.debug("[TweakerMore] Disabled mixin {} due to {}", mixinClassName, reason);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        // Fabric ASM (mm) direct MixinConfigPlugin getMixins() ---mm:early_risers--> optiFabric OptifabricSetup
        // We need to wait until optifine loading stuffs to be done to add our optifine mixins
        if (PlatformUtils.isModLoaded(ModIds.optifine))  // a rough check
        {
            LOGGER.info("[TweakerMore] loading optifine mixin");
            Mixins.addConfiguration("tweakermore.optifine_mixins.json");
        }
        return null;
    }
}
