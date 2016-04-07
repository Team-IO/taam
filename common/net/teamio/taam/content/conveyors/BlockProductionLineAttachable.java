package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class BlockProductionLineAttachable extends BlockProductionLine {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.class);

	public BlockProductionLineAttachable() {
		super();
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = state.getValue(VARIANT);
		return variant.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		if (meta < 0 || meta > values.length) {
			meta = 0;
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}
		return super.getUnlocalizedName() + "." + values[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = (Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META) state
				.getValue(VARIANT);
		switch (variant) {
		case itembag:
			// Item Bag
			return new TileEntityConveyorItemBag();
		case trashcan:
			// Trash Can
			return new TileEntityConveyorTrashCan();
		}
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		this.minY = 0f;
		this.maxY = 0.5f;
		TileEntity te = world.getTileEntity(pos);

		if (state.getBlock() != this || !(te instanceof IRotatable)) {
			this.minX = 0;
			this.maxX = 1;
			this.minZ = 0;
			this.maxZ = 1;
			return;
		}

		EnumFacing facing = ((IRotatable) te).getFacingDirection();
		switch (facing) {
		default:
		case NORTH:
			this.minX = 0;
			this.maxX = 1;
			this.minZ = 0;
			this.maxZ = 0.35f;
			break;
		case SOUTH:
			this.minX = 0;
			this.maxX = 1;
			this.minZ = 0.65f;
			this.maxZ = 1;
			break;
		case EAST:
			this.minX = 0.65f;
			this.maxX = 1;
			this.minZ = 0;
			this.maxZ = 1;
			break;
		case WEST:
			this.minX = 0;
			this.maxX = 0.35f;
			this.minZ = 0;
			this.maxZ = 1;
			break;
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IRotatable) {
			return TaamUtil.canAttach(world, pos, ((IRotatable) te).getFacingDirection());
		} else {
			return true;
		}
	}

}
