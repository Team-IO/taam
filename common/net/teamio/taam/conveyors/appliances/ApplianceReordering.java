package net.teamio.taam.conveyors.appliances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public class ApplianceReordering extends ATileEntityAppliance implements IWorldInteractable {

	public static final byte TYPE_ALIGNER = 0;
	public static final byte TYPE_DISTRIBUTOR = 1;
	
	private byte type;
	
	public ApplianceReordering(byte type) {
		this.type = type;
	}
	
	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * IConveyorAppliance implementation
	 */

	@Override
	public boolean processItem(IConveyorApplianceHost host, int slot, ItemWrapper wrapper) {
		// TODO Auto-generated method stub
		return false;
	}

}
