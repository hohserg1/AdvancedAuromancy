package hohserg.advancedauromancy.hooklib.minecraft;

import hohserg.advancedauromancy.hooklib.asm.AsmHook;
import hohserg.advancedauromancy.hooklib.asm.ClassMetadataReader;
import hohserg.advancedauromancy.hooklib.asm.HookClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public abstract class HookLoader implements IFMLLoadingPlugin {

    static DeobfuscationTransformer deobfuscationTransformer;

    private static ClassMetadataReader deobfuscationMetadataReader;

    static {
        if (HookLibPlugin.getObfuscated()) {
            deobfuscationTransformer = new DeobfuscationTransformer();
        }
        deobfuscationMetadataReader = new DeobfuscationMetadataReader();
    }

    public static HookClassTransformer getTransformer() {
        return PrimaryClassTransformer.instance.registeredSecondTransformer ?
                MinecraftClassTransformer.instance : PrimaryClassTransformer.instance;
    }

    public static void registerHook(AsmHook hook) {
        getTransformer().registerHook(hook);
    }


    public static void registerHookContainer(String className) {
        getTransformer().registerHookContainer(className);
    }

    public static ClassMetadataReader getDeobfuscationMetadataReader() {
        return deobfuscationMetadataReader;
    }

    // 1.6.x only
    public String[] getLibraryRequestClass() {
        return null;
    }

    // 1.7.x only
    public String getAccessTransformerClass() {
        return null;
    }


    public String[] getASMTransformerClass() {
        return null;
    }


    public String getModContainerClass() {
        return null;
    }


    public String getSetupClass() {
        return null;
    }


    public void injectData(Map<String, Object> data) {
        registerHooks();
    }

    protected abstract void registerHooks();
}
