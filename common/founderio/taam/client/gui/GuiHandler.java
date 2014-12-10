package founderio.taam.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntityLogisticsStation;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityConveyorHopper) {
			return new ContainerConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
		}
		if (tileEntity instanceof TileEntityLogisticsStation) {
			return new ContainerLogisticsStation(player.inventory, (TileEntityLogisticsStation) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityConveyorHopper) {
			return new GuiConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
		}
		if (tileEntity instanceof TileEntityLogisticsStation) {
			return new GuiLogisticsStation(player.inventory, (TileEntityLogisticsStation) tileEntity);
		}
		return null;
	}

}
