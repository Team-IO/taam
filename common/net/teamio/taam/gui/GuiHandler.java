package net.teamio.taam.gui;

import mezz.jei.util.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;
import net.teamio.taam.gui.advanced.IAdvancedMachineGUI;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		default:
		case 0:// TileEntity
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof BaseTileEntity) {
				return new ContainerConveyorSmallInventory(player.inventory, tileEntity, EnumFacing.UP);
			}
			break;
		case 1:// Entity
				// Entity entity = world.getEntityByID(x);
			break;
		case 2:// Advanced Machine GUI
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te == null) {
				Log.error("Failed to open advanced machine GUI on {}. No TileEntity at specified coordinates.", new BlockPos(x, y, z));
				return null;
			}
			IAdvancedMachineGUI advancedMachine = te.getCapability(Taam.CAPABILITY_ADVANCED_GUI, null);
			if(advancedMachine == null) {
				Log.error("Failed to open advanced machine GUI on {}. TileEntity {} at specified coordinates does not support IAdvancedMachineGUI.", new BlockPos(x, y, z), te);
				return null;
			}
			return new ContainerAdvancedMachine(player, advancedMachine);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		default:
		case 0:// TileEntity
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileEntityConveyorHopper) {
				return new GuiConveyorHopper(player.inventory, (TileEntityConveyorHopper) tileEntity);
			}

			if (tileEntity instanceof BaseTileEntity) {
				return new GuiConveyorSmallInventory<BaseTileEntity>(player.inventory, (BaseTileEntity) tileEntity, EnumFacing.UP);
			}
			break;
		case 1:// Entity
				// Entity entity = world.getEntityByID(x);
			break;
		case 2:// Advanced Machine GUI
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te == null) {
				Log.error("Failed to open advanced machine GUI on {}. No TileEntity at specified coordinates.", new BlockPos(x, y, z));
				return null;
			}
			IAdvancedMachineGUI advancedMachine = te.getCapability(Taam.CAPABILITY_ADVANCED_GUI, null);
			if(advancedMachine == null) {
				Log.error("Failed to open advanced machine GUI on {}. TileEntity {} at specified coordinates does not support IAdvancedMachineGUI.", new BlockPos(x, y, z), te);
				return null;
			}
			return new GuiAdvancedMachine(new ContainerAdvancedMachine(player, advancedMachine));
		}
		return null;
	}

}
