package net.teamio.taam.content.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;

public class TileEntitySensor extends BaseTileEntity implements IRotatable, ITickable {

	private final float offLength = 1.5f;
	private final float offLeft = 1.5f;
	private final float offRight = 1.5f;
	private final int blind = 1;
	private final float down = 2.5f;

	private boolean powering = false;

	public int renderingOffset = 0;

	private int tickOn = 0;

	private EnumFacing direction = EnumFacing.UP;

	public int getRedstoneLevel() {
		return powering ? 15 : 0;
	}

	public TileEntitySensor() {
	}

	public TileEntitySensor(EnumFacing rotation) {
		direction = rotation;
	}

	@Override
	public String getName() {
		return "tile.taam.sensor.name";
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;

	}

	private void setBlockMeta() {
		// Set block metadata according to rotation
		IBlockState blockState = world.getBlockState(pos);
		EnumFacing dir = blockState.getValue(BlockSensor.DIRECTION);
		if(dir != direction) {
			world.setBlockState(pos, blockState.withProperty(BlockSensor.DIRECTION, direction));
			markDirty();
		}
	}

	@Override
	public void updateContainingBlockInfo() {
		setBlockMeta();
	}

	@Override
	public void update() {

		float xMin = pos.getX() + 0.5f;
		float yMin = pos.getY() + 0.5f;
		float zMin = pos.getZ() + 0.5f;
		float xMax = pos.getX() + 0.5f;
		float yMax = pos.getY() + 0.5f;
		float zMax = pos.getZ() + 0.5f;

		switch(direction) {
		default:
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
		}

		AxisAlignedBB bb = new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax);

		boolean found = false;

		if(tickOn > 0) {
			tickOn--;
			found = true;
		} else {
			for(Object obj : world.loadedEntityList) {
				Entity ent = (Entity)obj;

				if(isDetectedEntityType(ent) && isEntityWithinBoundingBox(bb, ent)) {
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
			BaseBlock.updateBlocksAround(world, pos);
		}
	}

	private static boolean isDetectedEntityType(Entity ent) {
		return ent instanceof EntityLivingBase
				&& !(ent instanceof EntityIronGolem)
				&& !(ent instanceof EntitySnowman);
	}

	private static boolean isEntityWithinBoundingBox(AxisAlignedBB bb, Entity ent) {
		AxisAlignedBB entityBounds = ent.getEntityBoundingBox();
		if(entityBounds == null) {
			return bb.contains(ent.getPositionVector());
		}
		return entityBounds.intersects(bb);
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
		for(EnumFacing nextDir = EnumFacing.byIndex(direction.ordinal() + 1); nextDir != direction; nextDir = EnumFacing.byIndex(nextDir.ordinal() + 1)) {
			if(TaamMain.blockSensor.canPlaceBlockOnSide(world, pos, nextDir)) {
				return nextDir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		setBlockMeta();
		blockUpdate();
	}
}
