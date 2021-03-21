package hohserg.advancedauromancy.utils.endothermic.example.scala

import java.lang.management.ManagementFactory
import java.util

import com.sun.management.ThreadMXBean
import hohserg.advancedauromancy.utils.endothermic.quad.immutable.LazyUnpackedQuad
import net.minecraft.client.renderer.block.model.BakedQuad

class ColoredExample extends (BakedQuad => BakedQuad) {
  override def apply(v1: BakedQuad): BakedQuad = {

    val threadMXBean: ThreadMXBean = ManagementFactory.getThreadMXBean.asInstanceOf[ThreadMXBean]
    val list = new util.ArrayList[BakedQuad](1000)
    var threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId)
    var time = System.nanoTime()
    for (i <- 0 until 1000) {
      list.add(
        LazyUnpackedQuad(v1)
        .reconstruct(
          v1_r = 255, //colored first vertex to red
          v2_g = 255, //colored second vertex to green
          v3_b = 255 //colored third vertex to blue
        )
        .toBakedQuad
      )
    }
    time = System.nanoTime() - time
    threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(Thread.currentThread().getId()) - threadAllocatedBytes
    System.out.println("Alocated bytes for 1000 iterations: " + threadAllocatedBytes)
    System.out.println("Time spend for 1000 iterations: " + time + " ns")
    System.out.println("Alocated avarage bytes for 1 iterations: " + threadAllocatedBytes / 1000)//2958
    System.out.println("Time avarage spend for 1 iterations: " + time / 1000 + " ns\n\n")//11832


    list.get(0)
  }
}