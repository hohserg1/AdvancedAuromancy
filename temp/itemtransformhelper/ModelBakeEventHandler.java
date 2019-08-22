package itemtransformhelper;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

/**
 * User: The Grey Ghost
 * Date: 20/01/2015
 * We use the ModelBakeEvent to iterate through all the registered models, wrap each one in an ItemModelFlexibleCamera, and write it
 *   back into the registry.
 * Each wrapped model gets a reference to ModelBakeEventHandler::itemOverrideLink.
 * Later, we can alter the members of itemOverrideLink to change the ItemCameraTransforms for a desired Item
 */
@SuppressWarnings("deprecation")
public class ModelBakeEventHandler
{
  public ModelBakeEventHandler()
  {
    itemOverrideLink.forcedTransform = new ItemCameraTransforms(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
                                                                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
                                                                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
                                                                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
  }

  @SubscribeEvent
  public void modelBakeEvent(ModelBakeEvent event)
  {
    IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
    if (!(modelRegistry instanceof RegistrySimple)) {
      System.err.println("ModelBakeEventHandler::modelBakeEvent expected modelRegistry to be RegistrySimple, was actually:"+modelRegistry);
      return;
    }
    RegistrySimple<ModelResourceLocation, IBakedModel> modelSimpleRegistry = (RegistrySimple)modelRegistry;

    for (ModelResourceLocation modelKey : modelSimpleRegistry.getKeys()) {
      IBakedModel iBakedModel = event.getModelRegistry().getObject(modelKey);
      ItemModelFlexibleCamera wrappedModel = ItemModelFlexibleCamera.getWrappedModel(iBakedModel, itemOverrideLink);
      event.getModelRegistry().putObject(modelKey, wrappedModel);
    }
    FMLLog.log("ItemTransformHelper", Level.INFO, "Warning - The Item Transform Helper replaces your IBakedModels with a wrapped version, this");
    FMLLog.log("ItemTransformHelper", Level.INFO, "  is done even when the helper is not in your hotbar, and might cause problems if your");
    FMLLog.log("ItemTransformHelper", Level.INFO, "  IBakedModel implements an interface ItemTransformHelper doesn't know about.");
    FMLLog.log("ItemTransformHelper", Level.INFO, "  I recommend you disable the mod when you're not actively using it to transform your items.");
  }

  public ItemModelFlexibleCamera.UpdateLink getItemOverrideLink() {
    return itemOverrideLink;
  }

  private ItemModelFlexibleCamera.UpdateLink itemOverrideLink = new ItemModelFlexibleCamera.UpdateLink();
}
