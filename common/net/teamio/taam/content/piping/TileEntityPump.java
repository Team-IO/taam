package net.teamio.taam.content.piping;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEndSharedDistinct;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityPump extends BaseTileEntity implements IPipeTE, ITickable, IRenderable, IRotatable {

	private final PipeEndSharedDistinct pipeEndOut;
	private final PipeEndSharedDistinct pipeEndIn;

	private EnumFacing direction = EnumFacing.NORTH;
	private final PipeInfo info;
	
	public static final List<String> visibleParts = Lists.newArrayList("Baseplate_pmdl", "Pump_pumdl");
	
	private static final int capacity = 125;
	private static final int pressure = 50;
	
	public TileEntityPump() {
		info = new PipeInfo(capacity);
		pipeEndOut = new PipeEndSharedDistinct(direction, info, true);
		pipeEndIn = new PipeEndSharedDistinct(direction.getOpposite(), info, true);
		pipeEndOut.setPressure(pressure);
		pipeEndIn.setSuction(pressure);
	}
	
	@Override
	public List<String> getVisibleParts() {
		return visibleParts;
	}
	
	@Override
	public void update() {
		PipeUtil.processPipes(pipeEndOut, worldObj, pos);
		PipeUtil.processPipes(pipeEndIn, worldObj, pos);
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		info.writeToNBT(tag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		info.readFromNBT(tag);
	}

	/*
	 * IPipeTE implementation
	 */
	
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		if (side == direction) {
			return pipeEndOut.asPipeArray();
		} else if (side == direction.getOpposite()) {
			return pipeEndIn.asPipeArray();
		} else {
			return null;
		}
	}

	/*
	 * IRotatable implementation
	 */
	
	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(direction.getAxis() == Axis.Y) {
			return;
		}
		this.direction = direction;
		
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		
		blockUpdate();
		updateState();
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
	}
}
