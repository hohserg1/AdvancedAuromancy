package hohserg.advancedauromancy.utils.endothermic.example.java;

import com.google.common.collect.Lists;
import hohserg.advancedauromancy.utils.endothermic.quad.BaseUnpackedQuad;
import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public class MutableImmutableExample {

    public static List<BakedQuad> immutableVersion(BakedQuad quad) {
        LazyUnpackedQuad q1 = LazyUnpackedQuad.apply(quad);
        BaseUnpackedQuad translated = q1.translate(0.1f, 0, 1);
        BaseUnpackedQuad rotated = q1.rotate((float) Math.PI, 0, 1, 0);
        //q1 != translated; q1 != rotated ; translated != rotated
        return Lists.newArrayList(translated.toBakedQuad(), rotated.toBakedQuad());
    }

    public static List<BakedQuad> mutableVersion(BakedQuad quad) {
        hohserg.advancedauromancy.utils.endothermic.quad.mutable.LazyUnpackedQuad q1 = hohserg.advancedauromancy.utils.endothermic.quad.mutable.LazyUnpackedQuad.apply(quad);

        BaseUnpackedQuad translated = q1.copy().translate(0.1f, 0, 1);//copy for save original version
        // q1 != translated

        BaseUnpackedQuad rotated = q1.rotate((float) Math.PI, 0, 1, 0);//no copy, no new instance creating
        // q1 == rotated

        return Lists.newArrayList(translated.toBakedQuad(), rotated.toBakedQuad());
    }
}
