package net.teamio.taam.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.logistics.EntityLogisticsCart;
import net.teamio.taam.content.logistics.TileEntityLogisticsStation;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID) {
		default:
		case 0://TileEntity
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityConveyorHopper) {
				return new ContainerConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
			}
			if (tileEntity instanceof TileEntityLogisticsStation) {
				return new ContainerLogisticsStation(player.inventory, (TileEntityLogisticsStation) tileEntity);
			}
			break;
		case 1://Entity
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityLogisticsCart) {
				return new ContainerLogisticsCart(player.inventory, (EntityLogisticsCart) entity);
			}
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
		default:
		case 0://TileEntity
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityConveyorHopper) {
				return new GuiConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
			}
			if (tileEntity instanceof TileEntityLogisticsStation) {
				return new GuiLogisticsStation(player.inventory, (TileEntityLogisticsStation) tileEntity);
			}
			break;
		case 1://Entity
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityLogisticsCart) {
				return new GuiLogisticsCart(player.inventory, (EntityLogisticsCart) entity);
			}
			break;
		}
		return null;
	}

}