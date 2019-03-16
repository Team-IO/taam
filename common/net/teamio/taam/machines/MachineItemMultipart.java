package net.teamio.taam.machines;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;

import java.util.Collections;
import java.util.List;

public class MachineItemMultipart extends ItemMultiPart {

	public MachineItemMultipart(IMachineMetaInfo[] values) {
		super();
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		setHasSubtypes(true);
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = MachineTileEntity.getInfo(meta);

		// TODO: distinguish between IMachine and IMachineWithSpecialRenderer later

		MachineMultipart multipart = new MachineMultipart(info);

		if (multipart.machine instanceof IRotatable) {

			boolean defaultPlacement = true;

			EnumFacing placeDir = EnumFacing.NORTH;

			//TODO: Determination of special placement
			/*
		 	EnumFacing dir = side.getOpposite();
			EnumFacing placeDir = EnumFacing.NORTH;
			boolean defaultPlacement = false;

			if (dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
				defaultPlacement = true;
			} else {
				TileEntity ent = world.getTileEntity(pos.offset(dir));
				if (ent instanceof IRotatable) {
					EnumFacing otherDir = ((IRotatable) ent).getFacingDirection();
					if (otherDir == dir || otherDir == dir.getOpposite()) {
						placeDir = otherDir;
					} else {
						placeDir = dir;
					}
				} else if (ent.hasCapability(Taam.CAPABILITY_PIPE, dir)) {
					placeDir = dir;
				} else {
					defaultPlacement = true;
				}
			}
			 */

			if (defaultPlacement) {
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
			}

			((IRotatable) multipart.machine).setFacingDirection(placeDir);
		}

		return multipart;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int meta = stack.getMetadata();
		String[] ttip = MachineTileEntity.getInfo(meta).getTooltip();
		if (ttip != null) {
			Collections.addAll(tooltip, ttip);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = MachineTileEntity.getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> items) {
		Taam.MACHINE_META[] values = Taam.MACHINE_META.values();
		for (int i = 0; i < values.length; i++) {
			items.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}
}
