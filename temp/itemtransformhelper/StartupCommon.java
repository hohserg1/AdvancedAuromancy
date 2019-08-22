package itemtransformhelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
  public static ItemCamera itemCamera;  // this holds the unique instance of your block
  public static CreativeTabs tabITH = new CreativeTabITH(CreativeTabs.getNextID(), "temp/itemtransformhelper");

  public static void preInitCommon()
  {
    // each instance of your item should have a _name that is unique within your mod.  use lower case.
    itemCamera = (ItemCamera)(new ItemCamera().setUnlocalizedName("item_camera"));
    itemCamera.setRegistryName("item_camera");
    ForgeRegistries.ITEMS.register(itemCamera);
  }

  public static void initCommon()
  {
  }

  public static void postInitCommon()
  {
  }
}
