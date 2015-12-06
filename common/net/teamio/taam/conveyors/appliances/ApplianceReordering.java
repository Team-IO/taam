package net.teamio.taam.conveyors.appliances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ApplianceInventory;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceFactory;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public class ApplianceReordering extends ApplianceInventory implements IWorldInteractable {

	public static final byte TYPE_ALIGNER = 0;
	public static final byte TYPE_DISTRIBUTOR = 1;
	
	private byte type;
	
	public ApplianceReordering(byte type) {
		super(0, 0);
		this.type = type;
	}
	
	public static class Factory implements IConveyorApplianceFactory {

		@Override
		public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor) {
			IConveyorAppliance ainv = new ApplianceSprayer();
			return ainv; 
		}
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(TaamMain.itemConveyorAppliance, 1, type + 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("type", type);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		type = tag.getByte("type");
	}
	
	@Override
	public boolean canProcessItem(ItemWrapper wrapper) {
		return true;
	}

	@Override
	public void processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper) {
		//TODO: unimplemented.
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return false;
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	protected int getTankForSide(ForgeDirection from) {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, boolean hasWrench, int side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onBlockHit(World world, int x, int y, int z,
			EntityPlayer player, boolean hasWrench) {
		return false;
	}

}
