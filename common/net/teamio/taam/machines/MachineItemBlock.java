package net.teamio.taam.machines;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRotatable;

import java.util.List;

public class MachineItemBlock extends ItemBlock {

	private final IMachineMetaInfo[] values;

	public MachineItemBlock(Block block, IMachineMetaInfo[] values) {
		super(block);
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
		setHasSubtypes(true);
	}

	public IMachineMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public int getMetadata(int damage) {
		return 0;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);
		info.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {

		boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if (success) {
			MachineTileEntity te = (MachineTileEntity) world.getTileEntity(pos);

			int meta = stack.getMetadata();
			IMachineMetaInfo info = getInfo(meta);

			te.meta = info;
			te.machine = info.createMachine();
			te.markDirty();
			//TODO: world.markBlockForUpdate(pos);

			if (te.machine instanceof IRotatable) {

				boolean defaultPlacement = true;

				EnumFacing placeDir = EnumFacing.NORTH;

				// TODO: Determination of special placement

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
				((IRotatable) te.machine).setFacingDirection(placeDir);
			}
		}
		return success;
	}
}
