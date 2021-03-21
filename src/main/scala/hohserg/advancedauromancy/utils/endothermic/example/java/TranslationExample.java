package hohserg.advancedauromancy.utils.endothermic.example.java;

import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.function.Function;

public class TranslationExample implements Function<BakedQuad, BakedQuad> {

    @Override
    public BakedQuad apply(BakedQuad quad) {
        return LazyUnpackedQuad.apply(quad)
                .translate(1, 0, 0)
                .toBakedQuad();
    }
}
