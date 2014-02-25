package founderio.taam.blocks;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEmitter extends BaseTileEntity {

	private String sound = "default";
	private int count = 0;
	private int timeout = 600;
	
	@Override
	public void updateEntity() {
		count++;
		if(count >= timeout) {
			count = 0;
			
			worldObj.playSound(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, sound, 1f, 1f, true);
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound) {
		// TODO Auto-generated method stub

	}

}
