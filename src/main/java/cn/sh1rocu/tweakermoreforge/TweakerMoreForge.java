package cn.sh1rocu.tweakermoreforge;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.compat.modmenu.ModMenuApiImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.thinkingstudio.mafglib.loader.FoxifiedLoader;

@Mod(value = TweakerMoreForge.MODID, dist = Dist.CLIENT)
public class TweakerMoreForge {
    public static final String MODID = "tweakermoreforge";
    public static final String MOD_NAME = "TweakerMoreForge";

    public TweakerMoreForge(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLLoader.getDist().isClient()) {
            FoxifiedLoader.registerExtensionPoint(modContainer, IConfigScreenFactory.class, new ModMenuApiImpl().getModConfigScreenFactory());
            TweakerMoreMod.onInitialize(modContainer);
        }
    }
}