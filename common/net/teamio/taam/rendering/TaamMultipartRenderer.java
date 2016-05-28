package net.teamio.taam.rendering;

import org.lwjgl.opengl.GL11;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.teamio.taam.Taam;
import net.teamio.taam.machines.MachineMultipart;

public class TaamMultipartRenderer extends MultipartSpecialRenderer<MachineMultipart> {

	TaamRenderer renderer;

	public TaamMultipartRenderer(TaamRenderer renderer) {
		this.renderer = renderer;
	}

	public void renderMultipartAt(MachineMultipart part, double x, double y, double z, float partialTicks, int destroyStage) {

		TankRenderInfo[] tankRI = part.getCapability(Taam.CAPABILITY_RENDER_TANK, null);

		if (tankRI != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = TaamRenderer.getRotationDegrees(part);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			for (TankRenderInfo renderInfo : tankRI) {
				renderer.renderTankContent(renderInfo.tankInfo.fluid, renderInfo.tankInfo.capacity, renderInfo.bounds);
			}
			GL11.glPopMatrix();
		}
	};
}
