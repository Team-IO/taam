package founderio.taam.rendering;

import org.lwjgl.opengl.GL11;

import founderio.taam.Taam;
import founderio.taam.entities.EntityLogisticsCart;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class TaamEntityRenderer extends RenderEntity {
	
	public final WavefrontObject modelLogisticsCart;
	public final ResourceLocation textureLogisticsCart;
	
	public TaamEntityRenderer() {

		modelLogisticsCart = new WavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/logistics_cart.obj"));
		textureLogisticsCart = new ResourceLocation(Taam.MOD_ID + ":textures/models/logistics_cart.png");
	}
	
	@Override
	public void doRender(Entity entity, double x,
			double y, double z, float r1, float r2) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		GL11.glRotatef(r1, 0, 1, 0);
		
		if(entity instanceof EntityLogisticsCart) {
			bindTexture(textureLogisticsCart);
			modelLogisticsCart.renderAll();
		}
		
		GL11.glPopMatrix();
	}
	
	
}
