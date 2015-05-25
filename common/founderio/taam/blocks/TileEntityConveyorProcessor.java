package founderio.taam.blocks;

import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.TaamMain;
import founderio.taam.conveyors.api.IConveyorAwareTE;
import founderio.taam.conveyors.api.IItemFilter;
import founderio.taam.conveyors.api.IRedstoneControlled;
import founderio.taam.multinet.logistics.WorldCoord;
import founderio.taam.network.TPMachineConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityConveyorProcessor extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IHopper, IRedstoneControlled {

	public static final byte Shredder = 0;
	public static final byte Grinder = 1;
	public static final byte Crusher = 2;

	private InventorySimple inventory;
	private byte mode;

	private byte redstoneMode;
	
	private byte progress;
	

	public TileEntityConveyorProcessor() {
		this(Shredder);
	}
	
	public TileEntityConveyorProcessor(byte mode) {
		inventory = new InventorySimple(1, getInventoryName());
		this.mode = mode;
	}
	
	@Override
	public void updateEntity() {
		// TODO Auto-generated method stub
		super.updateEntity();
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		tag.setByte("mode", mode);
		tag.setByte("redstoneMode", redstoneMode);
		tag.setByte("progress", progress);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
		mode = tag.getByte("mode");
		redstoneMode = tag.getByte("redstoneMode");
		progress = tag.getByte("progress");
	}

	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		switch(mode) {
		case Shredder:
			return "tile.taam.productionline.shredder.name";
		case Grinder:
			return "tile.taam.productionline.grinder.name";
		case Crusher:
			return "tile.taam.productionline.crusher.name";
		default:
			return "tile.taam.productionline.invalid.name";
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
		// Nothig to do
	}

	@Override
	public void closeInventory() {
		// Nothig to do
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		//TODO: Check recipes
		return true;
	}

	/*
	 * IConveyorAwareTE
	 */
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public int getMaxMovementProgress() {
		return 1;
	}

	@Override
	public IItemFilter getSlotFilter(int slot) {
		return null;
	}

	@Override
	public int posX() {
		return xCoord;
	}

	@Override
	public int posY() {
		return yCoord;
	}

	@Override
	public int posZ() {
		return zCoord;
	}

	@Override
	public int insertItemAt(ItemStack item, int slot) {
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		return inserted;
	}

	@Override
	public ItemStack getItemAt(int slot) {
		return null;
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side == ForgeDirection.UP.ordinal()) {
			return new int[] { 0 };
		} else {
			return new int[0];
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if(side == ForgeDirection.UP.ordinal()) {
			//TODO: Check recipes
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}
	
	/*
	 * IHopper implementation
	 */
	
	@Override
	public double getXPos() {
		return this.xCoord;
	}

	@Override
	public double getYPos() {
		return this.yCoord;
	}

	@Override
	public double getZPos() {
		return this.zCoord;
	}
	

	/*
	 * IRedstoneControlled implementation
	 */

	@Override
	public boolean isPulsingSupported() {
		return false;
	}

	@Override
	public byte getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(byte mode) {
		this.redstoneMode = mode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			this.markDirty();
		}
	}

}
