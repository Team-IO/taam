package founderio.taam.content.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.Config;
import founderio.taam.content.BaseBlock;
import founderio.taam.content.BaseTileEntity;
import founderio.taam.conveyors.api.IRotatable;

public class TileEntitySensor extends BaseTileEntity implements IRotatable {
	
	private float offLength = 1.5f;
	private float offLeft = 1.5f;
	private float offRight = 1.5f;
	private int blind = 1;
	private float down = 2.5f;
	
	private boolean powering = false;
	
	public int renderingOffset = 0;
	
	private int tickOn = 0;
	
	public int isPowering() {
		if(powering) {
			return 15;
		} else {
			return 0;
		}
	}
	
	//TODO: move rotation from metadata to tileentity property.
	
	public TileEntitySensor() {
	}

	@Override
	public ForgeDirection getFacingDirection() {
		int meta = getBlockMetadata();
		int rotation = meta & 7;
		return ForgeDirection.getOrientation(rotation);
	}

	@Override
	public ForgeDirection getMountDirection() {
		return getFacingDirection().getOpposite();
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
		
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);

		boolean found = false;
		
		if(tickOn > 0) {
			tickOn--;
			found = true;
		} else {
			for(Object obj : worldObj.loadedEntityList) {
				Entity ent = (Entity)obj;
				
				if(ent instanceof EntityLivingBase && ent.boundingBox.intersectsWith(bb)) {
					found = true;
					break;
				}
			}
			if(found) {
				tickOn = Config.sensor_delay;
			}
		}
		
		if(found != powering) {
			powering = found;
			BaseBlock.updateBlocksAround(worldObj, xCoord, yCoord, zCoord);
		}
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
	public ForgeDirection getNextFacingDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		// TODO Auto-generated method stub
		
	}

}
