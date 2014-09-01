package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityConveyor extends BaseTileEntity implements IInventory {
	
	private List<ItemStack> items;
	
	private float offLength = 1.5f;
	private float offLeft = 1.5f;
	private float offRight = 1.5f;
	private int blind = 1;
	private float down = 2.5f;
	
	public int renderingOffset = 0;
	
	private int tickOn = 0;
	
	public TileEntityConveyor() {
		items = new ArrayList<ItemStack>();
	}
	
	@Override
	public void updateEntity() {
		int meta = getBlockMetadata();
//		int type = meta & 8;
		int rotation = meta & 7;
		
		
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound) {
		par1nbtTagCompound.setInteger("tickOn", tickOn);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound) {
		tickOn = par1nbtTagCompound.getInteger("tickOn");
	}

	@Override
	public int getSizeInventory() {
		// One more slot than already used so we can always accept items? Maybe not....
		return items.size() + 1;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
