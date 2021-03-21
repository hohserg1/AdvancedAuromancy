package hohserg.advancedauromancy.utils.endothermic.example.java;

import com.sun.management.ThreadMXBean;
import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class SliceExample implements Function<BakedQuad, BakedQuad> {

    @Override
    public BakedQuad apply(BakedQuad quad) {

        ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        List<BakedQuad> list = new ArrayList<>(1000);
        long threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId());
        long time = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            list.add(LazyUnpackedQuad.apply(quad)
                    .slice(
                            0, 0,
                            0.5f, 0,
                            0.5f, 0.5f,
                            0, 0.5f)
                    .toBakedQuad());
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
}