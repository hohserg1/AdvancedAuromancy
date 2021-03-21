package hohserg.advancedauromancy.utils.endothermic.example.java;

import com.google.common.collect.ImmutableSet;
import com.sun.management.ThreadMXBean;
import hohserg.advancedauromancy.utils.endothermic.quad.mutable.LazyUnpackedQuad;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ColoredExample implements Function<BakedQuad, BakedQuad> {

    @Override
    public BakedQuad apply(BakedQuad quad) {
        ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        List<BakedQuad> list = new ArrayList<>(1000);
        long threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId());
        long time = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            list.add(LazyUnpackedQuad.apply(quad)
                    .reconstructBuilder()
                    .v1_r(255)//colored first vertex to red
                    .v2_g(255)//colored second vertex to green
                    .v3_b(255)//colored third vertex to blue
                    .build()
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