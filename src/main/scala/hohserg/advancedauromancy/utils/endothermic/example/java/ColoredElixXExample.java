package hohserg.advancedauromancy.utils.endothermic.example.java;/*package hohserg.advancedauromancy.utils.endothermic.example.java;

import com.sun.management.ThreadMXBean;
import net.minecraft.client.renderer.block.model.BakedQuad;
import ru.mousecray.endmagic.util.render.elix_x.baked.UnpackedBakedQuad;
import ru.mousecray.endmagic.util.render.elix_x.ecomms.color.RGBA;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class ColoredElixXExample implements Function<BakedQuad, BakedQuad> {

    public RGBA red = new RGBA(255, 0, 0, 0);
    public RGBA green = new RGBA(0, 255, 0, 0);
    public RGBA blue = new RGBA(0, 0, 255, 0);

    @Override
    public BakedQuad apply(BakedQuad quad) {

        ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        List<BakedQuad> list = new ArrayList<>(1000);
        long threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId());
        long time = System.nanoTime();
        for (int i1 = 0; i1 < 1000; i1++) {
            UnpackedBakedQuad unpacked = UnpackedBakedQuad.unpack(quad);
            unpacked.getVertices().getVertices().get(0).setColor(red);
            unpacked.getVertices().getVertices().get(1).setColor(green);
            unpacked.getVertices().getVertices().get(2).setColor(blue);
            list.add(unpacked.pack());
        }
        time = System.nanoTime() - time;
        threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId()) -
                threadAllocatedBytes;
        System.out.println("Alocated bytes for 1000 iterations: " + threadAllocatedBytes);
        System.out.println("Time spend for 1000 iterations: " + time + " ns");
        System.out.println("Alocated avarage bytes for 1 iterations: " + threadAllocatedBytes / 1000);//2968
        System.out.println("Time avarage spend for 1 iterations: " + time / 1000 + " ns\n\n");
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}*/