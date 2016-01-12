package net.teamio.taam.content.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;

public class TileEntitySensor extends BaseTileEntity implements IRotatable, IUpdatePlayerListBox {
	
	private float offLength = 1.5f;
	private float offLeft = 1.5f;
	private float offRight = 1.5f;
	private int blind = 1;
	private float down = 2.5f;
	
	private boolean powering = false;
	
	public int renderingOffset = 0;
	
	private int tickOn = 0;
	
	private EnumFacing direction = EnumFacing.UP;
	
	public int getRedstoneLevel() {
		if(powering) {
			return 15;
		} else {
			return 0;
		}
	}
	
	public TileEntitySensor() {
	}
	
	public TileEntitySensor(EnumFacing rotation) {
		this.direction = rotation;
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
		
	}
	
	private void setBlockMeta() {
		// Set block metadata according to rotation
		int meta = getBlockMetadata();
		EnumFacing dir = EnumFacing.getOrientation(meta);
		if(dir != direction) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, direction.ordinal(), 3);
			markDirty();
		}
	}

	@Override
	public void updateContainingBlockInfo() {
		setBlockMeta();
	}
	
	@Override
	public void update() {
		int meta = getBlockMetadata();
//		int type = meta & 8;
		int rotation = meta & 7;
		
		EnumFacing dir = EnumFacing.getOrientation(rotation);
		
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
				
				if(ent instanceof EntityLivingBase && ent.boundingBox.intersectsWith(bb) && ent instanceof EntityIronGolem == false  && ent instanceof EntitySnowman == false) {
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
	public EnumFacing getNextFacingDirection() {
		for(EnumFacing nextDir = direction.getRotation(EnumFacing.UNKNOWN); nextDir != direction; nextDir = nextDir.getRotation(EnumFacing.UNKNOWN)) {
			if(TaamMain.blockSensor.canPlaceBlockOnSide(worldObj, xCoord, yCoord, zCoord, nextDir.ordinal())) {
				return nextDir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		setBlockMeta();
	}
}
