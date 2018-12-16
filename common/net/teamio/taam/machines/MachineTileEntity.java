package net.teamio.taam.machines;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;

public class MachineTileEntity extends BaseTileEntity implements ITickable, IMachineWrapper {

	public IMachine machine;
	public IMachineMetaInfo meta;

	public MachineTileEntity() {
	}

	public static Taam.MACHINE_META getInfo(int meta) {
		Taam.MACHINE_META[] values = Taam.MACHINE_META.values();
		int ordinal = MathHelper.clamp(meta, 0, values.length - 1);
		return values[ordinal];
	}

	public static EnumFacing getDirection(EntityLivingBase player, BlockPos pos) {
		EnumFacing placeDir = EnumFacing.NORTH;

		// TODO: Determination of special placement
		//if (defaultPlacement) ...

		// We hit top/bottom of a block
		double xDist = player.posX - pos.getX();
		double zDist = player.posZ - pos.getZ();
		if (Math.abs(xDist) > Math.abs(zDist)) {
			if (xDist < 0) {
				placeDir = EnumFacing.EAST;
			} else {
				placeDir = EnumFacing.WEST;
			}
		} else {
			if (zDist < 0) {
				placeDir = EnumFacing.SOUTH;
			} else {
				placeDir = EnumFacing.NORTH;
			}
		}
		return placeDir;
	}

	@Override
	public void markAsDirty() {
		markDirty();
	}

	@Override
	public void onLoad() {
		if (machine == null) {
			this.meta = getInfo(getBlockMetadata());

			Log.info("MachineTileEntity at {} creating machine instance {}.", getPos(), meta.unlocalizedName());
			this.machine = meta.createMachine(this);
			markDirty();
		}
		machine.onCreated(world, pos);
	}


	@Override
	public void onChunkUnload() {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", getPos());
			return;
		}
		machine.onUnload(world, pos);
	}

	@Override
	public String getName() {
		return "tile.taam.machine." + meta.getName() + ".name";
	}

	@Override
	public void update() {
		if (machine == null) {
			// DO NOT LOG, this will definitely lead to log spamming.
			return;
		}
		if (machine.update(world, pos)) {
			markDirty();
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if (meta != null) {
			tag.setString("machine", meta.unlocalizedName());
		}
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return;
		}
		machine.writePropertiesToNBT(tag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if (meta != null && this.meta != meta) {
			this.meta = meta;
			machine = meta.createMachine(this);
			updateState(false, true, false);
		}
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return;
		}
		machine.readPropertiesFromNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return false;
		}
		return machine.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return null;
		}
		return machine.getCapability(capability, facing);
	}

	@Override
	public void sendPacket() {
		updateState(true, false, false);
		//TODO: send update packet
	}
}
