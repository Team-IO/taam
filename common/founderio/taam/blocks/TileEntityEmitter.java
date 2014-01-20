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
			
			//TODO: Play Sound here
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
