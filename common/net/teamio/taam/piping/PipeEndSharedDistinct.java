package net.teamio.taam.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * A pipe end that is intended to have the same undelying {@link PipeInfo} in
 * multiple instance, but maintain a different pressure on each instance.
 * 
 * Useful for machines like pumps, that are technically a pipe but have
 * different pressure on each end.
 * 
 * @author Oliver Kahrmann
 *
 */
public class PipeEndSharedDistinct extends PipeEnd {
	private int pressure;
	private int suction;

	public PipeEndSharedDistinct(EnumFacing side, PipeInfo info, boolean active) {
		super(side, info, active);
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("pressure", pressure);
		tag.setInteger("suction", suction);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		pressure = tag.getInteger("pressure");
		suction = tag.getInteger("suction");
	}

	/*
	 * IPipe implementation for distinct pressure/suction
	 */

	@Override
	public int getPressure() {
		return pressure;
	}

	@Override
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	@Override
	public int getSuction() {
		return suction;
	}

	@Override
	public void setSuction(int suction) {
		this.suction = suction;
	}
}
