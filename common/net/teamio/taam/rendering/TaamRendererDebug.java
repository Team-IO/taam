package net.teamio.taam.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.piping.IPipe;
import org.lwjgl.opengl.GL11;

/**
 * Created by oliver on 2017-12-31.
 */
@SideOnly(Side.CLIENT)
public final class TaamRendererDebug {
	private TaamRendererDebug() {
		// Util Class
	}

	public static void renderTEDebug(TileEntity te, EntityPlayer player, EnumFacing hitSide, float partialTicks) {
		if (player == null || te == null) {
			return;
		}

		boolean sneaking = player.isSneaking();
		if (sneaking) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				attemptPipeRender(te, player, facing, partialTicks);
			}
		} else {
			/*EnumFacing side;
			if (player.rotationPitch < -45) {
				side = EnumFacing.DOWN;
			} else if (player.rotationPitch > 45) {
				side = EnumFacing.UP;
			} else {
				side = player.getAdjustedHorizontalFacing().getOpposite();
			}*/
			attemptPipeRender(te, player, hitSide, partialTicks);
		}
	}

	public static void attemptPipeRender(TileEntity tileEntity, EntityPlayer player, EnumFacing facing, float partialTicks) {
		//TODO: flag to toggle between viewspace and world space rendering

		if (tileEntity == null || !tileEntity.hasCapability(Taam.CAPABILITY_PIPE, facing)) {
			return;
		}
		BlockPos pos = tileEntity.getPos();
		IPipe pipe = tileEntity.getCapability(Taam.CAPABILITY_PIPE, facing);
		if(pipe == null) {
			// Render null/error info?
			return;
		}

		TaamRenderer.drawSelectionBoundingBox(player, partialTicks, 8, 0, 1, 0, 1,
				new AxisAlignedBB(pos.offset(facing)));

		FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;

		int fillLevel = 0;

		for (FluidStack fluid : pipe.getFluids()) {
			if (fluid != null) {
				fillLevel += fluid.amount;
			}
		}

		String info0 = String.format("Capacity: %03d/%d", fillLevel, pipe.getCapacity());
		String info1 = String.format("Pressure: %d", pipe.getPressure());
		//String info2 = "";

		GL11.glPushMatrix();
		{
			// if(viewspace) {
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			GL11.glTranslated(-d0, -d1, -d2);
			//}

			//pos = pos.offset(facing);
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());

			//GL11.glTranslated(.5f, .5f, .5f);
			GL11.glTranslated(.5f + facing.getFrontOffsetX(), .5f + facing.getFrontOffsetY(), .5f + facing.getFrontOffsetZ());

			double playerRot = Math.floor((player.getRotationYawHead() + 45) / 90f) * 90;
			double pitch = Math.floor((player.rotationPitch + 45) / 90f) * 90;


			GL11.glRotatef(180, 0, 0, 1);
			GL11.glRotated(playerRot, 0, 1, 0);
			GL11.glRotated(-pitch, 1, 0, 0);
			GL11.glTranslated(-.5f, -.5f, -.2f);

			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			{
				GL11.glTranslated(0.25f, 0.25f, 0.15f);

				GL11.glScalef(.02f, .02f, .02f);

				fontRendererObj.drawString(info0, -8, 0, 0x00FFFF);
				fontRendererObj.drawString(info1, -8, 8, 0xFFFFFF);
				//fontRendererObj.drawString(info2, -8, 16, 0xFFFF00);
			}
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}
}
