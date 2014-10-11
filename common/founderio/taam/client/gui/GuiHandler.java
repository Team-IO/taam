package founderio.taam.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import founderio.taam.blocks.TileEntityConveyorHopper;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityConveyorHopper) {
			return new ContainerConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityConveyorHopper) {
			return new GuiConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
		}
		return null;
	}

}
