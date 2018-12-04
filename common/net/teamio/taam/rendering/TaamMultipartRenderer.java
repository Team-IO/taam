package net.teamio.taam.rendering;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.machines.MachineMultipart;
import org.lwjgl.opengl.GL11;

public class TaamMultipartRenderer extends MultipartSpecialRenderer<MachineMultipart> {

	final TaamRenderer renderer;

	public TaamMultipartRenderer(TaamRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void renderMultipartAt(MachineMultipart part, double x, double y, double z, float partialTicks, int destroyStage) {

		if (Config.render_tank_content) {
			TankRenderInfo[] tankRI = part.getCapability(Taam.CAPABILITY_RENDER_TANK, null);

			if (tankRI != null) {
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);

				float rotationDegrees = RenderUtil.getRotationDegrees(part);

				GL11.glTranslated(.5f, .5f, .5f);
				GL11.glRotatef(rotationDegrees, 0, 1, 0);
				GL11.glTranslated(-.5f, -.5f, -.5f);

				for (TankRenderInfo renderInfo : tankRI) {
					renderer.renderTankContent(renderInfo.fluid, renderInfo.capacity, renderInfo.bounds);
				}
				GL11.glPopMatrix();
			}
		}
	}
}
