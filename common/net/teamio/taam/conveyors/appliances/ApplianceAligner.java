package net.teamio.taam.conveyors.appliances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.ItemWrapper;

public class ApplianceAligner extends ATileEntityAppliance implements IWorldInteractable {


	/**
	 * Info which redirector-bits are down, used for rendering.
	 * These ItemWrappers will be set to null by the renderer once complete.
	 */
	@SideOnly(Side.CLIENT)
	public final ItemWrapper[] down = new ItemWrapper[4];
	
	@SideOnly(Side.CLIENT)
	public EnumFacing conveyorDirection = EnumFacing.NORTH;
	
	@SideOnly(Side.CLIENT)
	public byte conveyorSpeedsteps = 10;
	
	public ApplianceAligner() {
	}
	
	@Override
	public String getName() {
		return "tile.taam.productionline_appliance.aligner.name";
	}
	
	@Override
	public void renderUpdate() {
		TileEntity te = worldObj.getTileEntity(pos.offset(direction));
		if(te instanceof IConveyorApplianceHost) {
			IConveyorApplianceHost host = (IConveyorApplianceHost) te;
			IConveyorSlots slots = host.getSlots();
			conveyorDirection = slots.getMovementDirection();
			conveyorSpeedsteps = slots.getSpeedsteps();
		}
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		// TODO Open inventory
		return false;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
	
	/*
	 * IConveyorAppliance implementation
	 */

	@Override
	public boolean processItem(IConveyorApplianceHost host, int slot, ItemWrapper wrapper) {
		// Nothing to do, this appliance only reroutes items.
		return false;
	}

	@Override
	public EnumFacing overrideNextSlot(IConveyorApplianceHost host, int slot, ItemWrapper wrapper,
			EnumFacing beforeOverride) {
		EnumFacing direction = host.getSlots().getMovementDirection();
		
		// We can only align when it passes left/right
		if(direction.getAxis() == this.direction.getAxis()) {
			return beforeOverride;
		}

		int row = ConveyorUtil.ROWS.get(slot, direction);
		int lane = ConveyorUtil.LANES.get(slot, direction);
		Log.debug("SLOT: {} ROW: {} LANE: {}", slot, row, lane);
		// Only process the front-most row
		if(row != 1) {
			return beforeOverride;
		}

		EnumFacing left = direction.rotateYCCW();
		EnumFacing right = direction.rotateY();
		EnumFacing afterOverride = beforeOverride;
		
		// FIXME Debug-Mode, move all to center
		if(lane == 1) {
			afterOverride = right;
		} else if(lane == 3) {
			afterOverride = left;
		} else {
//			afterOverride = right;
		}
		
		// On the client, update the rendering information
		if(worldObj.isRemote) {
			if(afterOverride == right) {
				if(lane == 1) {
					down[0] = wrapper;
				} else if(lane == 2) {
					down[2] = wrapper;
				}
			}
			if(afterOverride == left) {
				if(lane == 2) {
					down[1] = wrapper;
				} else if(lane == 3) {
					down[3] = wrapper;
				}
			}
		}
			
		
		return afterOverride;
	}

}
