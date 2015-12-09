package net.teamio.taam.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID) {
		default:
		case 0://TileEntity
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileEntityConveyorHopper) {
				return new ContainerConveyorSmallInventory(player.inventory, (TileEntityConveyorHopper) tileEntity);
			}
			
			if (tileEntity instanceof IInventory) {
				return new ContainerConveyorSmallInventory(player.inventory, (IInventory) tileEntity);
			}
			break;
		case 1://Entity
			//Entity entity = world.getEntityByID(x);
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		default:
		case 0://TileEntity
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileEntityConveyorHopper) {
				return new GuiConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
			}
			
			if (tileEntity instanceof IInventory) {
				return new GuiConveyorSmallInventory(player.inventory, (IInventory) tileEntity);
			}
			break;
		case 1://Entity
			//Entity entity = world.getEntityByID(x);
			break;
		}
		return null;
	}

}
