package hohserg.advancedauromancy.entities

import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.init.SoundEvents
import net.minecraft.network.datasync.{DataSerializers, EntityDataManager}
import net.minecraft.util.math.RayTraceResult.Type
import net.minecraft.util.math.{MathHelper, RayTraceResult, Vec3d}
import thaumcraft.api.casters.{FocusEngine, FocusPackage, Trajectory}
import thaumcraft.common.entities.projectile.EntityFocusProjectile
import thaumcraft.common.lib.events.ServerEvents

class EntityOrb(pack: FocusPackage, speed: Float, trajectory: Trajectory, special: Int) extends EntityThrowable(pack.world) {
  this.setPosition(trajectory.source.x + trajectory.direction.x * pack.getCaster.width.toDouble * 2.1D, trajectory.source.y + trajectory.direction.y * pack.getCaster.width.toDouble * 2.1D, trajectory.source.z + trajectory.direction.z * pack.getCaster.width.toDouble * 2.1D)
  this.shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, speed, 0.0F)
  this.setSize(0.15F, 0.15F)
  this.setSpecial(special)
  this.ignoreEntity = pack.getCaster
  this.setOwner(this.getThrower.getEntityId)

  import EntityOrb._

  override def entityInit(): Unit = {
    super.entityInit()
    this.getDataManager.register[Integer](SPECIAL, 0)
    this.getDataManager.register[Integer](OWNER, 0)
  }

  override def onUpdate(): Unit = {
    super.onUpdate()
  }

  def setSpecial(s: Int): Unit = {
    this.getDataManager.set[Integer](SPECIAL, s)
  }
  def getSpecial: Int = {
    this.getDataManager.get(SPECIAL)
  }

  def setOwner(s: Int): Unit = {
    this.getDataManager.set[Integer](OWNER, s)
  }

  override def onImpact(result: RayTraceResult): Unit = {
    if (result != null) {
      if (getSpecial == 1 && (result.typeOfHit eq Type.BLOCK)) {
        val bs = this.world.getBlockState(result.getBlockPos)
        val bb = bs.getCollisionBoundingBox(this.world, result.getBlockPos)
        if (bb == null) return
        this.posX -= this.motionX
        this.posY -= this.motionY
        this.posZ -= this.motionZ
        if (result.sideHit.getFrontOffsetZ != 0) this.motionZ *= -1.0D
        if (result.sideHit.getFrontOffsetX != 0) this.motionX *= -1.0D
        if (result.sideHit.getFrontOffsetY != 0) this.motionY *= -0.9D
        this.motionX *= 0.9D
        this.motionY *= 0.9D
        this.motionZ *= 0.9D
        val var20 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ)
        this.posX -= this.motionX / var20.toDouble * 0.05000000074505806D
        this.posY -= this.motionY / var20.toDouble * 0.05000000074505806D
        this.posZ -= this.motionZ / var20.toDouble * 0.05000000074505806D
        if (!this.world.isRemote) this.playSound(SoundEvents.ENTITY_LEASHKNOT_PLACE, 0.25F, 1.0F)
        if (!this.world.isRemote && new Vec3d(this.motionX, this.motionY, this.motionZ).lengthVector < 0.2D) this.setDead()
        return
      }
      if (!this.world.isRemote) {
        if (result.entityHit != null) result.hitVec = this.getPositionVector
        val pv = new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ)
        val vf = new Vec3d(this.motionX, this.motionY, this.motionZ)
        ServerEvents.addRunnableServer(this.getEntityWorld, new Runnable {
          override def run(): Unit = {
            FocusEngine.runFocusPackage(pack, Array(new Trajectory(pv, vf.normalize)), Array(result))
          }
        }, 0)
        this.setDead()
      }
    }
    
  }
}

object EntityOrb {
  private val SPECIAL = EntityDataManager.createKey(classOf[EntityFocusProjectile], DataSerializers.VARINT)
  private val OWNER = EntityDataManager.createKey(classOf[EntityFocusProjectile], DataSerializers.VARINT)

}
