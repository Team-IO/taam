package founderio.taam.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntitySensor extends BaseTileEntity {
	
	private float offLength = 1.5f;
	private float offLeft = 1.5f;
	private float offRight = 1.5f;
	private int blind = 1;
	private float down = 2.5f;
	
	private boolean powering = false;
	
	public int renderingOffset = 0;
	
	public int isPowering() {
		if(powering) {
			return 15;
		} else {
			return 0;
		}
	}
	
	public TileEntitySensor() {
	}
	
	@Override
	public void updateEntity() {
		int meta = getBlockMetadata();
//		int type = meta & 8;
		int rotation = meta & 7;
		
		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		
		float xMin = xCoord + 0.5f;
		float yMin = yCoord + 0.5f;
		float zMin = zCoord + 0.5f;
		float xMax = xCoord + 0.5f;
		float yMax = yCoord + 0.5f;
		float zMax = zCoord + 0.5f;

		switch(dir) {
		case DOWN:
			yMax -= blind;
			yMin -= down;
			xMin -= offLength / 2;
			xMax += offLength / 2;
			zMin -= offLength / 2;
			zMax += offLength / 2;
			break;
		case UP:
			yMax += down;
			yMin += blind;
			xMin -= offLength / 2;
			xMax += offLength / 2;
			zMin -= offLength / 2;
			zMax += offLength / 2;
			break;
			//TODO: blind for the following ones...
		case NORTH:
			yMin -= down;
			zMin -= offLength;
			xMin -= offLeft;
			xMax += offRight;
			break;
		case SOUTH:
			yMin -= down;
			zMax += offLength;
			xMin -= offRight;
			xMax += offLeft;
			break;
		case WEST:
			yMin -= down;
			xMin -= offLength;
			zMax += offLeft;
			zMin -= offRight;
			break;
		case EAST:
			yMin -= down;
			xMax += offLength;
			zMax += offRight;
			zMin -= offLeft;
			break;
		case UNKNOWN:
			break;
		}
		
		AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(xMin, yMin, zMin, xMax, yMax, zMax);
		
		//System.out.println(bb);
		
		boolean found = false;
		for(Object obj : worldObj.loadedEntityList) {
			Entity ent = (Entity)obj;
			
			if(ent.boundingBox.intersectsWith(bb)) {
				found = true;
				break;
			}
		}
		if(found != powering) {
			powering = found;
			//worldObj.setB
			((TaamSensorBlock)getBlockType()).updateBlocksAround(worldObj, xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound) {
		//TODO: Write properties
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound) {
		//TODO: Read properties
	}

}
