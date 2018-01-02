package net.teamio.taam.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.Config;

/**
 * A pipe end that is intended to have the same undelying {@link PipeInfo} in
 * multiple instance, but maintain a different pressure on each instance.
 * <p>
 * Useful for machines like pumps, that are technically a pipe but have
 * different pressure on each end.
 *
 * @author Oliver Kahrmann
 */
public class PipeEndSharedDistinct extends PipeEnd {
	private int pressure;

	public PipeEndSharedDistinct(IPipePos pos, EnumFacing side, PipeInfo info) {
		super(pos, side, info);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("pressure", pressure);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		pressure = tag.getInteger("pressure");
	}

	/*
	 * IPipe implementation for distinct pressure
	 */

	@Override
	public int getPressure() {
		return pressure;
	}

	public int applyPressure(int pressure) {

		// TODO: make this a parameter. If we make it a parameter, config options need the world restart flag!
		int absMaxPressure = Config.pl_pipe_max_pressure * 100;

		//TODO: unit test this (compare to PipeInfo)

		if(pressure == 0) {
			return 0;
		} else if(pressure > 0) {
			int capa = absMaxPressure - this.pressure;
			if(capa > 0) {
				capa = Math.min(capa, pressure);
				this.pressure += capa;
				return capa;
			} else {
				return 0;
			}
		} else {
			int capa = absMaxPressure + this.pressure;
			if(capa > 0) {
				capa = Math.min(capa, -pressure);
				this.pressure -= capa;
				return -capa;
			} else {
				return 0;
			}
		}
	}

	@Override
	public boolean isNeutral() {
		return false;
	}
}
