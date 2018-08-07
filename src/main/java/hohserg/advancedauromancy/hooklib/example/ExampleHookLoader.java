package hohserg.advancedauromancy.hooklib.example;

import hohserg.advancedauromancy.hooklib.minecraft.HookLoader;
import hohserg.advancedauromancy.hooklib.minecraft.PrimaryClassTransformer;

public class ExampleHookLoader extends HookLoader {

    // включает саму HookLib'у. Делать это можно только в одном из HookLoader'ов.
    // При желании, можно включить gloomyfolken.hooklib.minecraft.HookLibPlugin и не указывать здесь это вовсе.
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{PrimaryClassTransformer.class.getName()};
    }

    @Override
    public void registerHooks() {
        registerHookContainer("hohserg.advancedauromancy.hooks.ChargeHooks");
    }
}
