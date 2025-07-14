package cn.sh1rocu.tweakermoreforge;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.thinkingstudio.mafglib.util.ForgePlatformUtils;

@OnlyIn(Dist.CLIENT)
@Mod(value = TweakerMoreForge.MODID)
public class TweakerMoreForge {
    public static final String MODID = "tweakermoreforge";
    public static final String MOD_NAME = "TweakerMoreForge";

    public TweakerMoreForge() {
        if (FMLLoader.getDist().isClient()) {
            ForgePlatformUtils.getInstance().registerModConfigScreen(MODID, (screen) -> {
                TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
                gui.setParent(screen);
                return gui;
            });
            TweakerMoreMod.onInitialize();
        }
    }
}