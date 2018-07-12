package hohserg.advancedauromancy.hooklib.example;

import hohserg.advancedauromancy.hooklib.minecraft.HookLoader;
import hohserg.advancedauromancy.hooklib.minecraft.PrimaryClassTransformer;

public class ExampleHookLoader extends HookLoader {

    public String[] getASMTransformerClass() {
        return new String[]{PrimaryClassTransformer.class.getName()};
    }


    public void registerHooks() {
        registerHookContainer("hohserg.advancedauromancy.hooks.ChargeHooks");
    }
}
