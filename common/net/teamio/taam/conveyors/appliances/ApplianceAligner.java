package net.teamio.taam.conveyors.appliances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.IAdvancedMachineGUI;
import net.teamio.taam.gui.advanced.apps.AlignerSettings;
import net.teamio.taam.gui.advanced.apps.RedstoneMode;
import net.teamio.taam.util.InventoryUtils;

import javax.annotation.Nonnull;

public class ApplianceAligner extends ATileEntityAppliance implements IWorldInteractable {


	/**
	 * Info which redirector-bits are down, used for rendering.
	 * These ItemWrappers will be set to null by the renderer once complete.
	 */
	@SideOnly(Side.CLIENT)
	public ItemWrapper[] clientRenderCache;

	@SideOnly(Side.CLIENT)
	public EnumFacing conveyorDirection;

	@SideOnly(Side.CLIENT)
	public byte conveyorSpeedsteps;

	public final ItemFilterCustomizable[] filters;

	public ApplianceAligner() {
		filters = new ItemFilterCustomizable[3];
		for(int i = 0; i < 3; i++) {
			filters[i] = new ItemFilterCustomizable(3);
		}
	}

	@Nonnull
	@Override
	public String getName() {
		return "tile.taam.productionline_appliance.aligner.name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderUpdate() {
		TileEntity te = world.getTileEntity(pos.offset(direction));
		if(te instanceof IConveyorApplianceHost) {
			IConveyorApplianceHost host = (IConveyorApplianceHost) te;
			IConveyorSlots slots = host.getSlots();
			conveyorDirection = slots.getMovementDirection();
			conveyorSpeedsteps = slots.getSpeedsteps();
		}
	}

	public final IAdvancedMachineGUI gui = new IAdvancedMachineGUI() {

		@Override
		public boolean hasCustomName() {
			return ApplianceAligner.this.hasCustomName();
		}

		@Nonnull
		@Override
		public String getName() {
			return ApplianceAligner.this.getName();
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return ApplianceAligner.this.getDisplayName();
		}

		@Override
		public void setup(ContainerAdvancedMachine container) {
			new RedstoneMode(container, null);
			new AlignerSettings(container, ApplianceAligner.this);
		}

		@Override
		public void markDirty() {
			ApplianceAligner.this.markDirty();
		}
	};

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
		if(capability == Taam.CAPABILITY_ADVANCED_GUI) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		if(capability == Taam.CAPABILITY_ADVANCED_GUI) {
			return (T) gui;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		super.writePropertiesToNBT(tag);
		for (int i = 0; i < filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = filters[i];
			NBTTagCompound filterTag = itemFilterCustomizable.serializeNBT();
			tag.setTag("filter" + i, filterTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		super.readPropertiesFromNBT(tag);
		for (int i = 0; i < filters.length; i++) {
			NBTTagCompound filterTag = tag.getCompoundTag("filter" + i);
			ItemFilterCustomizable itemFilterCustomizable = filters[i];
			itemFilterCustomizable.deserializeNBT(filterTag);
		}
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		player.openGui(TaamMain.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
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
		if(InventoryUtils.isEmpty(wrapper.itemStack)) {
			return beforeOverride;
		}

		EnumFacing direction = host.getSlots().getMovementDirection();

		// We can only align when it passes left/right
		if(direction.getAxis() == this.direction.getAxis()) {
			return beforeOverride;
		}

		int row = ConveyorUtil.ROWS.get(slot, direction);
		int lane = ConveyorUtil.LANES.get(slot, direction);

		// Only process the front-most row
		if(row != 1) {
			return beforeOverride;
		}

		EnumFacing left = direction.rotateYCCW();
		EnumFacing right = direction.rotateY();
		EnumFacing afterOverride = beforeOverride;

		ItemFilterCustomizable filterLane1;
		ItemFilterCustomizable filterLane2;
		ItemFilterCustomizable filterLane3;

		if(direction == this.direction.rotateY()) {
			filterLane1 = filters[0];
			filterLane2 = filters[1];
			filterLane3 = filters[2];
		} else {
			// To match the setup in the GUI, reverse the order of the filters
			filterLane1 = filters[2];
			filterLane2 = filters[1];
			filterLane3 = filters[0];
		}


		// Item can continue on lane if filter matches & is include or does not match & is exclude
		boolean canContinueLane1 = filterLane1.isItemStackMatching(wrapper.itemStack) != filterLane1.isExcluding();
		boolean canContinueLane2 = filterLane2.isItemStackMatching(wrapper.itemStack) != filterLane2.isExcluding();
		boolean canContinueLane3 = filterLane3.isItemStackMatching(wrapper.itemStack) != filterLane3.isExcluding();

		// FIXME Debug-Mode, move all to center
		if(lane == 1) {
			if(!canContinueLane1) {
				// If it can continue on one of the other lanes, move right. Else block.
				if(canContinueLane2 || canContinueLane3) {
					afterOverride = right;
				} else {
					wrapper.blockForce();
				}
			}
		} else if(lane == 3) {
			if(!canContinueLane3) {
				if(canContinueLane1 || canContinueLane2) {
					afterOverride = left;
				} else {
					wrapper.blockForce();
				}
			}
		} else {
			if(!canContinueLane2) {
				if(canContinueLane1) {
					afterOverride = left;
				} else if(canContinueLane3) {
					afterOverride = right;
				} else {
					wrapper.blockForce();
				}
			}
		}

		// On the client, update the rendering information
		if(world.isRemote) {
			if(clientRenderCache == null) {
				clientRenderCache = new ItemWrapper[4];
			}
			if(afterOverride == right) {
				if(lane == 1) {
					clientRenderCache[0] = wrapper;
				} else if(lane == 2) {
					clientRenderCache[2] = wrapper;
				}
			}
			if(afterOverride == left) {
				if(lane == 2) {
					clientRenderCache[1] = wrapper;
				} else if(lane == 3) {
					clientRenderCache[3] = wrapper;
				}
			}
		}


		return afterOverride;
	}

}
